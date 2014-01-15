package com.kt;

import com.kt.api.Action;
import com.kt.api.Push;
import com.kt.chesco.CHESCOReader;
import com.kt.chesco.CHESCOWriter;
import com.kt.game.*;
import com.kt.game.ChessPiece;
import com.kt.utils.ChessHeroException;
import com.kt.api.Result;
import com.kt.utils.SLog;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/9/13
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */

public class ClientConnection extends Thread implements GameClockEventListener
{
    private static final int NOAUTH_READ_TIMEOUT = 15 * 1000; // 15 seconds in milliseconds
	private static final int AUTH_READ_TIMEOUT = 30 * 60 * 1000; // 30 minutes in milliseconds

    private static final int DEFAULT_FETCH_GAMES_OFFSET = 0;
    private static final int DEFAULT_FETCH_GAMES_LIMIT = 100;
	private static final int MAX_FETCH_GAMES_LIMIT = 1000;

	private static final String DEFAULT_FETCH_GAMES_TYPE = "pending";

    private static final String DEFAULT_PLAYER_COLOR = "white";

	private static final HashMap<String, ClientConnection> playerConnections = new HashMap<String, ClientConnection>();
	private static final Lock playerConnectionsMutex = new ReentrantLock(true);

	private static final HashMap<Integer, Game> games = new HashMap<Integer, Game>();
	private static final Lock gamesMutex = new ReentrantLock(true);

	// Not using synchronized blocks because those use non-fair ReentrantLocks
	// Since these methods are frequently used by all threads, non-fair policy might lead to thread starvation
	private static void putPlayerConnection(int gameID, int userID, ClientConnection conn)
	{
		playerConnectionsMutex.lock();
		playerConnections.put(gameID + ":" + userID, conn);
		playerConnectionsMutex.unlock();
	}

	private static ClientConnection getPlayerConnection(int gameID, int userID)
	{
		playerConnectionsMutex.lock();
		ClientConnection conn = playerConnections.get(gameID + ":" + userID);
		playerConnectionsMutex.unlock();
		return conn;
	}

	private static ClientConnection popPlayerConnection(int gameID, int userID)
	{
		String key = gameID + ":" + userID;
		playerConnectionsMutex.lock();
		ClientConnection conn = playerConnections.get(key);
		playerConnections.remove(key);
		playerConnectionsMutex.unlock();
		return conn;
	}

	private static void addGame(Game game)
	{
		gamesMutex.lock();
		games.put(game.getID(), game);
		gamesMutex.unlock();
	}

	private static Game removeGame(int gameID)
	{
		gamesMutex.lock();
		Game game = games.get(gameID);
		games.remove(gameID);
		gamesMutex.unlock();
		return game;
	}

	private static Game getGame(int gameID)
	{
		gamesMutex.lock();
		Game game = games.get(gameID);
		gamesMutex.unlock();
		return game;
	}

	private static String generateChatToken(int gameID, int userID, String gameName) throws NoSuchAlgorithmException
	{
		String base = gameName + gameID + userID;

		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte tokenData[] = digest.digest(base.getBytes());

		Formatter formatter = new Formatter();

		for (byte b : tokenData)
		{
			formatter.format("%02x", b);
		}

		return formatter.toString();
	}

    private boolean running = true;
    private Socket sock = null;
	private int readTimeout = NOAUTH_READ_TIMEOUT;

    private CHESCOReader reader = null;
    private CHESCOWriter writer = null;

    private Database db = null;

    private Player player = null;

	private AtomicBoolean saveRequestUnresolved = new AtomicBoolean(false);
	private AtomicBoolean saveRequestAccepted = new AtomicBoolean(false);

    ClientConnection(Socket sock) throws IOException
    {
        this.sock = sock;
        db = new Database();

        reader = new CHESCOReader(sock.getInputStream());
        writer = new CHESCOWriter(sock.getOutputStream());
    }

    private void closeSocket()
    {
        SLog.write("Closing socket...");

        if (sock.isClosed())
        {
            SLog.write("Socket already closed");
            return;
        }

        try
        {
            sock.close();
            SLog.write("Socket closed");
        }
        catch (IOException ignore)
        {
        }
    }

    @Override
    public void run()
    {
        listen();

		try
		{
			if (null == player)
			{
				return;
			}

			Game game = null;

			if (null == (game = player.getGame()))
			{
				return;
			}

			synchronized (game)
			{
				int gameID = game.getID();
				short state = game.getState();

				if (Game.STATE_PENDING == state || Game.STATE_WAITING == state)
				{
					cancelGame(game);
					popPlayerConnection(gameID, player.getUserID());
				}
				else if (Game.STATE_ACTIVE == state || Game.STATE_PAUSED == state)
				{
					Player opponent = player.getOpponent();
					int opponentUserID = opponent.getUserID();

					popPlayerConnection(gameID, player.getUserID());
					ClientConnection opponentConnection = popPlayerConnection(gameID, opponentUserID);

					if (Game.STATE_PAUSED == state && opponentConnection != null)
					{
						opponentConnection.saveRequestAccepted.set(false);
						opponentConnection.saveRequestUnresolved.set(false);
					}

					// End the game with the opponent as the winner
					game.getController().endGame(opponent, false);
					finalizeGame(game);

					if (opponentConnection != null)
					{
						HashMap push = aPushWithEvent(Push.GAME_END);
						push.put("winner", opponentUserID);
						push.put("opponentdisconnected", true);

						opponentConnection.writeMessage(push);
					}
				}
			}
		}
		finally
		{
			db.disconnect();
			closeSocket();
		}
    }

    private void listen()
    {
		while (running)
		{
			try
			{
				sock.setSoTimeout(readTimeout);

				Object request = reader.read();
				handleRequest(request);
			}
			catch (InputMismatchException e)
			{
				SLog.write("Message not conforming to CHESCO");
				writeMessage(aResponseWithResult(Result.INVALID_REQUEST_FORMAT));
				running = false;
			}
			catch (SocketTimeoutException e)
			{
				SLog.write("Read timed out");
				running = false;
			}
			catch (EOFException e)
			{
				SLog.write("Client closed connection: " + e);
				running = false;
			}
			catch (IOException e)
			{
				SLog.write("Error reading: " + e);
				running = false;
			}
			catch (ChessHeroException e)
			{
				int code = e.getCode();
				SLog.write("Chess hero exception: " + code);
				writeMessage(aResponseWithResult(code));
				running = false;
			}
			catch (Exception e)
			{
				SLog.write("Surprise exception: " + e);
				e.printStackTrace();
				writeMessage(aResponseWithResult(Result.INTERNAL_ERROR));
				running = false;
			}
		}
    }

    private HashMap<String, Object> aResponseWithResult(int result)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("result", result);
        return map;
    }

    private HashMap<String, Object> aPushWithEvent(int event)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("push", true);
		map.put("event", event);
        return map;
    }

	private void cancelGame(Game game)
	{
		int gID = game.getID();

		removeGame(gID);
		player.leave();

		try
		{
			db.connect();
			db.startTransaction();

			int userID = player.getUserID();

			db.deleteGame(gID);
			db.deletePlayer(gID, userID);
			db.deleteChatEntry(gID, userID);

			db.commit();
		}
		catch (SQLException e)
		{
			SLog.write("MANUAL CLEANUP REQUIRED FOR GAME WITH ID: " + game.getID() + " AND PLAYER: " + player);
			e.printStackTrace();

			try
			{
				db.rollback();
			}
			catch(SQLException ignore)
			{
			}
		}
		finally
		{
			db.disconnect();
		}
	}

	private void finalizeGame(Game game)
	{
		int gameID = game.getID();

		Player winner = game.getWinner();
		Player loser = (winner != null ? winner.getOpponent() : null);

		game.getPlayer1().leave();
		game.getPlayer2().leave();

		removeGame(gameID);

		try
		{
			db.connect();
			db.startTransaction();

			db.deleteGame(gameID);
			db.deletePlayersForGame(gameID);
			db.deleteChatEntriesForGame(gameID);

			if (winner != null)
			{
				db.insertResult(gameID, winner.getUserID(), loser.getUserID(), game.isCheckmate());
			}
			else if (!game.wasSaved())
			{	// Draw
				db.insertResult(gameID);
			}

			db.commit();
		}
		catch (SQLException e)
		{
			SLog.write("MANUAL CLEANUP REQUIRED FOR GAME WITH ID: " + game.getID() + " AND PLAYERS: " + player + ", " + player.getOpponent());
			e.printStackTrace();

			try
			{
				db.rollback();
			}
			catch (SQLException ignore)
			{
			}
		}
		finally
		{
			db.disconnect();
		}
	}

    private synchronized void writeMessage(HashMap message)
    {
        try
        {
            SLog.write("Writing message: " + message + " ...");
            writer.write(message);
            SLog.write("Message written");
        }
        catch (Exception e)
        {
            SLog.write("Exception raised while writing: " + e);
            running = false;
        }
    }

    private void handleRequest(Object aRequest) throws ChessHeroException
    {
		SLog.write("Request received: " + aRequest);

        try
        {
			if (!(aRequest instanceof Map))
			{   // Map is the only type of object the server allows for sending requests
				writeMessage(aResponseWithResult(Result.WRONG_REQUEST_FORMAT));
				return;
			}

			HashMap<String, Object> request = (HashMap<String, Object>)aRequest;

            Integer action = (Integer)request.get("action");

            if (null == action)
            {
                writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
                return;
            }

            if (null == player && action != Action.LOGIN && action != Action.REGISTER)
            {
                SLog.write("Client attempting unauthorized action");
                throw new ChessHeroException(Result.AUTH_REQUIRED);
            }

			if (saveRequestUnresolved.get() && action != Action.SAVE_GAME)
			{
				writeMessage(aResponseWithResult(Result.ACTION_DISABLED));
				return;
			}

            switch (action)
            {
                case Action.LOGIN:
                    handleLogin(request);
                    break;

                case Action.REGISTER:
                    handleRegister(request);
                    break;

                case Action.CREATE_GAME:
                    handleCreateGame(request);
                    break;

                case Action.CANCEL_GAME:
                    handleCancelGame(request);
                    break;

                case Action.FETCH_GAMES:
                    handleFetchGames(request);
                    break;

                case Action.JOIN_GAME:
                    handleJoinGame(request);
                    break;

                case Action.EXIT_GAME:
                    handleExitGame(request);
                    break;

				case Action.MOVE:
					handleMove(request);
					break;

				case Action.SAVE_GAME:
					handleSaveGame(request);
					break;

				case Action.DELETE_SAVED_GAME:
					handleDeleteSavedGame(request);
					break;

				case Action.RESUME_GAME:
					handleResumeGame(request);
					break;

                default:
                    throw new ChessHeroException(Result.UNRECOGNIZED_ACTION);
            }
        }
        catch (ClassCastException e)
        {
            SLog.write("A request parameter is not of the appropriate type");
            writeMessage(aResponseWithResult(Result.INVALID_PARAM));
        }
    }

    private void handleRegister(HashMap<String, Object> request) throws ChessHeroException
    {
        if (player != null)
        {
            writeMessage(aResponseWithResult(Result.ALREADY_LOGGEDIN));
            return;
        }

        String name = (String)request.get("username");
        String pass = (String)request.get("password");

        if (null == name || null == pass)
        {
            writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        if (!Credentials.isNameValid(name))
        {
            writeMessage(aResponseWithResult(Result.INVALID_NAME));
            return;
        }

        if (Credentials.isBadUser(name))
        {
            writeMessage(aResponseWithResult(Result.BAD_USER));
            return;
        }

        if (!Credentials.isPassValid(pass))
        {
            writeMessage(aResponseWithResult(Result.INVALID_PASS));
            return;
        }

        try
        {
            db.connect();

            if (db.userExists(name))
            {
                writeMessage(aResponseWithResult(Result.USER_EXISTS));
                return;
            }

            int salt = Credentials.generateSalt();
            String passHash = Credentials.saltAndHash(pass, salt);

            db.insertUser(name, passHash, salt);

            int userID = db.getUserID(name);

            if (-1 == userID)
            {   // Could not fetch the user id
                throw new ChessHeroException(Result.INTERNAL_ERROR);
            }

            player = new Player(userID, name);
			readTimeout = AUTH_READ_TIMEOUT;

            HashMap response = aResponseWithResult(Result.OK);
            response.put("username", name);
            response.put("userid", userID);
            writeMessage(response);
        }
        catch (SQLException e)
        {
            SLog.write("Registration failed: " + e);
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        catch (NoSuchAlgorithmException e)
        {
            SLog.write("Registration failed: " + e);
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        finally
        {
            db.disconnect();
        }
    }

    private void handleLogin(HashMap<String, Object> request) throws ChessHeroException
    {
        if (player != null)
        {
            writeMessage(aResponseWithResult(Result.ALREADY_LOGGEDIN));
            return;
        }

        String name = (String)request.get("username");
        String pass = (String)request.get("password");

        if (null == name || null == pass)
        {
            writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        try
        {
            db.connect();

            AuthPair auth = db.getAuthPair(name);

            if (null == auth || !auth.matches(pass))
            {
                writeMessage(aResponseWithResult(Result.INVALID_CREDENTIALS));
                return;
            }

            int userID = db.getUserID(name);

            if (-1 == userID)
            {
                throw new ChessHeroException(Result.INTERNAL_ERROR);
            }

            player = new Player (userID, name);
			readTimeout = AUTH_READ_TIMEOUT;

            HashMap response = aResponseWithResult(Result.OK);
            response.put("username", name);
            response.put("userid", userID);

            writeMessage(response);
        }
        catch (SQLException e)
        {
            SLog.write("Logging failed: " + e);
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        catch (NoSuchAlgorithmException e)
        {
            SLog.write("Logging failed: " + e);
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        finally
        {
            db.disconnect();
        }
    }

    private void handleCreateGame(HashMap<String, Object> request) throws ChessHeroException
    {
        if (player.getGame() != null)
        {
            writeMessage(aResponseWithResult(Result.ALREADY_PLAYING));
            return;
        }

        String gameName = (String)request.get("gamename");

        if (null == gameName)
        {
            writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        if (!Game.isGameNameValid(gameName))
        {
            writeMessage(aResponseWithResult(Result.INVALID_GAME_NAME));
            return;
        }

		Integer timeout = (Integer)request.get("timeout");

		if (null == timeout)
		{
			timeout = Game.DEFAULT_TIMEOUT;
		}
		else if (timeout < Game.MIN_TIMEOUT || timeout > Game.MAX_TIMEOUT)
		{
			writeMessage(aResponseWithResult(Result.INVALID_TIMEOUT));
			return;
		}

		String color = (String)request.get("color");

        if (null == color || Color.NONE == Color.fromString(color))
        {
            color = DEFAULT_PLAYER_COLOR;
        }

        try
        {
            db.connect();
			db.startTransaction();

            int gameID = db.insertGame(gameName, Game.STATE_PENDING, timeout);

            if (-1 == gameID)
            {
                throw new ChessHeroException(Result.INTERNAL_ERROR);
            }

			int userID = player.getUserID();
			String chatToken = generateChatToken(gameID, userID, gameName);
			db.insertPlayer(gameID, userID, color);
			db.insertChatEntry(gameID, userID, chatToken);

			db.commit();

			putPlayerConnection(gameID, userID, this);

			Game game = new Game(gameID, gameName, timeout);
			game.setState(Game.STATE_PENDING);
			player.join(game, Color.fromString(color));
			GameClock clock = game.getClock();

			if (clock != null)
			{
				clock.addEventListener(this);
			}

			addGame(game);

            HashMap response = aResponseWithResult(Result.OK);
            response.put("gameid", gameID);
			response.put("chattoken", chatToken);
            writeMessage(response);
        }
		catch (NoSuchAlgorithmException e)
		{
			SLog.write("Exception raised while generating chat token for new game: " + e);
			e.printStackTrace();

			try
			{
				db.rollback();
			}
			catch (SQLException ignore)
			{
			}

			throw new ChessHeroException(Result.INTERNAL_ERROR);
		}
        catch (SQLException e)
        {
			SLog.write("Exception raised while creating game: " + e);
			e.printStackTrace();

			try
			{
				db.rollback();
			}
			catch (SQLException ignore)
			{
			}

            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        finally
        {
            db.disconnect();
        }
    }

    private void handleCancelGame(HashMap<String, Object> request) throws ChessHeroException
    {
        Game game = player.getGame();

        if (null == game)
        {
            writeMessage(aResponseWithResult(Result.NOT_PLAYING));
            return;
        }

        Integer gameIDToDelete = (Integer)request.get("gameid");

        if (null == gameIDToDelete)
        {
            writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        // Using flags to write any errors outside of the synchronized block so that the
        // lock on the object is not prolonged so much by IO operations
        boolean isInGame = false;
        boolean invalidGameID = false;

        synchronized (game)
        {
			short state = game.getState();
			int gameID = game.getID();

			if (!(invalidGameID = gameIDToDelete != gameID) && !(isInGame = Game.STATE_PENDING == state || Game.STATE_WAITING == state))
			{
				popPlayerConnection(gameID, player.getUserID());
				cancelGame(game);
			}
        }

        if (isInGame)
        {
            writeMessage(aResponseWithResult(Result.CANCEL_NA));
            return;
        }

        if (invalidGameID)
        {
            writeMessage(aResponseWithResult(Result.INVALID_GAME_ID));
            return;
        }

        writeMessage(aResponseWithResult(Result.OK));
    }

    private void handleFetchGames(HashMap<String, Object> request) throws ChessHeroException
    {
		String type = (String)request.get("type");
        Integer offset = (Integer)request.get("offset");
        Integer limit = (Integer)request.get("limit");

		if (null == type)
		{
			type = DEFAULT_FETCH_GAMES_TYPE;
		}
        if (null == offset || offset < 0)
        {
            offset = DEFAULT_FETCH_GAMES_OFFSET;
        }
        if (null == limit || limit < 0 || limit > MAX_FETCH_GAMES_LIMIT)
        {
            limit = DEFAULT_FETCH_GAMES_LIMIT;
        }

        try
        {
            db.connect();

			ArrayList games = null;

			if (type.equals("saved"))
			{
				games = db.getSavedGamesWithOpponentsForUser(player.getUserID(), offset, limit);
			}
			else
			{
				games = db.getGamesAndPlayerInfo(Game.STATE_PENDING, offset, limit);
			}

            HashMap response = aResponseWithResult(Result.OK);
            response.put("games", games);
            writeMessage(response);
        }
        catch (SQLException e)
        {
			SLog.write("Exception raised while fetching games: " + e);
			e.printStackTrace();

            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        finally
        {
            db.disconnect();
        }
    }

    private void handleJoinGame(HashMap<String, Object> request) throws ChessHeroException
    {
        if (player.getGame() != null)
        {
            writeMessage(aResponseWithResult(Result.ALREADY_PLAYING));
            return;
        }

        Integer joinGameID = (Integer)request.get("gameid");

        if (null == joinGameID)
        {
            writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        int gameID = joinGameID.intValue();

        Game game = getGame(gameID);

        if (null == game)
        {
            writeMessage(aResponseWithResult(Result.INVALID_GAME_ID));
            return;
        }

        // Using flags to write any errors outside of the synchronized block so that the
        // lock on the object is not prolonged so much by IO operations
        boolean gameOccupied = false;
        boolean sameUser = false;

        Player opponent = null;
        String myChatToken = null;

        synchronized (game)
        {
            if (!(gameOccupied = game.getState() != Game.STATE_PENDING))
            {
                String gameName = game.getName();
                opponent = game.getPlayer1();

                int myUserID = player.getUserID();
                int opponentUserID = opponent.getUserID();

                if (!(sameUser = myUserID == opponentUserID))
                {
                    try
                    {
                        db.connect();
                        db.startTransaction();

						Color myColor = opponent.getColor().Opposite;
						myChatToken = generateChatToken(gameID, myUserID, gameName);

                        db.updateGameState(gameID, Game.STATE_ACTIVE);
                        db.insertPlayer(gameID, myUserID, myColor.toString());
						db.insertChatEntry(gameID, myUserID, myChatToken);

                        db.commit();

						putPlayerConnection(gameID, myUserID, this);

                        player.join(game, myColor);
						GameClock clock = game.getClock();

						if (clock != null)
						{
							clock.addEventListener(this);
						}

                        GameController controller = new GameController(game);
						controller.startGame();
                    }
                    catch (Exception e)
                    {
                        SLog.write("Exception raised while joining game: " + e);
						e.printStackTrace();

                        try
                        {
                            db.rollback();
                        }
                        catch (SQLException ignore)
                        {
                        }

                        throw new ChessHeroException(Result.INTERNAL_ERROR);
                    }
                    finally
                    {
                        db.disconnect();
                    }
                }
            }
        }

        if (gameOccupied)
        {
            writeMessage(aResponseWithResult(Result.GAME_OCCUPIED));
            return;
        }

        if (sameUser)
        {
            writeMessage(aResponseWithResult(Result.DUPLICATE_PLAYER));
            return;
        }

        HashMap myMsg = aResponseWithResult(Result.OK);
		myMsg.put("chattoken", myChatToken);
        writeMessage(myMsg);

		ClientConnection opponentConnection = getPlayerConnection(gameID, opponent.getUserID());
		if (opponentConnection != null)
		{
			HashMap msg = aPushWithEvent(Push.GAME_JOIN);
			msg.put("opponentname", player.getName());
			msg.put("opponentid", player.getUserID());

			opponentConnection.writeMessage(msg);
		}
    }

    private void handleExitGame(HashMap<String, Object> request) throws ChessHeroException
    {
        Game game = player.getGame();

        if (null == game)
        {
            writeMessage(aResponseWithResult(Result.NOT_PLAYING));
            return;
        }

        Integer gameID = (Integer)request.get("gameid");

        if (null == gameID)
        {
            writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        boolean invalidGameID = false;
        boolean gameHasNotStarted = false;

		ClientConnection opponentConnection = null;
		int opponentUserID = 0;

        synchronized (game)
        {
            if (!(invalidGameID = game.getID() != gameID) && !(gameHasNotStarted = game.getState() != Game.STATE_ACTIVE))
            {
				Player opponent = player.getOpponent();
				opponentUserID = opponent.getUserID();

				// End the game with the opponent as the winner
				game.getController().endGame(opponent, false);

				popPlayerConnection(gameID, player.getUserID());
				opponentConnection = popPlayerConnection(gameID, opponentUserID);

				finalizeGame(game);
			}
        }

        if (invalidGameID)
        {
            writeMessage(aResponseWithResult(Result.INVALID_GAME_ID));
            return;
        }

        if (gameHasNotStarted)
        {
            writeMessage(aResponseWithResult(Result.EXIT_NA));
            return;
        }

		HashMap myMsg = aResponseWithResult(Result.OK);
		myMsg.put("winner", opponentUserID);
        writeMessage(myMsg);

		if (opponentConnection != null)
		{
			HashMap msg = aPushWithEvent(Push.GAME_END);
			msg.put("winner", opponentUserID);
			msg.put("opponentexited", true);

			opponentConnection.writeMessage(msg);
		}
    }

	private void handleMove(HashMap<String, Object> request) throws ChessHeroException
	{
		Game game = player.getGame();

		if (null == game)
		{
			writeMessage(aResponseWithResult(Result.NOT_PLAYING));
			return;
		}

		String move = (String)request.get("move");

		if (null == move)
		{
			writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
			return;
		}

		boolean gameNotStarted = false;
		int result = 0;
		boolean gameFinished = false;
		ArrayList<ChessPiece> attackers = null;
		Player winner = null;
		ClientConnection opponentConnection = null;

		synchronized (game)
		{
			if (!(gameNotStarted = game.getState() != Game.STATE_ACTIVE))
			{
				GameController controller = game.getController();

				result = controller.execute(player, move);

				if (Result.OK == result)
				{
					attackers = game.getAttackers();
					gameFinished = Game.STATE_FINISHED == game.getState();

					int gameID = game.getID();
					int opponentUserID = player.getOpponent().getUserID();

					opponentConnection = getPlayerConnection(gameID, opponentUserID);

					if (gameFinished)
					{
						winner = game.getWinner();

						popPlayerConnection(gameID, player.getUserID());
						popPlayerConnection(gameID, opponentUserID);

						finalizeGame(game);
					}
				}
			}
		}

		if (gameNotStarted)
		{
			writeMessage(aResponseWithResult(Result.MOVE_NA));
			return;
		}

		writeMessage(aResponseWithResult(result));

		if (result != Result.OK)
		{
			return;
		}

		try
		{
			db.connect();
			db.insertMove(game.getID(), player.getUserID(), move);
		}
		catch (SQLException ignore)
		{
			SLog.write("Exception raised while inserting move: " + ignore);
			ignore.printStackTrace();
		}
		finally
		{
			db.disconnect();
		}

		if (opponentConnection != null)
		{
			HashMap msg = aPushWithEvent(Push.GAME_MOVE);
			msg.put("move", move);

			if (attackers != null)
			{
				ArrayList<String> positions = new ArrayList<String>(attackers.size());

				for (ChessPiece attacker : attackers)
				{
					positions.add(Position.boardPositionFromPosition(attacker.getPosition()));
				}

				msg.put("attackers", positions);
			}

			opponentConnection.writeMessage(msg);
		}

		if (!gameFinished)
		{
			return;
		}

		HashMap endMsg = aPushWithEvent(Push.GAME_END);

		if (winner != null)
		{	// winner will be null when game is draw
			endMsg.put("winner", winner.getUserID());
		}

		writeMessage(endMsg);

		if (opponentConnection != null)
		{
			opponentConnection.writeMessage(endMsg);
		}
	}

	private void handleSaveGame(HashMap<String, Object> request) throws ChessHeroException
	{
		Game game = player.getGame();

		if (null == game)
		{
			writeMessage(aResponseWithResult(Result.NOT_PLAYING));
			return;
		}

		Integer gameID = (Integer)request.get("gameid");

		if (null == gameID)
		{
			writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
			return;
		}

		Boolean save = (Boolean)request.get("save");

		if (null == save)
		{
			save = true;
		}

		ClientConnection opponentConnection = null;
		boolean gameNotActive = false;
		boolean invalidGameID = false;
		boolean prompt = false;

		synchronized (game)
		{
			if (!(invalidGameID = game.getID() != gameID))
			{
				short state = game.getState();

				if (Game.STATE_ACTIVE == state)
				{
					if (save)
					{
						opponentConnection = getPlayerConnection(gameID, player.getOpponent().getUserID());

						if (null == opponentConnection)
						{
							throw new ChessHeroException(Result.INTERNAL_ERROR);
						}

						game.setState(Game.STATE_PAUSED);
						opponentConnection.saveRequestUnresolved.set(true);
						prompt = true;
					}
				}
				else if (Game.STATE_PAUSED == state)
				{
					if (save)
					{
						try
						{
							int myUserID = player.getUserID();
							int opponentUserID = player.getOpponent().getUserID();
							boolean iAmNext = player.equals(game.getTurn());

							db.connect();
							db.startTransaction();

							db.insertGameSave(gameID, game.getName(), game.toData(), game.getTimeout());
							db.insertSavedGamePlayer(gameID, myUserID, player.getColor().toString(), iAmNext);
							db.insertSavedGamePlayer(gameID, opponentUserID, player.getOpponent().getColor().toString(), !iAmNext);

							db.commit();

							game.getController().endGame(null, false, true);

							popPlayerConnection(gameID, player.getUserID());
							popPlayerConnection(gameID, player.getOpponent().getUserID());

							finalizeGame(game);
						}
						catch (SQLException e)
						{
							SLog.write("Exception raised while saving game: " + e);
							e.printStackTrace();

							try
							{
								db.rollback();
							}
							catch (SQLException ignore)
							{
							}

							// Revert
							game.setState(Game.STATE_ACTIVE);
							saveRequestAccepted.set(false);
							saveRequestUnresolved.set(false);

							throw new ChessHeroException(Result.INTERNAL_ERROR);
						}
						finally
						{
							db.disconnect();
						}
					}
					else
					{
						game.setState(Game.STATE_ACTIVE);
					}

					saveRequestAccepted.set(save);
					saveRequestUnresolved.set(false);
				}
				else
				{
					gameNotActive = true;
				}
			}
		}

		if (gameNotActive)
		{
			writeMessage(aResponseWithResult(Result.SAVE_NA));
			return;
		}

		if (invalidGameID)
		{
			writeMessage(aResponseWithResult(Result.INVALID_GAME_ID));
			return;
		}

		boolean saved;

		if (prompt)
		{
			opponentConnection.writeMessage(aPushWithEvent(Push.GAME_SAVE));

			while (opponentConnection.saveRequestUnresolved.get())
			{
				try { Thread.sleep(2); } catch (InterruptedException ignore) {}
			}

			saved = opponentConnection.saveRequestAccepted.get();
		}
		else
		{
			saved = saveRequestAccepted.get();
		}

		HashMap response = aResponseWithResult(Result.OK);
		response.put("saved", saved);
		writeMessage(response);

		if (saved)
		{
			writeMessage(aPushWithEvent(Push.GAME_END));
		}
	}

	private void handleDeleteSavedGame(HashMap<String, Object> request) throws ChessHeroException
	{
		Integer gameID = (Integer)request.get("gameid");

		if (null == gameID)
		{
			writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
			return;
		}

		boolean invalidGameID;

		try
		{
			db.connect();
			db.startTransaction();

			invalidGameID = !db.isUserPresentInSavedGame(gameID, player.getUserID());

			if (!invalidGameID)
			{
				db.deleteSavedGame(gameID);
				db.deletePlayersForSavedGame(gameID);
			}

			db.commit();
		}
		catch (SQLException e)
		{
			SLog.write("Exception raised while deleting saved game: " + e);
			e.printStackTrace();

			try
			{
				db.rollback();
			}
			catch (SQLException ignore)
			{
			}

			throw new ChessHeroException(Result.INTERNAL_ERROR);
		}
		finally
		{
			db.disconnect();
		}

		if (invalidGameID)
		{
			writeMessage(aResponseWithResult(Result.INVALID_GAME_ID));
			return;
		}

		writeMessage(aResponseWithResult(Result.OK));
	}

	private void handleResumeGame(HashMap<String, Object> request) throws ChessHeroException
	{
		if (player.getGame() != null)
		{
			writeMessage(aResponseWithResult(Result.ALREADY_PLAYING));
			return;
		}

		Integer gameID = (Integer)request.get("gameid");

		if (null == gameID)
		{
			writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
			return;
		}

		int myUserID = player.getUserID();
		boolean success = false;

		try
		{
			db.connect();

			if (!db.isUserPresentInSavedGame(gameID, myUserID))
			{
				writeMessage(aResponseWithResult(Result.INVALID_GAME_ID));
				return;
			}

			HashMap gameInfo = db.getGameSave(gameID);

			if (null == gameInfo)
			{
				SLog.write("Raising exception: no game info");
				throw new ChessHeroException(Result.INTERNAL_ERROR);
			}

			ArrayList<HashMap> playersInfo = db.getSavedGamePlayers(gameID);

			if (null == playersInfo || playersInfo.size() != 2)
			{
				SLog.write("Raising exception: no players info");
				throw new ChessHeroException(Result.INTERNAL_ERROR);
			}

			HashMap firstPlayer = playersInfo.get(0);
			HashMap secondPlayer = playersInfo.get(1);
			HashMap myPlayerInfo = firstPlayer.get("id").equals(myUserID) ? firstPlayer : secondPlayer;
			HashMap opponentPlayerInfo = firstPlayer == myPlayerInfo ? secondPlayer : firstPlayer;

			Game game = null;
			byte gameData[] = (byte[])gameInfo.get("gdata");
			String gameName = (String)gameInfo.get("gname");

			gamesMutex.lock();

			if (null == (game = games.get(gameID)))
			{
				game = new Game(gameID, gameName, (Integer)gameInfo.get("timeout"), gameData);
				games.put(gameID, game);
			}

			gamesMutex.unlock();

			boolean gameOccupied = false;
			boolean duplicateUser = false;
			String color = (String)myPlayerInfo.get("color");
			String token = null;
			String gameDataEncoded = null;
			ClientConnection opponentConnection = null;
			boolean join = false;

			synchronized (game)
			{
				short state = game.getState();

				try
				{
					token = generateChatToken(gameID, myUserID, gameName);
					gameDataEncoded = new String(gameData, "UTF-8");
				}
				catch (Exception e)
				{
					SLog.write("Exception raised while resuming game: " + e);
					e.printStackTrace();

					throw new ChessHeroException(Result.INTERNAL_ERROR);
				}

				if (Game.STATE_INIT == state)
				{
					try
					{
						db.startTransaction();

						db.insertGame(gameID, gameName, Game.STATE_WAITING);
						db.insertPlayer(gameID, myUserID, color);
						db.insertChatEntry(gameID, myUserID, token);

						db.commit();
					}
					catch (SQLException e)
					{
						SLog.write("Exception raised while resuming game: " + e);
						e.printStackTrace();

						try
						{
							db.rollback();
						}
						catch (SQLException ignore)
						{
						}

						throw new ChessHeroException(Result.INTERNAL_ERROR);
					}

					putPlayerConnection(gameID, myUserID, this);
					game.setState(Game.STATE_WAITING);
					player.join(game, Color.fromString(color));
					GameClock clock = game.getClock();

					if (clock != null)
					{
						clock.addEventListener(this);
					}
				}
				else if (Game.STATE_WAITING == state)
				{
					if (!(duplicateUser = game.getPlayer1().equals(player)))
					{
						opponentConnection = getPlayerConnection(gameID, (Integer)opponentPlayerInfo.get("id"));

						if (null == opponentConnection)
						{
							SLog.write("Raising exception: no opponent connection");
							throw new ChessHeroException(Result.INTERNAL_ERROR);
						}

						try
						{
							db.startTransaction();

							db.insertPlayer(gameID, myUserID, color);
							db.insertChatEntry(gameID, myUserID, token);
							db.updateGameState(gameID, Game.STATE_ACTIVE);
							db.deleteSavedGame(gameID);
							db.deletePlayersForSavedGame(gameID);

							db.commit();
						}
						catch (SQLException e)
						{
							SLog.write("Exception raised while resuming game: " + e);
							e.printStackTrace();

							try
							{
								db.rollback();
							}
							catch (SQLException ignore)
							{
							}

							throw new ChessHeroException(Result.INTERNAL_ERROR);
						}

						putPlayerConnection(gameID, myUserID, this);
						player.join(game, Color.fromString(color));
						GameClock clock = game.getClock();

						if (clock != null)
						{
							clock.addEventListener(this);
						}

						Boolean myTurn = (Boolean)myPlayerInfo.get("next");
						new GameController(game).startGame(myTurn ? player : player.getOpponent());
						join = true;
					}
				}
				else
				{
					gameOccupied = true;
				}
			}

			if (gameOccupied)
			{
				writeMessage(aResponseWithResult(Result.GAME_OCCUPIED));
				return;
			}

			if (duplicateUser)
			{
				writeMessage(aResponseWithResult(Result.DUPLICATE_PLAYER));
				return;
			}

			HashMap response = aResponseWithResult(Result.OK);
			response.put("game", gameDataEncoded);
			response.put("chattoken", token);
			response.put("next", (Boolean)myPlayerInfo.get("next"));
			response.put("started", join);
			writeMessage(response);

			if (join)
			{
				HashMap push = aPushWithEvent(Push.GAME_JOIN);
				push.put("opponentname", player.getName());
				push.put("opponentid", player.getUserID());
				opponentConnection.writeMessage(push);
			}

			success = true;
		}
		catch (SQLException e)
		{
			SLog.write("Exception raised while resuming game: " + e);
			e.printStackTrace();
		}
		finally
		{
			db.disconnect();

			if (!success)
			{
				removeGame(gameID);
			}
		}
	}

	@Override
	public void playerDidTimeout(Player player)
	{

	}
}

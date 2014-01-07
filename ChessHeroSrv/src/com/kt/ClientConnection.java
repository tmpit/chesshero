package com.kt;

import com.kt.api.Action;
import com.kt.api.Push;
import com.kt.chesco.CHESCOReader;
import com.kt.chesco.CHESCOWriter;
import com.kt.game.*;
import com.kt.game.chesspieces.ChessPiece;
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

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/9/13
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */

public class ClientConnection extends Thread
{
    private static final int READ_TIMEOUT = 15 * 1000; // In milliseconds

    private static final int DEFAULT_FETCH_GAMES_OFFSET = 0;
    private static final int DEFAULT_FETCH_GAMES_LIMIT = 100;
	private static final int MAX_FETCH_GAMES_LIMIT = 1000;

    private static final String DEFAULT_PLAYER_COLOR = "white";

	private static final HashMap<Integer, Game> games = new HashMap<Integer, Game>();
	private static final HashMap<String, ClientConnection> playerConnections = new HashMap<String, ClientConnection>();

	private static synchronized void putConnection(int gameID, int userID, ClientConnection conn)
	{
		playerConnections.put(gameID + ":" + userID, conn);
	}

	private static synchronized ClientConnection getConnection(int gameID, int userID)
	{
		return playerConnections.get(gameID + ":" + userID);
	}

	private static synchronized ClientConnection popConnection(int gameID, int userID)
	{
		String key = gameID + ":" + userID;
		ClientConnection conn = playerConnections.get(key);
		playerConnections.remove(key);
		return conn;
	}

	private static synchronized void addGame(Game game)
	{
		games.put(game.getID(), game);
	}

	private static synchronized Game removeGame(int gameID)
	{
		Game game = games.get(gameID);
		games.remove(gameID);
		return game;
	}

	private static synchronized Game getGame(int gameID)
	{
		return games.get(gameID);
	}

	private static String generateChatToken(int gameID, int userID, String gameName) throws NoSuchAlgorithmException
	{
		String cat1 = gameName + gameID + userID;

		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte tokenData[] = digest.digest(cat1.getBytes());

		Formatter formatter = new Formatter();

		for (byte b : tokenData)
		{
			formatter.format("%02x", b);
		}

		return formatter.toString();
	}

    private boolean running = true;
    private Socket sock = null;
	private int readTimeout = READ_TIMEOUT;

    private CHESCOReader reader = null;
    private CHESCOWriter writer = null;

    private Database db = null;

    private Player player = null;

	private boolean saveGamePrompted = false;
	private boolean gameSaved;

    ClientConnection(Socket sock) throws IOException
    {
        this.sock = sock;
        db = new Database();

        reader = new CHESCOReader(sock.getInputStream());
        writer = new CHESCOWriter(sock.getOutputStream());
    }

	private synchronized boolean isSaveGamePrompted()
	{
		return saveGamePrompted;
	}

	private synchronized void setSaveGamePrompted(boolean flag)
	{
		saveGamePrompted = flag;
	}

	private synchronized boolean isGameSaved()
	{
		return gameSaved;
	}

	private synchronized void setGameSaved(boolean flag)
	{
		gameSaved = flag;
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

        Game game = null;

        if (player != null && (game = player.getGame()) != null)
        {
            synchronized (game)
            {
				int gameID = game.getID();

                if (Game.STATE_PENDING == game.getState())
                {
					cancelGame(game);
					popConnection(gameID, player.getUserID());
                }
                else if (Game.STATE_ACTIVE == game.getState())
                {
					Player opponent = player.getOpponent();
					int opponentUserID = opponent.getUserID();
					ClientConnection opponentConnection = null;

					// End the game with the opponent as the winner
					game.getController().endGame(opponent);
					finalizeGame(game);
					popConnection(gameID, player.getUserID());
					opponentConnection = popConnection(gameID, opponentUserID);

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

		db.disconnect();
        closeSocket();
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
				handleTimeout();
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

			db.deleteGame(gID);
			db.removePlayer(gID, player.getUserID());

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
		Player loser = winner.getOpponent();

		winner.leave();
		loser.leave();

		removeGame(gameID);

		try
		{
			db.connect();
			db.startTransaction();

			db.deleteGame(gameID);
			db.removePlayersForGame(gameID);

			if (null == winner)
			{	// Draw
				db.insertResult(gameID);
			}
			else
			{
				db.insertResult(gameID, winner.getUserID(), loser.getUserID());
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

    private synchronized void writeMessage(HashMap<String, Object> message)
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

	private void handleTimeout()
	{
		Game game = null;

		if (null == player)
		{	// Login / Register timeout
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

			if (isSaveGamePrompted() && action != Action.SAVE_GAME)
			{
				writeMessage(aResponseWithResult(Result.ACTION_DISABLED));
				return;
			}

            switch (action.intValue())
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
			readTimeout = 0;

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
			readTimeout = 0;

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

        String color = (String)request.get("color");

        if (null == color || (!color.equals("white") && !color.equals("black")))
        {
            color = DEFAULT_PLAYER_COLOR;
        }

        try
        {
            db.connect();
			db.startTransaction();

            int gameID = db.insertGame(gameName, Game.STATE_PENDING);

            if (-1 == gameID)
            {
                throw new ChessHeroException(Result.INTERNAL_ERROR);
            }

			int userID = player.getUserID();
			String chatToken = generateChatToken(gameID, userID, gameName);
			db.insertPlayer(gameID, userID, chatToken, color);

			db.commit();

			putConnection(gameID, userID, this);

			Game game = new Game(gameID, gameName);
			game.setState(Game.STATE_PENDING);
			player.join(game, (color.equals("white") ? Color.WHITE : Color.BLACK));

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
            if (!(isInGame = game.getState() != Game.STATE_PENDING))
            {
                int gID = gameIDToDelete.intValue();

                if (!(invalidGameID = game.getID() != gID))
                {
					popConnection(gID, player.getUserID());
                    cancelGame(game);
                }
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
        Integer offset = (Integer)request.get("offset");
        Integer limit = (Integer)request.get("limit");

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

            ArrayList games = db.getGamesAndPlayerInfo(Game.STATE_PENDING, offset, limit);

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
                        myChatToken = generateChatToken(gameID, myUserID, gameName);

                        db.connect();
                        db.startTransaction();

						Color opponentColor = opponent.getColor();
						Color myColor = opponentColor.Opposite;

                        db.updateGameState(gameID, Game.STATE_ACTIVE);
                        db.insertPlayer(gameID, myUserID, myChatToken, (myColor == Color.WHITE ? "white" : "black"));

                        db.commit();

						putConnection(gameID, myUserID, this);

                        player.join(game, myColor);

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

		int opponentUserID = opponent.getUserID();

        HashMap myMsg = aResponseWithResult(Result.OK);
        myMsg.put("opponentname", opponent.getName());
        myMsg.put("opponentid", opponentUserID);
		myMsg.put("chattoken", myChatToken);
        writeMessage(myMsg);

		ClientConnection opponentConnection = getConnection(gameID, opponentUserID);
		if (opponentConnection != null)
		{
			HashMap msg = aPushWithEvent(Push.GAME_START);
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
				game.getController().endGame(opponent);

				popConnection(gameID, player.getUserID());
				opponentConnection = popConnection(gameID, opponentUserID);
				readTimeout = 0;

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

					opponentConnection = getConnection(gameID, opponentUserID);

					if (gameFinished)
					{
						winner = game.getWinner();

						popConnection(gameID, player.getUserID());
						popConnection(gameID, opponentUserID);

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

		if (false == gameFinished)
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
		boolean invalidGameID = false;
		boolean gameNotActive = false;
		boolean waitForResponse = false;
		boolean saved = false;

		synchronized (game)
		{
			if (!(invalidGameID = game.getID() != gameID))
			{
				short state = game.getState();

				if (Game.STATE_ACTIVE == state)
				{
					if (save)
					{
						opponentConnection = getConnection(gameID, player.getOpponent().getUserID());

						if (null == opponentConnection)
						{
							throw new ChessHeroException(Result.INTERNAL_ERROR);
						}

						game.setState(Game.STATE_PAUSED);
						opponentConnection.setSaveGamePrompted(true);

						waitForResponse = true;
					}
				}
				else if (Game.STATE_PAUSED == state)
				{
					game.setState(Game.STATE_ACTIVE);

					if (save)
					{
						try
						{
							db.connect();
							db.insertSavedGame(gameID, game.getTurn().getUserID(), game.toData());

							setGameSaved(true);
						}
						catch (SQLException e)
						{
							SLog.write("Exception raised while saving game: " + e);
							e.printStackTrace();

							setGameSaved(false);
							setSaveGamePrompted(false);

							throw new ChessHeroException(Result.INTERNAL_ERROR);
						}
						finally
						{
							db.disconnect();
						}
					}
					else
					{
						setGameSaved(false);
					}

					setSaveGamePrompted(false);
					saved = isGameSaved();
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

		if (waitForResponse)
		{
			while (opponentConnection.isSaveGamePrompted())
			{
				try { Thread.sleep(10);} catch (InterruptedException ignore) {}
			}

			saved = opponentConnection.isGameSaved();
		}

		HashMap msg = aResponseWithResult(Result.OK);
		msg.put("saved", saved);
		writeMessage(msg);
	}
}

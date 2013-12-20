package com.kt;

import com.kt.api.Action;
import com.kt.api.Push;
import com.kt.chesco.CHESCOReader;
import com.kt.chesco.CHESCOWriter;
import com.kt.game.Game;
import com.kt.game.GameController;
import com.kt.game.Player;
import com.kt.game.Position;
import com.kt.utils.ChessHeroException;
import com.kt.api.Result;
import com.kt.utils.SLog;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/9/13
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */
// TODO: keep client connections in a map or something
// TODO: move game cancel and end in separate methods
public class ClientConnection extends Thread
{
    private static final int READ_TIMEOUT = 15 * 1000; // In milliseconds

    private static final int DEFAULT_FETCH_GAMES_OFFSET = 0;
    private static final int DEFAULT_FETCH_GAMES_LIMIT = 100;
	private static final int MAX_FETCH_GAMES_LIMIT = 1000;

    private static final String DEFAULT_PLAYER_COLOR = "white";

    private boolean running = true;
    private Socket sock = null;
    private CHESCOReader reader = null;
    private CHESCOWriter writer = null;
    private Database db = null;

    private Player player = null;

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

        Game game = null;

        if (player != null && (game = player.getGame()) != null)
        {
            synchronized (game)
            {
				int gameID = game.getID();

                if (Game.STATE_PENDING == game.getState())
                {
                    try
                    {
                        db.connect();
						db.startTransaction();

                        db.deleteGame(gameID);
						db.removePlayer(gameID, player.getUserID());

						db.commit();

						game.setState(Game.STATE_FINISHED);
						Game.removeGame(gameID);
                    }
                    catch (SQLException e)
                    {
						SLog.write("Exception while cancelling game due to EOF: " + e);

						try
						{
							db.rollback();
						}
						catch (SQLException eeeeeeeee)
						{
							eeeeeeeee.printStackTrace();
							SLog.write("MANUAL CLEANUP REQUIRED FOR GAME WITH ID: " + game.getID() + " AND PLAYER: " + player);
						}
                    }
                }
                else if (Game.STATE_STARTED == game.getState())
                {
					Player opponent = null;

                    try
					{
						db.connect();
						db.startTransaction();

						opponent = player.getOpponent();

						db.deleteGame(gameID);
						db.removePlayersForGame(gameID);
						db.insertResult(gameID, opponent.getUserID(), player.getUserID());

						db.commit();

						game.setState(Game.STATE_FINISHED);
						player.leave();
						opponent.leave();
						Game.removeGame(gameID);
					}
					catch (Exception e)
					{
						SLog.write("Exception while ending game due to EOF: " + e);

						try
						{
							db.rollback();
						}
						catch (SQLException eeeeeeeee)
						{
							eeeeeeeee.printStackTrace();
							SLog.write("MANUAL CLEANUP REQUIRED FOR GAME WITH ID: " + game.getID() + " AND PLAYERS: " + player + ", " + opponent);
						}
					}

					if (opponent != null)
					{
						HashMap msg = aPushWithEvent(Push.GAME_ENDED);
						msg.put("winner", opponent.getUserID());
						msg.put("opponentdisconnected", true);

						ClientConnection conn = opponent.getConnection();

						if (conn != null)
						{
							conn.writeMessage(msg);
						}
					}
				}
            }
        }

        closeSocket();
        db.disconnect();
    }

    private void listen()
    {
        try
        {
            // Set timeout for the first message, if someone connected, he must say something
            sock.setSoTimeout(READ_TIMEOUT);

            while (running)
            {
                // Read request
                Object request = reader.read();

                SLog.write("Request received: " + request);

                if (!(request instanceof Map))
                {   // Map is the only type of object the server allows for sending requests
                    SLog.write("Request is not in MAP format, ignoring!");
                    continue;
                }

                handleRequest((HashMap<String, Object>)request);

                // Remove timeout after first message
                sock.setSoTimeout(0);
            }
        }
        catch (InputMismatchException e)
        {
            SLog.write("Message not conforming to CHESCO");
            writeMessage(aResponseWithResult(Result.INVALID_REQUEST));
        }
        catch (SocketTimeoutException e)
        {
            SLog.write("Read timed out");
        }
        catch (EOFException e)
        {
            SLog.write("Client closed connection: " + e);
        }
        catch (IOException e)
        {
            SLog.write("Error reading: " + e);
        }
        catch (ChessHeroException e)
        {
            int code = e.getCode();
            SLog.write("Chess hero exception: " + code);
            writeMessage(aResponseWithResult(code));
        }
        catch (Exception e)
        {
            SLog.write("Surprise exception: " + e);
			e.printStackTrace();
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

    public synchronized void writeMessage(HashMap<String, Object> message)
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

    private void handleRequest(HashMap<String, Object> request) throws ChessHeroException
    {
        try
        {
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

            player = new Player(userID, name, this);

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

            player = new Player (userID, name, this);

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
			String chatToken = Game.generateChatToken(gameID, userID, gameName);
			db.insertPlayer(gameID, userID, chatToken, color);

            Game game = new Game(gameID, gameName);
            game.setState(Game.STATE_PENDING);
            player.join(game, (color.equals("white") ? Player.Color.WHITE : Player.Color.BLACK));

            Game.addGame(game);

			db.commit();

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
                int playerGID = game.getID();

                if (!(invalidGameID = playerGID != gID))
                {
                    try
                    {
                        db.connect();
						db.startTransaction();
                        db.deleteGame(gID);
						db.removePlayer(gID, player.getUserID());

                        Game.removeGame(gID);
                        player.leave();

						db.commit();
                    }
                    catch (SQLException e)
                    {
						SLog.write("Exception raised while cancelling game: " + e);
						e.printStackTrace();

						try
						{
							db.rollback();
						}
						catch(SQLException ignore)
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

        Game game = Game.getGame(gameID);

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
                        myChatToken = Game.generateChatToken(gameID, myUserID, gameName);

                        db.connect();
                        db.startTransaction();

						Player.Color opponentColor = opponent.getColor();
						Player.Color myColor = (opponentColor == Player.Color.WHITE ? Player.Color.BLACK : Player.Color.WHITE);

                        db.updateGameState(gameID, Game.STATE_STARTED);
                        db.insertPlayer(gameID, myUserID, myChatToken, (myColor == Player.Color.WHITE ? "white" : "black"));

                        db.commit();

                        player.join(game, myColor);

                        game.setState(Game.STATE_STARTED);
                        new GameController(game);
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
        myMsg.put("opponentname", opponent.getName());
        myMsg.put("opponentid", opponent.getUserID());
		myMsg.put("chattoken", myChatToken);
        writeMessage(myMsg);

        HashMap msg = aPushWithEvent(Push.GAME_STARTED);
        msg.put("opponentname", player.getName());
        msg.put("opponentid", player.getUserID());

        opponent.getConnection().writeMessage(msg);
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

		Player opponent = null;
		int opponentUserID = 0;

        synchronized (game)
        {
            if (!(invalidGameID = game.getID() != gameID) && !(gameHasNotStarted = game.getState() != Game.STATE_STARTED))
            {
				try
				{
					db.connect();
					db.startTransaction();

					opponent = player.getOpponent();
					opponentUserID = opponent.getUserID();

					db.deleteGame(gameID);
					db.removePlayersForGame(gameID);
					db.insertResult(gameID, opponentUserID, player.getUserID());

					db.commit();

					game.setState(Game.STATE_FINISHED);
					player.leave();
					opponent.leave();

					Game.removeGame(gameID);
				}
				catch (SQLException e)
				{
					SLog.write("Exception raised while exiting game: " + e);
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

		HashMap msg = aPushWithEvent(Push.GAME_ENDED);
		msg.put("winner", opponentUserID);
		msg.put("opponentexited", true);
		opponent.getConnection().writeMessage(msg);
    }

	private void handleMove(HashMap<String, Object> request) throws ChessHeroException
	{
		Game game = player.getGame();

		if (null == game)
		{
			writeMessage(aResponseWithResult(Result.NOT_PLAYING));
			return;
		}

		String fromStr = (String)request.get("from");
		String toStr = (String)request.get("to");

		if (null == fromStr || null == toStr)
		{
			writeMessage(aResponseWithResult(Result.MISSING_PARAMETERS));
			return;
		}

		boolean gameNotStarted = false;
		boolean invalidPosition = false;
		int result = 0;
		Player opponent = null;
		Player winner = null;

		synchronized (game)
		{
			if (!(gameNotStarted = game.getState() != Game.STATE_STARTED))
			{
				Position from = Position.positionFromBoardPosition(fromStr);
				Position to = Position.positionFromBoardPosition(toStr);

				if (!(invalidPosition = null == from || null == to))
				{
					GameController controller = game.getController();

					opponent = player.getOpponent();
					result = controller.execute(from, to);
					winner = controller.getWinner();

					if (winner != null)
					{
						try
						{
							int gameID = game.getID();

							db.connect();
							db.startTransaction();

							db.deleteGame(gameID);
							db.removePlayersForGame(gameID);
							db.insertResult(gameID, winner.getUserID(), (winner == player ? opponent.getUserID() : player.getUserID()));

							db.commit();

							game.setState(Game.STATE_FINISHED);
							Game.removeGame(gameID);

							player.leave();
							opponent.leave();
						}
						catch (SQLException e)
						{
							SLog.write("Exception raised while ending game: " + e);
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
		}

		if (gameNotStarted)
		{
			writeMessage(aResponseWithResult(Result.MOVE_NA));
			return;
		}

		if (invalidPosition)
		{
			writeMessage(aResponseWithResult(Result.INVALID_MOVE_FORMAT));
			return;
		}

		writeMessage(aResponseWithResult(result));

		if (result != Result.OK)
		{
			return;
		}

		ClientConnection opponentConnection = opponent.getConnection();

		HashMap msg = aPushWithEvent(Push.GAME_MOVE);
		msg.put("from", fromStr);
		msg.put("to", toStr);
		opponentConnection.writeMessage(msg);

		if (null == winner)
		{
			return;
		}

		HashMap endMsg = aPushWithEvent(Push.GAME_ENDED);
		endMsg.put("winner", winner.getUserID());

		writeMessage(endMsg);
		opponentConnection.writeMessage(endMsg);
	}
}

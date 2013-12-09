package com.kt;

import com.kt.api.Action;
import com.kt.chesco.CHESCOReader;
import com.kt.chesco.CHESCOWriter;
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
public class ClientConnection extends Thread
{
    private final static int READ_TIMEOUT = 15 * 1000; // In milliseconds

    private final static int NONE = -1;

    private boolean running = true;
    private Socket sock = null;
    private CHESCOReader reader = null;
    private CHESCOWriter writer = null;
    private Database db = null;

    private boolean hasAuthenticated = false;
    private int userID = NONE;

    private int gameID = NONE;
    private boolean isWaitingPlayer = false;
    private boolean isInGame = false;

    ClientConnection(Socket sock) throws IOException
    {
        this.sock = sock;
        db = new Database();

        reader = new CHESCOReader(sock.getInputStream());
        writer = new CHESCOWriter(sock.getOutputStream());
    }

    public int getUserID()
    {
        return userID;
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

    public void run()
    {
        listen();

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
            writeMessage(responseWithResult(code));
        }
        catch (Exception e)
        {
            SLog.write("Surprise exception: " + e);
        }
    }

    private HashMap<String, Object> responseWithResult(int result)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("result", result);
        return map;
    }

    private void writeMessage(HashMap<String, Object> message)
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
                writeMessage(responseWithResult(Result.MISSING_PARAMETERS));
                return;
            }

            if (!hasAuthenticated && action != Action.LOGIN && action != Action.REGISTER)
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

                default:
                    throw new ChessHeroException(Result.UNRECOGNIZED_ACTION);
            }
        }
        catch (ClassCastException e)
        {
            SLog.write("A request parameter is not of the appropriate type");
            writeMessage(responseWithResult(Result.INVALID_PARAM));
        }
    }

    private void handleRegister(HashMap<String, Object> request) throws ChessHeroException
    {
        if (hasAuthenticated)
        {
            writeMessage(responseWithResult(Result.ALREADY_LOGGEDIN));
            return;
        }

        String name = (String)request.get("username");
        String pass = (String)request.get("password");

        if (null == name || null == pass)
        {
            writeMessage(responseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        if (!Credentials.isNameValid(name))
        {
            writeMessage(responseWithResult(Result.INVALID_NAME));
            return;
        }

        if (Credentials.isBadUser(name))
        {
            writeMessage(responseWithResult(Result.BAD_USER));
            return;
        }

        if (!Credentials.isPassValid(pass))
        {
            writeMessage(responseWithResult(Result.INVALID_PASS));
            return;
        }

        try
        {
            db.connect();

            if (db.userExists(name))
            {
                writeMessage(responseWithResult(Result.USER_EXISTS));
                return;
            }

            int salt = Credentials.generateSalt();
            String passHash = Credentials.saltAndHash(pass, salt);

            db.insertUser(name, passHash, salt);

            userID = db.getUserID(name);

            if (-1 == userID)
            {   // Could not fetch the user id
                throw new ChessHeroException(Result.INTERNAL_ERROR);
            }

            hasAuthenticated = true;

            HashMap response = responseWithResult(Result.OK);
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
        if (hasAuthenticated)
        {
            writeMessage(responseWithResult(Result.ALREADY_LOGGEDIN));
            return;
        }

        String name = (String)request.get("username");
        String pass = (String)request.get("password");

        if (null == name || null == pass)
        {
            writeMessage(responseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        try
        {
            db.connect();

            AuthPair auth = db.getAuthPair(name);

            if (null == auth || !auth.matches(pass))
            {
                writeMessage(responseWithResult(Result.INVALID_CREDENTIALS));
                return;
            }

            userID = db.getUserID(name);

            if (-1 == userID)
            {
                throw new ChessHeroException(Result.INTERNAL_ERROR);
            }

            hasAuthenticated = true;

            HashMap response = responseWithResult(Result.OK);
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
        if (gameID != NONE)
        {
            writeMessage(responseWithResult(Result.ALREADY_PLAYING));
            return;
        }

        String gameName = (String)request.get("gamename");

        if (null == gameName)
        {
            writeMessage(responseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        try
        {
            db.connect();

            int gameID = db.insertGame(gameName, Game.STATE_PENDING);

            if (-1 == gameID)
            {
                throw new ChessHeroException(Result.INTERNAL_ERROR);
            }

            Game game = new Game(gameID, gameName, this);
            game.setState(Game.STATE_PENDING);
            Game.addGame(game);

            this.gameID = gameID;
            isWaitingPlayer = true;

            HashMap response = responseWithResult(Result.OK);
            response.put("gameid", gameID);
            writeMessage(response);
        }
        catch (SQLException e)
        {
            SLog.write("Exception while creating game: " + e);
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        finally
        {
            db.disconnect();
        }
    }

    private void handleCancelGame(HashMap<String, Object> request) throws ChessHeroException
    {
        if (NONE == gameID)
        {
            writeMessage(responseWithResult(Result.NOT_PLAYING));
            return;
        }

        if (isInGame)
        {
            writeMessage(responseWithResult(Result.CANCEL_NA));
            return;
        }

        Integer gameIDToDelete = (Integer)request.get("gameid");

        if (null == gameIDToDelete)
        {
            writeMessage(responseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        int gid = gameIDToDelete.intValue();

        if (gameID != gid)
        {
            writeMessage(responseWithResult(Result.INVALID_GAME_ID));
            return;
        }

        try
        {
            db.connect();

            db.deleteGame(gid);

            Game.removeGame(gameID);
            isWaitingPlayer = false;
            gameID = NONE;

            writeMessage(responseWithResult(Result.OK));
        }
        catch (SQLException e)
        {
            SLog.write("Exception while deleting game: " + e);
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        finally
        {
            db.disconnect();
        }
    }

    private void handleFetchGames(HashMap<String, Object> request) throws ChessHeroException
    {
        Integer offset = (Integer)request.get("offset");
        Integer limit = (Integer)request.get("limit");

        if (null == offset || null == limit)
        {
            writeMessage(responseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        try
        {
            db.connect();

            ArrayList games = db.fetchGames(Game.STATE_PENDING, offset, limit);

            HashMap response = responseWithResult(Result.OK);
            response.put("games", games);
            writeMessage(response);
        }
        catch (SQLException e)
        {
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        finally
        {
            db.disconnect();
        }
    }

    private void handleJoinGame(HashMap<String, Object> request) throws ChessHeroException
    {
        if (gameID != NONE)
        {
            writeMessage(responseWithResult(Result.ALREADY_PLAYING));
            return;
        }

        Integer joinGameID = (Integer)request.get("gameid");

        if (null == joinGameID)
        {
            writeMessage(responseWithResult(Result.MISSING_PARAMETERS));
            return;
        }

        int gameID = joinGameID.intValue();

        Game game = Game.getGame(gameID);

        if (null == game)
        {
            writeMessage(responseWithResult(Result.INVALID_GAME_ID));
            return;
        }

        // Using flags to write any errors outside of the synchronized block so that the
        // lock on the object is released as soon as possible
        boolean gameOccupied = false;

        synchronized (game)
        {
            if (game.getState() != Game.STATE_PENDING)
            {
                gameOccupied = true;
            }
            else
            {
                try
                {
                    String gameName = game.getName();
                    int otherUserID = game.getOtherUserID(userID);

                    String myChatToken = Game.generateChatToken(gameID, userID, gameName);
                    String otherChatToken = Game.generateChatToken(gameID, otherUserID, gameName);

                    db.connect();
                    db.startTransaction();

                    db.updateGameState(gameID, Game.STATE_STARTED);
                    db.insertPlayerPair(gameID, userID, myChatToken, otherUserID, otherChatToken);

                    db.commit();

                    game.setState(Game.STATE_STARTED);
                }
                catch (Exception e)
                {
                    SLog.write("Exception raised while joining game: " + e);

                    try
                    {
                        db.rollback();
                    }
                    catch (SQLException ignore)
                    {
                    }

                    writeMessage(responseWithResult(Result.INTERNAL_ERROR));
                    return;
                }
                finally
                {
                    db.disconnect();
                }
            }
        }

        if (gameOccupied)
        {
            writeMessage(responseWithResult(Result.GAME_OCCUPIED));
            return;
        }

        game.join(this);
    }
}

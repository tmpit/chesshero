package com.kt;

import com.kt.api.Result;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/16/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
class Database
{
    private static String DB_URL = "jdbc:mysql://localhost:3306/";
    private static String DB_NAME = "chesshero";
    private static String DB_USER = "chesshero_srv";
    private static String DB_PASS = "banichkasyssirene";

    private Connection conn = null;

    private boolean keepAlive = false;
    private boolean isOpen = false;
    private boolean inTransaction = false;

    private void connect() throws SQLException
    {
        if (isOpen)
        {
            return;
        }

        String connector = "com.mysql.jdbc.Driver";

        try
        {
            Class.forName(connector).newInstance();
            conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
            isOpen = true;
        }
        catch (IllegalAccessException e)
        {
            throw new SQLException("Connect raised illegal access exception");
        }
        catch (InstantiationException e)
        {
            throw new SQLException("Connect raised instantiation exception");
        }
        catch (ClassNotFoundException e)
        {
            throw new SQLException("Connect raised class not found exception");
        }
    }

    private void disconnect()
    {
        if (null == conn)
        {
            return;
        }

        try
        {
            conn.close();
        }
        catch (SQLException ignore)
        {
        }
        finally
        {
            conn = null;
            isOpen = false;
            keepAlive = false;
            inTransaction = false;
        }
    }

    private void closeStatement(PreparedStatement stmt)
    {
        if (null == stmt)
        {
            return;
        }

        try
        {
            stmt.close();
        }
        catch (SQLException ignore)
        {
        }
    }

    public boolean getKeepAlive()
    {
        return keepAlive;
    }

    public void setKeepAlive(boolean flag)
    {
        keepAlive = flag;

        if (!flag)
        {
            disconnect();
        }
    }

    public void startTransaction() throws SQLException
    {
        if (!isOpen)
        {
            return;
        }

        conn.setAutoCommit(false);
        inTransaction = true;
    }

    public void commit() throws SQLException
    {
        if (!isOpen)
        {
            return;
        }

        conn.setAutoCommit(true);
        inTransaction = false;
    }

    public boolean userExists(String username) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ?");
            stmt.setString(1, username);

            boolean exists = false;
            ResultSet set = stmt.executeQuery();

            while (set.next())
            {
                exists = set.getInt(1) > 0;
            }

            return exists;
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }

    // Returns -1 if the user with that name does not exist
    public int getUserID(String username) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("SELECT id FROM users WHERE name = ?");
            stmt.setString(1, username);

            ResultSet set = stmt.executeQuery();

            while (set.next())
            {
                return set.getInt(1);
            }

            return -1;
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }

    public AuthPair getAuthPair(String username) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("SELECT pass, salt FROM users WHERE name = ?");
            stmt.setString(1, username);

            ResultSet set = stmt.executeQuery();

            while (set.next())
            {
                String passHash = set.getString(1);
                int salt = set.getInt(2);
                return new AuthPair(passHash, salt);
            }

            return null;
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }

    public void insertUser(String name, String passHash, int salt) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("INSERT INTO users (name, pass, salt) VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, passHash);
            stmt.setInt(3, salt);

            stmt.executeUpdate();
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }

    // Returns the new game id
    // On error returns -1
    public int insertGame(String name, int user1ID, int user2ID, short state) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("INSERT INTO games (gname, guid1, guid2, state) VALUES (?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setInt(2, user1ID);
            stmt.setInt(3, user2ID);
            stmt.setShort(4, state);

            stmt.executeUpdate();

            closeStatement(stmt);

            stmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");

            ResultSet set = stmt.executeQuery();

            while (set.next())
            {
                return set.getInt(1);
            }

            return -1;
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }

    public void deleteGame(int gameID) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("DELETE FROM games WHERE gid = ?");
            stmt.setInt(1, gameID);

            stmt.executeUpdate();
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }

    public ArrayList fetchGames(short state, int offset, int limit) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("SELECT gid, gname FROM games WHERE state = ? LIMIT ?, ?");
            stmt.setShort(1, state);
            stmt.setInt(2, offset);
            stmt.setInt(3, limit);

            ArrayList games = new ArrayList();
            ResultSet set = stmt.executeQuery();

            while (set.next())
            {
                HashMap game = new HashMap();
                game.put("gameid", set.getInt(1));
                game.put("gamename", set.getString(2));

                games.add(game);
            }

            return games;
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }

    public void gameSetSecondPlayer(int gameID, int user2ID, short state) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("UPDATE games SET guid2 = ?, state = ? WHERE gid = ?");
            stmt.setInt(1, user2ID);
            stmt.setShort(2, state);
            stmt.setInt(3, gameID);

            stmt.executeUpdate();
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }

    public void insertChatTokens(int gameID, int user1ID, String user1Token, int user2ID, String user2Token) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("INSERT INTO chat_auth (gid, uid, token) VALUES (?, ?, ?), (?, ?, ?)");
            stmt.setInt(1, gameID);
            stmt.setInt(2, user1ID);
            stmt.setString(3, user1Token);
            stmt.setInt(4, gameID);
            stmt.setInt(5, user2ID);
            stmt.setString(6, user2Token);

            stmt.executeUpdate();
        }
        finally
        {
            closeStatement(stmt);

            if (!keepAlive)
            {
                disconnect();
            }
        }
    }
}

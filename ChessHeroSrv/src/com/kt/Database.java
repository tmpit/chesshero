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

    private boolean isOpen = false;
    private boolean inTransaction = false;

    public void connect() throws SQLException
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

    public void disconnect()
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
            inTransaction = false;
        }
    }

    private void closeResources(PreparedStatement stmt, ResultSet set)
    {
        if (null == stmt)
        {
            return;
        }

        try
        {
            stmt.close();

            if (set != null)
            {
                set.close();
            }
        }
        catch (SQLException ignore)
        {
        }
    }

    public void startTransaction() throws SQLException
    {
        if (inTransaction)
        {
            return;
        }

        conn.setAutoCommit(false);
        inTransaction = true;
    }

    public void commit() throws SQLException
    {
        if (!inTransaction)
        {
            return;
        }

        conn.setAutoCommit(true);
        inTransaction = false;
    }

    public void rollback() throws SQLException
    {
        if (!inTransaction)
        {
            return;
        }

        conn.rollback();
        inTransaction = false;
    }

    public boolean userExists(String username) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet set = null;

        try
        {
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ?");
            stmt.setString(1, username);

            boolean exists = false;
            set = stmt.executeQuery();

            while (set.next())
            {
                exists = set.getInt(1) > 0;
            }

            return exists;
        }
        finally
        {
            closeResources(stmt, set);
        }
    }

    // Returns -1 if the user with that name does not exist
    public int getUserID(String username) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet set = null;

        try
        {
            stmt = conn.prepareStatement("SELECT id FROM users WHERE name = ?");
            stmt.setString(1, username);

            set = stmt.executeQuery();

            while (set.next())
            {
                return set.getInt(1);
            }

            return -1;
        }
        finally
        {
            closeResources(stmt, set);
        }
    }

    public AuthPair getAuthPair(String username) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet set = null;

        try
        {
            stmt = conn.prepareStatement("SELECT pass, salt FROM users WHERE name = ?");
            stmt.setString(1, username);

            set = stmt.executeQuery();

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
            closeResources(stmt, set);
        }
    }

    public void insertUser(String name, String passHash, int salt) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            stmt = conn.prepareStatement("INSERT INTO users (name, pass, salt) VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, passHash);
            stmt.setInt(3, salt);

            stmt.executeUpdate();
        }
        finally
        {
            closeResources(stmt, null);
        }
    }

    // Returns the new game id
    // On error returns -1
    public int insertGame(String name, short state) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet set = null;

        try
        {
            stmt = conn.prepareStatement("INSERT INTO games (gname, state) VALUES (?, ?)");
            stmt.setString(1, name);
            stmt.setShort(2, state);

            stmt.executeUpdate();

            closeResources(stmt, null);

            stmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");

            set = stmt.executeQuery();

            while (set.next())
            {
                return set.getInt(1);
            }

            return -1;
        }
        finally
        {
            closeResources(stmt, set);
        }
    }

    public void deleteGame(int gameID) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            stmt = conn.prepareStatement("DELETE FROM games WHERE gid = ?");
            stmt.setInt(1, gameID);

            stmt.executeUpdate();
        }
        finally
        {
            closeResources(stmt, null);
        }
    }

    public ArrayList getGames(short state, int offset, int limit) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet set = null;

        try
        {
            stmt = conn.prepareStatement("SELECT gid, gname FROM games WHERE state = ? LIMIT ?, ?");
            stmt.setShort(1, state);
            stmt.setInt(2, offset);
            stmt.setInt(3, limit);

            ArrayList games = new ArrayList();
            set = stmt.executeQuery();

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
            closeResources(stmt, set);
        }
    }

	// Get games of the specified state within offset and limit and returns player color along with game info
	public ArrayList<HashMap> getGamesAndPlayerInfo(short state, int offset, int limit) throws SQLException
	{
		PreparedStatement stmt = null;
		ResultSet set = null;

		try
		{
			stmt = conn.prepareStatement("SELECT games.gid, games.gname, players.color FROM games LEFT JOIN players USING(gid) WHERE games.state = ? LIMIT ?, ?");
			stmt.setShort(1, state);
			stmt.setInt(2, offset);
			stmt.setInt(3, limit);

			set =  stmt.executeQuery();
			ArrayList<HashMap> games = new ArrayList<HashMap>();

			while (set.next())
			{
				HashMap game = new HashMap();
				game.put("gameid", set.getInt(1));
				game.put("gamename", set.getString(2));
				game.put("playercolor", set.getString(3));

				games.add(game);
			}

			return games;
		}
		finally
		{
			closeResources(stmt, set);
		}
	}

    public void updateGameState(int gameID, short state) throws SQLException
    {
        PreparedStatement stmt = null;

        try
        {
            stmt = conn.prepareStatement("UPDATE games SET state = ? WHERE gid = ?");
            stmt.setShort(1, state);
            stmt.setInt(2, gameID);

            stmt.executeUpdate();
        }
        finally
        {
            closeResources(stmt, null);
        }
    }

	public void insertPlayer(int gameID, int userID, String token, String color) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO players (gid, uid, token, color) VALUES (?, ?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setInt(2, userID);
			stmt.setString(3, token);
			stmt.setString(4, color);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void removePlayer(int gameID, int userID) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("DELETE FROM players WHERE gid = ? AND uid = ?");
			stmt.setInt(1, gameID);
			stmt.setInt(2, userID);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void removePlayersForGame(int gameID) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("DELETE FROM players WHERE gid = ?");
			stmt.setInt(1, gameID);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void insertResult(int gameID) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO results (gid) VALUES (?)");
			stmt.setInt(1, gameID);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void insertResult(int gameID, int winnerUserID, int loserUserID) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO results (gid, winner, loser) VALUES (?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setInt(2, winnerUserID);
			stmt.setInt(3, loserUserID);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void insertMove(int gameID, int userID, String move) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO moves (gid, uid, move) VALUES (?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setInt(2, userID);
			stmt.setString(3, move);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void insertGameSave(int gameID, int moveUserID, byte gameData[]) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO saved_games (gid, next, game) VALUES (?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setInt(2, moveUserID);
			stmt.setBytes(3, gameData);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}
}

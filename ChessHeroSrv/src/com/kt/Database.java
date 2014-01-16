package com.kt;

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
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "chesshero";
    private static final String DB_USER = "chesshero_srv";
    private static final String DB_PASS = "banichkasyssirene";

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

            if (set.next())
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

            if (set.next())
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
    public int insertGame(String name, short state, int timeout) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet set = null;

        try
        {
            stmt = conn.prepareStatement("INSERT INTO games (gname, state, timeout) VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setShort(2, state);
			stmt.setInt(3, timeout);

            stmt.executeUpdate();

            closeResources(stmt, null);

            stmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");

            set = stmt.executeQuery();

            if (set.next())
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

	public void insertGame(int gameID, String name, short state) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO games (gid, gname, state) VALUES (?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setString(2, name);
			stmt.setShort(3, state);
			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
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

	// Get games of the specified state within offset and limit and returns player color along with game info
	// Each hashmap will contain the following:
	// "gameid" => (int)
	// "gamename" => (string)
	// "timeout" => (int)
	// "userid" => (int)
	// "username" => (string)
	// "usercolor" => (string)
	public ArrayList<HashMap> getGamesAndPlayerInfo(short state, int offset, int limit) throws SQLException
	{
		PreparedStatement stmt = null;
		ResultSet set = null;

		try
		{
			stmt = conn.prepareStatement("SELECT gid, gname, timeout, uid, name, color FROM games INNER JOIN players USING(gid) INNER JOIN users ON(players.uid = users.id) WHERE STATE = ? LIMIT ?, ?");
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
				game.put("timeout", set.getInt(3));
				game.put("userid", set.getInt(4));
				game.put("username", set.getString(5));
				game.put("usercolor", set.getString(6));

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

	public void insertPlayer(int gameID, int userID, String color) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO players (gid, uid, color) VALUES (?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setInt(2, userID);
			stmt.setString(3, color);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void deletePlayer(int gameID, int userID) throws SQLException
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

	public void deletePlayersForGame(int gameID) throws SQLException
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

	public void insertChatEntry(int gameID, int userID, String token) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO chat_auth (gid, uid, token) VALUES (?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setInt(2, userID);
			stmt.setString(3, token);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void deleteChatEntry(int gameID, int userID) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("DELETE FROM chat_auth WHERE gid = ? AND uid = ?");
			stmt.setInt(1, gameID);
			stmt.setInt(2, userID);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void deleteChatEntriesForGame(int gameID) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("DELETE FROM chat_auth WHERE gid = ?");
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

	public void insertResult(int gameID, int winnerUserID, int loserUserID, boolean checkmate) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO results (gid, winner, loser, checkmate) VALUES (?, ?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setInt(2, winnerUserID);
			stmt.setInt(3, loserUserID);
			stmt.setBoolean(4, checkmate);

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

	public void insertGameSave(int gameID, String name, byte gameData[], int timeout) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("REPLACE INTO saved_games (gid, gname, gdata, timeout) VALUES (?, ?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setString(2, name);
			stmt.setBytes(3, gameData);
			stmt.setInt(4, timeout);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public void deleteSavedGame(int gameID) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("DELETE FROM saved_games WHERE gid = ?");
			stmt.setInt(1, gameID);

			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	// Get saved games within offset and limit in which a user with the specified user id plays
	// Each hashmap will contain the following:
	// "gameid" => (int)
	// "gamename" => (string)
	// "timeout" => (int)
	// "userid" => (int)
	// "username" => (string)
	// "usercolor" => (string)
	public ArrayList<HashMap> getSavedGamesWithOpponentsForUser(int userID, int offset, int limit) throws SQLException
	{
		PreparedStatement stmt = null;
		ResultSet set = null;

		try
		{
			// We need to take all saved games that the player has played in and then take the player's opponent's id, name and color in each of those games
			stmt = conn.prepareStatement("SELECT gid, gname, timeout, uid, name, color FROM saved_games " +
											"INNER JOIN saved_players USING(gid) " +
											"INNER JOIN users ON(users.id = saved_players.uid) " +
											"WHERE gid IN " +
												"(SELECT gid FROM saved_players WHERE uid = ?) " +
											"AND uid != ? " +
											"LIMIT ?, ?");
			stmt.setInt(1, userID);
			stmt.setInt(2, userID);
			stmt.setInt(3, offset);
			stmt.setInt(4, limit);

			set = stmt.executeQuery();

			ArrayList<HashMap> games = new ArrayList<HashMap>();

			while (set.next())
			{
				HashMap game = new HashMap();
				game.put("gameid", set.getInt(1));
				game.put("gamename", set.getString(2));
				game.put("timeout", set.getInt(3));
				game.put("userid", set.getInt(4));
				game.put("username", set.getString(5));
				game.put("usercolor", set.getString(6));

				games.add(game);
			}

			return games;
		}
		finally
		{
			closeResources(stmt, set);
		}
	}

	public boolean isUserPresentInSavedGame(int gameID, int userID) throws SQLException
	{
		PreparedStatement stmt = null;
		ResultSet set = null;

		try
		{
			stmt = conn.prepareStatement("SELECT COUNT(*) FROM saved_players WHERE gid = ? AND uid = ?");
			stmt.setInt(1, gameID);
			stmt.setInt(2, userID);

			set = stmt.executeQuery();

			return set.next();
		}
		finally
		{
			closeResources(stmt, set);
		}
	}

	public HashMap<String, Object> getGameSave(int gameID) throws SQLException
	{
		PreparedStatement stmt = null;
		ResultSet set = null;

		try
		{
			stmt = conn.prepareStatement("SELECT gname, gdata, timeout FROM saved_games WHERE gid = ?");
			stmt.setInt(1, gameID);

			set = stmt.executeQuery();

			if (set.next())
			{
				HashMap<String, Object> result = new HashMap<String, Object>();
				result.put("gname", set.getString(1));
				result.put("gdata", set.getBytes(2));
				result.put("timeout", set.getLong(3));

				return result;
			}

			return null;
		}
		finally
		{
			closeResources(stmt, set);
		}
	}

	public void insertSavedGamePlayer(int gameID, int userID, String color, boolean next, long millisPlayed) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO saved_players (gid, uid, color, next, millis_played) VALUES (?, ?, ?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setInt(2, userID);
			stmt.setString(3, color);
			stmt.setBoolean(4, next);
			stmt.setLong(5, millisPlayed);
			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	public ArrayList<HashMap> getSavedGamePlayers(int gameID) throws SQLException
	{
		PreparedStatement stmt = null;
		ResultSet set = null;

		try
		{
			stmt = conn.prepareStatement("SELECT uid, color, next, millis_played FROM saved_players WHERE gid = ?");
			stmt.setInt(1, gameID);
			set = stmt.executeQuery();

			ArrayList<HashMap> players = new ArrayList<HashMap>(2);

			while (set.next())
			{
				HashMap<String, Object> player = new HashMap<String, Object>();
				player.put("id", set.getInt(1));
				player.put("color", set.getString(2));
				player.put("next", set.getBoolean(3));
				player.put("played", set.getLong(4));
				players.add(player);
			}

			return players;
		}
		finally
		{
			closeResources(stmt, set);
		}
	}

	public void deletePlayersForSavedGame(int gameID) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("DELETE FROM saved_players WHERE gid = ?");
			stmt.setInt(1, gameID);
			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}
}

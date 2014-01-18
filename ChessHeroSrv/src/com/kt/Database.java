package com.kt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Database class provides a convenient interface for performing database queries on the
 * Chess Hero database
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
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

	/**
	 * Attempts to connect to the MySQL server
	 * @throws SQLException Thrown if connection could not be established
	 */
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

	/**
	 * Disconnects from the MySQL server
	 */
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

	/**
	 * Properly closes a prepared statement and a result set objects
	 * @param stmt The {@code PreparedStatement}
	 * @param set The {@code ResultSet}
	 */
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

	/**
	 * Attempts to begin a transaction
	 * @throws SQLException
	 */
    public void startTransaction() throws SQLException
    {
        if (inTransaction)
        {
            return;
        }

        conn.setAutoCommit(false);
        inTransaction = true;
    }

	/**
	 * Attempts to close a transaction
	 * @throws SQLException
	 */
    public void commit() throws SQLException
    {
        if (!inTransaction)
        {
            return;
        }

        conn.setAutoCommit(true);
        inTransaction = false;
    }

	/**
	 * Attempts to rollback a transaction
	 * @throws SQLException
	 */
    public void rollback() throws SQLException
    {
        if (!inTransaction)
        {
            return;
        }

        conn.rollback();
        inTransaction = false;
    }

	/**
	 * Checks if a user with the specified username already exists in the database
	 * @param username A {@code String}
	 * @return True if the user with the specified username already exists in the database, false if not
	 * @throws SQLException
	 */
    public boolean userExists(String username) throws SQLException
    {
        PreparedStatement stmt = null;
        ResultSet set = null;

        try
        {
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE name = ?");
            stmt.setString(1, username);
            set = stmt.executeQuery();

			if (set.next())
			{
				return set.getInt(1) > 0;
			}

			return false;
        }
        finally
        {
            closeResources(stmt, set);
        }
    }

	/**
	 * Gets the user id of a user with the specified user name
	 * @param username A {@code String}
	 * @return The user id of the user or -1 if no such user exists in the database
	 * @throws SQLException
	 */
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

	/**
	 * Gets the password hash and salt for a user with the specified username and constructs
	 * a {@code AuthPair} object from them
	 * @param username A {@code String}
	 * @return An {@code AuthPair} instance with the password hash and salt or null if no such user exists
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a user entry in the database with the specified username, password hash and salt
	 * @param name A {@code String}
	 * @param passHash A {@code String}
	 * @param salt An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a game entry in the database with the specified name, state and timeout
	 * @param name A {@code String}
	 * @param state A state constant declared in the {@code Game} class
	 * @param timeout An {@code int}
	 * @return The game id of the newly created game or -1 if the id could not be fetched
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a game entry in the database with the specified game id, name, state and timeout
	 * @param gameID An {@code int}
	 * @param name A {@code String}
	 * @param state A state constant declared in the {@code Game} class
	 * @param timeout An {@code int}
	 * @throws SQLException
	 */
	public void insertGame(int gameID, String name, short state, int timeout) throws SQLException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt = conn.prepareStatement("INSERT INTO games (gid, gname, state, timeout) VALUES (?, ?, ?, ?)");
			stmt.setInt(1, gameID);
			stmt.setString(2, name);
			stmt.setShort(3, state);
			stmt.setInt(4, timeout);
			stmt.executeUpdate();
		}
		finally
		{
			closeResources(stmt, null);
		}
	}

	/**
	 * Deletes a game entry from the database with the specified game id
	 * @param gameID An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Gets an array of maps with info for games and the players in them for games with the specified state
	 * and within the specified offset and limit parameters
	 * @param state A state constant declared in the {@code Game} class
	 * @param offset An {@code int}
	 * @param limit An {@code int}
	 * @return An {@code ArrayList} of {@code HashMap}s each one containing the following keys and data mapped
	 * to those keys:
	 * <pre>
	 * "gameid" => (int) - The id of the game
	 * "gamename" => (string) - The name of the game
	 * "timeout" => (int) - The timeout value of the game
	 * "userid" => (int) - The user id of a player in the game
	 * "username" => (string) - The username of a player in the game
	 * "usercolor" => (string) - The color of a player in the game
	 * </pre>
	 * @throws SQLException
	 */
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

	/**
	 * Updates a game with the specified game id with the specified state
	 * @param gameID An {@code int}
	 * @param state A state constant declared in the {@code Game} class
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a player entry in the database with the specified game id, user id and color
	 * @param gameID An {@code int}
	 * @param userID An {@code int}
	 * @param color A {@code String}
	 * @throws SQLException
	 */
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

	/**
	 * Deletes a player entry from the database for the specified game and user identifiers
	 * @param gameID An {@code int}
	 * @param userID An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Deletes player entries for a specified game id
	 * @param gameID An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a chat entry in the database with the specified game id, user id and chat token
	 * @param gameID An {@code int}
	 * @param userID An {@code int}
	 * @param token A {@code String}
	 * @throws SQLException
	 */
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

	/**
	 * Deletes a chat entry from the database for the specified game id and user id
	 * @param gameID An {@code int}
	 * @param userID An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Deletes chat entries from the database for the specified game id
	 * @param gameID An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a result entry in the database for the specified game id
	 * @param gameID An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a result entry in the database with the specified game id, winner user id, loser user id
	 * and a flag specifying if the game finished due to a checkmate
	 * @param gameID An {@code int}
	 * @param winnerUserID An {@code int}
	 * @param loserUserID An {@code int}
	 * @param checkmate A {@code boolean}
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a move entry in the database with the specified game id, user id and move string
	 * @param gameID An {@code int}
	 * @param userID An {@code int}
	 * @param move A {@code String}
	 * @throws SQLException
	 */
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

	/**
	 * Inserts a game save entry in the database with the specified game id, name, game data and timeout
	 * @param gameID An {@code int}
	 * @param name A {@code String}
	 * @param gameData A {@code byte[]}
	 * @param timeout An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Delete a save game entry from the database for the specified game id
	 * @param gameID An {@code int}
	 * @throws SQLException
	 */
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

	/**
	 * Gets a list of all saved games the user with the specified user id is present in along
	 * with info about their opponent with the specified offset and limit
	 * @param userID An {@code int}
	 * @param offset An {@code int}
	 * @param limit An {@code int}
	 * @return An {@code ArrayList} of {@code HashMap}s each containing the following keys and data
	 * mapped to those keys:
	 * <pre>
	 * "gameid" => (int) - The id of the game
	 * "gamename" => (string) - The name of the game
	 * "timeout" => (int) - The timeout value of the game
	 * "userid" => (int) - The user id of the opponent within the game
	 * "username" => (string) - The name of the opponent within the game
	 * "usercolor" => (string) - The color of the opponent within the game
	 * </pre>
	 * @throws SQLException
	 */
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

	/**
	 * Checks whether a user with the specified user is is a player within a saved game with
	 * the specified game id
	 * @param gameID An {@code int}
	 * @param userID An {@code int}
	 * @return True if the user is a player within the game, false if not
	 * @throws SQLException
	 */
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

			if (set.next())
			{
				return set.getInt(1) != 0;
			}

			return false;
		}
		finally
		{
			closeResources(stmt, set);
		}
	}

	/**
	 * Gets name, timeout and game data for a saved game with the specified game id
	 * @param gameID An {@code int}
	 * @return A {@code HashMap} with keys and the data mapped to those keys:
	 * <pre>
	 * "gname" => (string) - The name of the game
	 * "gdata" => (byte[]) - The game data
	 * "timeout" => (int) - The timeout value of the game
	 * </pre>
	 * @throws SQLException
	 */
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
				result.put("timeout", set.getInt(3));

				return result;
			}

			return null;
		}
		finally
		{
			closeResources(stmt, set);
		}
	}

	/**
	 * Inserts a player save entry in the database with the specified game id, user id, color, the time (in milliseconds)
	 * the player has played the game and a flag specifying if they are the next to perform a move within the game
	 * @param gameID An {@code int}
	 * @param userID An {@code int}
	 * @param color A {@code String}
	 * @param next A {@code boolean}
	 * @param millisPlayed A {@code long}
	 * @throws SQLException
	 */
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

	/**
	 * Gets info about the two players present in a saved game with the specified game id
	 * @param gameID An {@code int}
	 * @return An {@code ArrayList} of {@code HashMap}s with keys and data mapped to those keys:
	 * <pre>
	 * "id" => (int) - The user id of the player
	 * "color" => (string) - The color of the player within the game
	 * "next" => (boolean) - True if the player is the next to perform a move within the game, false if not
	 * "played" => (long) - The time (in milliseconds) the player has played the game
	 * </pre>
	 * @throws SQLException
	 */
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

	/**
	 * Deletes a player save entry from the database for the specified game id
	 * @param gameID An {@code int}
	 * @throws SQLException
	 */
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

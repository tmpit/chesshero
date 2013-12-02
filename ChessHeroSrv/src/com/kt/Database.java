package com.kt;

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

    private void connect() throws SQLException
    {
        if (conn != null && !conn.isClosed())
        {
            return;
        }

        String connector = "com.mysql.jdbc.Driver";

        try
        {
            Class.forName(connector).newInstance();
            conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
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
            AuthPair auth = null;

            while (set.next())
            {
                String passHash = set.getString(1);
                int salt = set.getInt(2);
                auth = new AuthPair(passHash, salt);
            }

            return auth;
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

    public boolean insertUser(Credentials credentials) throws SQLException, NoSuchAlgorithmException
    {
        PreparedStatement stmt = null;

        try
        {
            connect();

            stmt = conn.prepareStatement("INSERT INTO users (name, pass, salt) VALUES (?, ?, ?)");
            stmt.setString(1, credentials.getName());
            stmt.setString(2, credentials.getAuthPair().getHash());
            stmt.setInt(3, credentials.getAuthPair().getSalt());

            return (stmt.executeUpdate() == 1);
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

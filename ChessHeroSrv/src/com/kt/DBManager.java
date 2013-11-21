package com.kt;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/16/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
class DBManager
{
    private static String DB_URL = "jdbc:mysql://localhost:3306/";
    private static String DB_NAME = "chesshero";
    private static String DB_USER = "chesshero_srv";
    private static String DB_PASS = "banichkasyssirene";

    private static DBManager singleton = null;

    private Connection conn = null;

    private DBManager() throws ChessHeroException
    {
        String connector = "com.mysql.jdbc.Driver";

        try
        {
            Class.forName(connector).newInstance();
            conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
        }
        catch (Exception e)
        {
            SLog.write("Could not establish database connection: " + e);
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
    }

    public static synchronized DBManager getSingleton() throws ChessHeroException
    {
        if (null == singleton)
        {
            singleton = new DBManager();
        }

        return singleton;
    }

    public synchronized AuthPair getAuthPair(String username) throws SQLException
    {
        AuthPair auth = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT pass, salt FROM users WHERE name = ?");
        stmt.setString(1, username);

        ResultSet set = stmt.executeQuery();
        while (set.next())
        {
            String passHash = set.getString(1);
            int salt = set.getInt(2);
            auth = new AuthPair(passHash, salt);
        }

        stmt.close();

        return auth;
    }

    public synchronized boolean insertUser(Credentials credentials) throws SQLException, NoSuchAlgorithmException
    {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, pass, salt) VALUES (?, ?, ?)");
        stmt.setString(1, credentials.getName());
        stmt.setString(2, credentials.getAuthPair().getHash());
        stmt.setInt(3, credentials.getAuthPair().getSalt());
        int val = stmt.executeUpdate();

        return (val == 1);
    }
}

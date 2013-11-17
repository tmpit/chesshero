package com.kt;

import java.sql.Connection;
import java.sql.DriverManager;

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
}

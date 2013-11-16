package com.kt;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/16/13
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */

public class Credentials
{
    public static final short MIN_NAME_LENGTH = 3;
    public static final short MAX_NAME_LENGTH = 50;
    public static final short MIN_PASS_LENGTH = 3;
    public static final short MAX_PASS_LENGTH = 50;

    private String name;
    private String pass;

    Credentials(String name, String pass)
    {
        this.name = name;
        this.pass = pass;
    }

    public String getName()
    {
        return name;
    }

    public String getPass()
    {
        return pass;
    }
}

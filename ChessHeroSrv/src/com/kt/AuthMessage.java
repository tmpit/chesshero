package com.kt;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/16/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthMessage extends Message
{
    private int action;
    private Credentials credentials;

    AuthMessage(int action, Credentials credentials)
    {
        this.action = action;
        this.credentials = credentials;
    }

    public int getAction()
    {
        return action;
    }

    public Credentials getCredentials()
    {
        return credentials;
    }
}

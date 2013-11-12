package com.kt;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/12/13
 * Time: 1:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class Message
{
    public static final int HEADER_LENGTH = 2;
    public static enum ActionCode
    {
        REGISTER,
        LOGIN,
        MOVE
    }

    public static Message fromData(byte data[])
    {
        return new Message();
    }

    public byte[] toData()
    {
        return null;
    }
}

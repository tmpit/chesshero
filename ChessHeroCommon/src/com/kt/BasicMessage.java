package com.kt;

/**
 * Created by Toshko on 12/6/13.
 */
public class BasicMessage extends Message
{
    public BasicMessage(byte type)
    {
        this(type, (byte)0);
    }

    public BasicMessage(byte type, byte flags)
    {
        super(type, flags);
    }

    @Override
    protected byte[] serialized()
    {
        byte data[] = {type, flags};
        return data;
    }
}

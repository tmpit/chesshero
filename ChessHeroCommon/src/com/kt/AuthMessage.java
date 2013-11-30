package com.kt;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/16/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthMessage extends Message
{
    private Credentials credentials;

    public AuthMessage(byte type, Credentials credentials)
    {
        super(type, (byte)0);

        this.credentials = credentials;
    }

    public AuthMessage(byte type, byte flags, Credentials credentials)
    {
        super(type, flags);

        this.credentials = credentials;
    }

    public Credentials getCredentials()
    {
        return credentials;
    }

    @Override
    public byte[] toData()
    {
        byte nameData[] = credentials.getName().getBytes();
        byte passData[] = credentials.getPass().getBytes();

        int bodyLen = 1 + 1 + 2 + nameData.length + 2 + passData.length; // Type + flags + name length + name + pass length + pass

        ByteBuffer messageData = ByteBuffer.allocate(bodyLen);
        messageData.put(type); // Put type
        messageData.put(flags); // Put flags
        messageData.putShort((short)nameData.length); // Put name length
        messageData.put(nameData); // Put name
        messageData.putShort((short)passData.length); // Put pass length
        messageData.put(passData); // Put pass

        return messageData.array();
    }

    @Override
    public String toString()
    {
        return "<AuthMessage :: type: " + type + ", flags: " + flags + ", credentials: " + credentials + ">";
    }
}

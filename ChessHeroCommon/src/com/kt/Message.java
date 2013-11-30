package com.kt;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/12/13
 * Time: 1:07 AM
 * To change this template use File | Settings | File Templates.
 */

abstract public class Message
{
    public static final byte TYPE_REGISTER = 1;
    public static final byte TYPE_LOGIN = 2;
    public static final byte TYPE_MOVE = 3;
    public static final byte TYPE_RESULT = 4;

    public static final byte FLAG_PUSH = 1 << 0;

    protected byte type;
    protected byte flags = 0;

    public static boolean isTypeValid(short type)
    {
        return (type == TYPE_REGISTER || type == TYPE_LOGIN || type == TYPE_MOVE);
    }

    protected Message(byte type, byte flags)
    {
        this.type = type;
        this.flags = flags;
    }

    public byte getType()
    {
        return type;
    }

    public byte getFlags()
    {
        return flags;
    }

    public static Message fromData(byte data[]) throws ChessHeroException
    {
        SLog.write("Parsing message");

        ByteBuffer buf = ByteBuffer.allocate(data.length);
        buf.put(data);
        buf.rewind();

        try
        {
            // Read type and flags
            byte type = buf.get();
            byte flags = buf.get();
            SLog.write("Message type: " + type);

            switch (type)
            {
                case TYPE_REGISTER:
                case TYPE_LOGIN:
                    Credentials credentials = readCredentials(buf);
                    return new AuthMessage(type, flags, credentials);

                case TYPE_RESULT:
                    int result = buf.getInt();
                    return new ResultMessage(result, flags);

                case TYPE_MOVE:
                    return null;

                default:
                    throw new ChessHeroException(Result.INVALID_TYPE);
            }
        }
        catch (BufferUnderflowException e)
        {
            SLog.write(e);
            throw new ChessHeroException(Result.INVALID_MESSAGE);
        }
    }

    private static Credentials readCredentials(ByteBuffer buf) throws ChessHeroException
    {
        SLog.write("Parsing credentials");

        try
        {
            short nameLen = buf.getShort();

            // Read name bytes
            byte nameData[] = new byte[nameLen];
            buf.get(nameData, 0, nameLen);

            short passLen = buf.getShort();

            // Read pass bytes
            byte passData[] = new byte[passLen];
            buf.get(passData, 0, passLen);

            return new Credentials(new String(nameData), new String(passData));
        }
        catch (BufferUnderflowException e)
        {
            SLog.write(e);
            throw new ChessHeroException(Result.INVALID_MESSAGE);
        }
    }

    @Override
    public String toString()
    {
        return "<Message :: type: " + type + ", flags: " + flags + ">";
    }

    abstract public byte[] toData();
}

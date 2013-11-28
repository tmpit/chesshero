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
    public static final short TYPE_REGISTER = 1;
    public static final short TYPE_LOGIN = 2;
    public static final short TYPE_MOVE = 3;
    public static final short TYPE_RESULT = 4;

    protected short type;

    public static boolean isTypeValid(int type)
    {
        return (type == TYPE_REGISTER || type == TYPE_LOGIN || type == TYPE_MOVE);
    }

    protected Message(short type)
    {
        this.type = type;
    }

    public short getType()
    {
        return type;
    }

    public static Message fromData(byte data[]) throws ChessHeroException
    {
        SLog.write("Parsing message");

        ByteBuffer buf = ByteBuffer.allocate(data.length);
        buf.put(data);
        buf.rewind();

        try
        {
            // Read type
            short type = buf.getShort();
            SLog.write("Message type: " + type);

            switch (type)
            {
                case TYPE_REGISTER:
                case TYPE_LOGIN:
                    Credentials credentials = readCredentials(buf);
                    return new AuthMessage(type, credentials);

                case TYPE_RESULT:
                    int result = buf.getInt();
                    return new ResultMessage(result);

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
            if (nameLen < Credentials.MIN_NAME_LENGTH || nameLen > Credentials.MAX_NAME_LENGTH)
            {
                throw new ChessHeroException(Result.INVALID_NAME);
            }

            // Read name bytes
            byte nameData[] = new byte[nameLen];
            buf.get(nameData, 0, nameLen);

            short passLen = buf.getShort();
            if (passLen < Credentials.MIN_PASS_LENGTH || passLen > Credentials.MAX_PASS_LENGTH)
            {
                throw new ChessHeroException(Result.INVALID_PASS);
            }

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

    abstract public byte[] toData();
}

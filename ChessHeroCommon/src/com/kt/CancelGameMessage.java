package com.kt;

/**
 * Created by Toshko on 12/7/13.
 */
public class CancelGameMessage extends Message
{
    private int gameID;

    public CancelGameMessage(int gameID)
    {
        this(gameID, (byte)0);
    }

    public CancelGameMessage(int gameID, byte flags)
    {
        super(TYPE_CANCEL_GAME, flags);

        this.gameID = gameID;
    }

    public int getGameID()
    {
        return gameID;
    }

    @Override
    protected byte[] serialized()
    {
        byte data[] = new byte[1 + 1 + 4]; // Type + flags + gameID
        data[0] = type;
        data[1] = flags;
        Utils.bytesPutInt(data, gameID, 2);

        return data;
    }

    @Override
    public String toString()
    {
        return "<CancelGameMessage :: flags: " + flags + ", gameid: " + gameID + ">";
    }
}

package com.kt;

/**
 * Created by Toshko on 12/6/13.
 */
public class CreateGameMessage extends Message
{
    private String name;

    public CreateGameMessage(String name)
    {
        this(name, (byte) 0);

        this.name = name;
    }

    public CreateGameMessage(String name, byte flags)
    {
        super(TYPE_CREATE_GAME, flags);

        this.name = name;
    }
    public String getName()
    {
        return name;
    }

    protected byte[] serialized()
    {
        byte nameData[] = name.getBytes();
        short nameLen = (short)nameData.length;
        byte data[] = new byte[1 + 1 + 2 + nameLen];

        data[0] = type;
        data[1] = flags;
        Utils.bytesPutShort(data, nameLen, 2);
        Utils.bytesPutBytes(data, nameData, 4);

        return data;
    }
}

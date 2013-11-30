package com.kt;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/29/13
 * Time: 12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResultMessage extends Message
{
    private int result;

    public ResultMessage(int result)
    {
        super(TYPE_RESULT, (byte)0);

        this.result = result;
    }

    public ResultMessage(int result, byte flags)
    {
        super(TYPE_RESULT, flags);

        this.result = result;
    }

    public int getResult()
    {
        return result;
    }

    @Override
    public byte[] toData()
    {
        byte data[] = new byte[1 + 1 + 4]; // Type + flags + result
        data[0] = type;
        data[1] = flags;
        Utils.bytesPutInt(data, result, 2);

        return data;
    }

    @Override
    public String toString()
    {
        return "<ResultMessage :: flags: " + flags + ", result: " + result + ">";
    }
}

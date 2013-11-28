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
        super(TYPE_RESULT);

        this.result = result;
    }

    public int getResult()
    {
        return result;
    }

    @Override
    public byte[] toData()
    {
        byte data[] = new byte[2 + 4]; // Type + result
        Utils.bytesPutShort(data, type, 0);
        Utils.bytesPutInt(data, result, 2);

        return data;
    }
}

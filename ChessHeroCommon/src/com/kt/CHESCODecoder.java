package com.kt;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;

/**
 * Created by Toshko on 12/7/13.
 */
public class CHESCODecoder
{
    private CHESCOReader reader;

    public CHESCODecoder(byte data[])
    {
        reader = new CHESCOReader(data);
    }

    public void setData(byte data[])
    {
        reader.setData(data);
    }

    private Object parse()
    {
        byte type = reader.get();

        switch (type)
        {
            case CHESCO.TYPE_BOOL:
                return new Boolean((reader.get() != 0 ? true : false));

            case CHESCO.TYPE_INT:
                byte intData[] = reader.get(4);
                return new Integer(Utils.intFromBytes(intData, 0));

            case CHESCO.TYPE_STR:
                short length = Utils.shortFromBytes(reader.get(2), 0);
                try
                {
                    return new String(reader.get(length), "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    SLog.write("Exception raised while parsing: " + e);
                    return "";
                }

            case CHESCO.TYPE_ARR:
                short count = Utils.shortFromBytes(reader.get(2), 0);
                ArrayList list = new ArrayList(count);

                for (int i = 0; i < count; i++)
                {
                    list.add(parse());
                }

                return list;

            case CHESCO.TYPE_MAP:
                short pairsCount = Utils.shortFromBytes(reader.get(2), 0);
                HashMap<String, Object> map = new HashMap<String, Object>(pairsCount);

                for (int i = 0; i < pairsCount; i++)
                {
                    String key = (String)parse();
                    Object val = parse();
                    map.put(key, val);
                }

                return map;
        }

        throw new InputMismatchException("Unsupported type");
    }
}

class CHESCOReader
{
    private byte data[];
    private int length;
    private int index;

    public CHESCOReader(byte data[])
    {
        setData(data);
    }

    public void setData(byte data[])
    {
        this.data = data;
        length = data.length;
        index = 0;
    }

    public byte get() throws BufferUnderflowException
    {
        if (index >= length)
        {
            throw new BufferUnderflowException();
        }

        return data[index++];
    }

    public boolean isEmpty()
    {
        return index >= length;
    }

    public byte[] get(int count) throws BufferUnderflowException
    {
        if (index >= length)
        {
            throw new BufferUnderflowException();
        }

        byte bytes[] = new byte[count];
        System.arraycopy(data, index, bytes, 0, count);
        index += count;

        return bytes;
    }
}

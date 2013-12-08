package com.kt.chesco;

import com.kt.utils.SLog;
import com.kt.utils.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;

/**
 * Created by Toshko on 12/7/13.
 */
public class CHESCOReader
{
    private CHESCOStreamReader reader;

    public CHESCOReader(InputStream stream)
    {
        reader = new CHESCOStreamReader(stream);
    }

    public Object read() throws InputMismatchException, IOException
    {
        int type = reader.get();

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
                ArrayList<Object> list = new ArrayList<Object>(count);

                for (int i = 0; i < count; i++)
                {
                    list.add(read());
                }

                return list;

            case CHESCO.TYPE_MAP:
                short pairsCount = Utils.shortFromBytes(reader.get(2), 0);
                HashMap<String, Object> map = new HashMap<String, Object>(pairsCount);

                for (int i = 0; i < pairsCount; i++)
                {
                    String key = (String)read();
                    Object val = read();
                    map.put(key, val);
                }

                return map;
        }

        throw new InputMismatchException("Unsupported type");
    }
}

class CHESCOStreamReader
{
    private InputStream istream;

    public CHESCOStreamReader(InputStream stream)
    {
        istream = stream;
    }

    public int get() throws IOException
    {
        int aByte = istream.read();

        if (-1 == aByte)
        {
            throw new EOFException();
        }

        return aByte;
    }

    public byte[] get(int count) throws IOException
    {
        byte bytes[] = new byte[count];
        int read = 0;

        do
        {
            read = istream.read(bytes, read, count - read);

            if (-1 == read)
            {
                throw new EOFException();
            }
        }
        while (read < count);

        return bytes;
    }
}

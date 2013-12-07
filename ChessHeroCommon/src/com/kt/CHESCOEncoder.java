package com.kt;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Toshko on 12/7/13.
 */
public class CHESCOEncoder
{
    private void write(String str, ByteArrayOutputStream stream)
    {
        try
        {
            byte strData[] = str.getBytes("UTF-8");
            int dataLen = strData.length;

            if (dataLen > Short.MAX_VALUE)
            {
                dataLen = Short.MAX_VALUE;
            }

            stream.write(CHESCO.TYPE_STR);
            stream.write(Utils.bytesFromShort((short)dataLen), 0, 2);
            stream.write(strData, 0, dataLen);
        }
        catch (UnsupportedEncodingException e)
        {
            SLog.write("Exception while serializing string: " + e);
        }
    }

    private void write(Integer i, ByteArrayOutputStream stream)
    {
        stream.write(CHESCO.TYPE_INT);
        stream.write(Utils.bytesFromInt(i.intValue()), 0, 4);
    }

    private void write(Boolean b, ByteArrayOutputStream stream)
    {
        stream.write(CHESCO.TYPE_BOOL);
        stream.write((b.booleanValue() ? 1 : 0));
    }

    private void write(Collection collection, ByteArrayOutputStream stream) throws InputMismatchException
    {
        byte count[] = Utils.bytesFromShort((short)collection.size());

        stream.write(CHESCO.TYPE_ARR);
        stream.write(count, 0, 2);

        for (Object val : collection)
        {
            if (val instanceof String)
            {
                write((String) val, stream);
            }
            else if (val instanceof Integer)
            {
                write((Integer) val, stream);
            }
            else if (val instanceof Boolean)
            {
                write((Boolean) val, stream);
            }
            else if (val instanceof Map)
            {
                write((Map)val, stream);
            }
            else if (val instanceof Collection)
            {
                write((Collection)val, stream);
            }
            else
            {
                throw new InputMismatchException("Attempting to serialize object not supported by CHESCO");
            }
        }
    }

    private void write(Map map, ByteArrayOutputStream stream) throws InputMismatchException
    {
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        byte count[] = Utils.bytesFromShort((short)entrySet.size());

        stream.write(CHESCO.TYPE_MAP);
        stream.write(count, 0, 2);

        for (Map.Entry<String, Object> entry : entrySet)
        {
            String key = entry.getKey();

            write(key, stream);

            Object val = entry.getValue();

            if (val instanceof String)
            {
                write((String)val, stream);
            }
            else if (val instanceof Integer)
            {
                write((Integer)val, stream);
            }
            else if (val instanceof Boolean)
            {
                write((Boolean)val, stream);
            }
            else if (val instanceof Map)
            {
                write((Map)val, stream);
            }
            else if (val instanceof Collection)
            {
                write((Collection)val, stream);
            }
            else
            {
                throw new InputMismatchException("Attempting to serialize object not supported by CHESCO");
            }
        }
    }

    public byte[] serialize(Map<String, Object> map)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(256);

        write(map, stream);

        return stream.toByteArray();
    }

    public byte[] serialize(Collection collection)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(256);

        write(collection, stream);

        return stream.toByteArray();
    }
}

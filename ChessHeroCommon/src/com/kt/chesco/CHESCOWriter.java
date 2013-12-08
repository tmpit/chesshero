package com.kt.chesco;

import com.kt.utils.SLog;
import com.kt.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Toshko on 12/7/13.
 */
public class CHESCOWriter
{
    private OutputStream ostream;

    public CHESCOWriter(OutputStream stream)
    {
        this.ostream = stream;
    }

    private void write(String str) throws IOException
    {


        try
        {
            byte strData[] = str.getBytes("UTF-8");
            int dataLen = strData.length;

            if (dataLen > Short.MAX_VALUE)
            {
                dataLen = Short.MAX_VALUE;
            }

            ostream.write(CHESCO.TYPE_STR);
            ostream.write(Utils.bytesFromShort((short) dataLen), 0, 2);
            ostream.write(strData, 0, dataLen);
        }
        catch (UnsupportedEncodingException e)
        {
            SLog.write("Exception while serializing string: " + e);
        }
    }

    private void write(Integer i) throws IOException
    {
        ostream.write(CHESCO.TYPE_INT);
        ostream.write(Utils.bytesFromInt(i.intValue()), 0, 4);
    }

    private void write(Boolean b) throws IOException
    {
        ostream.write(CHESCO.TYPE_BOOL);
        ostream.write((b.booleanValue() ? 1 : 0));
    }

    public void write(Collection collection) throws InputMismatchException, IOException
    {
        int entriesCount = collection.size();
        byte count[] = Utils.bytesFromShort((short)entriesCount);

        ostream.write(CHESCO.TYPE_ARR);
        ostream.write(count, 0, 2);

        int entriesWritten = 0;

        for (Object val : collection)
        {
            if (entriesWritten >= entriesCount)
            {   // Making sure no more than entriesCount entries are written to the stream
                break;
            }

            if (val instanceof String)
            {
                write((String) val);
            }
            else if (val instanceof Integer)
            {
                write((Integer) val);
            }
            else if (val instanceof Boolean)
            {
                write((Boolean) val);
            }
            else if (val instanceof Map)
            {
                write((Map)val);
            }
            else if (val instanceof Collection)
            {
                write((Collection)val);
            }
            else
            {
                throw new InputMismatchException("Attempting to serialize object not supported by CHESCO");
            }

            entriesWritten++;
        }
    }

    public void write(Map map) throws InputMismatchException, IOException
    {
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        int entriesCount = entrySet.size();

        if (entriesCount > Short.MAX_VALUE)
        {
            entriesCount = Short.MAX_VALUE;
        }

        byte count[] = Utils.bytesFromShort((short)entriesCount);

        ostream.write(CHESCO.TYPE_MAP);
        ostream.write(count, 0, 2);

        int entriesWritten = 0;

        for (Map.Entry<String, Object> entry : entrySet)
        {
            if (entriesWritten >= entriesCount)
            {   // Making sure no more than entriesCount entries are written to the stream
                break;
            }

            String key = entry.getKey();

            write(key);

            Object val = entry.getValue();

            if (val instanceof String)
            {
                write((String)val);
            }
            else if (val instanceof Integer)
            {
                write((Integer)val);
            }
            else if (val instanceof Boolean)
            {
                write((Boolean)val);
            }
            else if (val instanceof Map)
            {
                write((Map)val);
            }
            else if (val instanceof Collection)
            {
                write((Collection)val);
            }
            else
            {
                throw new InputMismatchException("Attempting to serialize object that is not supported by CHESCO");
            }

            entriesWritten++;
        }
    }
}

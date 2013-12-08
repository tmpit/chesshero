package com.kt;

import com.kt.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Toshko on 12/6/13.
 */
public class MapMessage extends Message
{
    public static final byte VAL_TYPE_INT = 1;
    public static final byte VAL_TYPE_STR = 2;
    public static final byte MAP_END = -128;

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public MapMessage()
    {
        this((byte)0);
    }

    public MapMessage(byte flags)
    {
        super(TYPE_MAP, flags);
    }

    public MapMessage(HashMap<String, Object> map, byte flags)
    {
        super(TYPE_MAP, flags);
        this.map.putAll(map);
    }

    public void set(String key, String val)
    {
        map.put(key, val);
    }

    public void set(String key, Integer val)
    {
        map.put(key, val);
    }

    public Object get(String key)
    {
        return map.get(key);
    }

    @Override
    protected byte[] serialized()
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(128);
        stream.write(type);
        stream.write(flags);

        Set<String> keys = map.keySet();

        for (String key : keys)
        {
            byte keyData[] = key.getBytes();
            int keyLen = keyData.length;

            if (keyLen > 255)
            {   // Value cannot be put into a byte - skip
                continue;
            }

            // Write key info
            stream.write(VAL_TYPE_STR);
            stream.write((byte)keyLen);
            stream.write(keyData, 0, keyLen);

            Object val = map.get(key);

            byte valData[];

            if (val instanceof String)
            {
                valData = ((String)val).getBytes();
                int valLen = valData.length;

                if (valLen > 255)
                {   // Value cannot be put into a byte - skip
                    continue;
                }

                // Write val info
                stream.write(VAL_TYPE_STR);
                stream.write((byte)valLen);
                stream.write(valData, 0, valLen);
            }
            else if (val instanceof Integer)
            {
                valData = Utils.bytesFromInt(((Integer) val).intValue());

                // Write val info
                stream.write(VAL_TYPE_INT);
                stream.write(valData, 0, 4);
            }
        }

        stream.write(MAP_END);

        return stream.toByteArray();
    }

    @Override
    public String toString()
    {
        return "<MapMessage :: flags: " + flags + ", map: " + map.entrySet() + ">";
    }
}

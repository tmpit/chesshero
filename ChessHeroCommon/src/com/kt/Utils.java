package com.kt;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/24/13
 * Time: 12:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class Utils
{
    public static short shortFromBytes(byte bytes[], int index)
    {
        return (short)((bytes[index] << 8) | (bytes[index + 1] & 0xFF));
    }

    public static byte[] bytesFromShort(short s)
    {
        byte data[] = new byte[2];
        data[0] = (byte)((s >>> 8) & 0xFF);
        data[1] = (byte)(s & 0xFF);

        return data;
    }

    // Writes 's' as two bytes into 'bytes' starting at index 'index'
    public static void bytesPutShort(byte bytes[], short s, int index)
    {
        bytes[index] = (byte)((s >>> 8) & 0xFF);
        bytes[index + 1] = (byte)(s & 0xFF);
    }

    // Writes all bytes from 'data' into 'bytes' starting at index 'index'
    public static void bytesPutBytes(byte bytes[], byte data[], int index)
    {
        int dataLen = data.length;
        for (int i = 0, j = index; i < dataLen; i++, j++)
        {
            bytes[j] = data[i];
        }
    }
}

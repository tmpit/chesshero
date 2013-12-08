package com.kt.utils;

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

    public static int intFromBytes(byte bytes[], int index)
    {
        int num = bytes[index + 3] & 0xFF;
        num |= (bytes[index + 2] << 8) & 0xFF00;
        num |= (bytes[index + 1] << 16) & 0xFF0000;
        num |= (bytes[index] << 24) & 0xFF000000;

        return num;
    }

    public static byte[] bytesFromInt(int i)
    {
        byte data[] = new byte[4];
        data[0] = (byte)((i >>> 24) & 0xFF);
        data[1] = (byte)((i >>> 16) & 0xFF);
        data[2] = (byte)((i >>> 8) & 0xFF);
        data[3] = (byte)(i & 0xFF);

        return data;
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
        bytes[index] = (byte)(s >>> 8);
        bytes[index + 1] = (byte)s;
    }

    // Writes 'i' as four bytes into 'bytes' starting at index 'index'
    public static void bytesPutInt(byte bytes[], int i, int index)
    {
        bytes[index] = (byte)(i >>> 24);
        bytes[index + 1] = (byte)(i >>> 16);
        bytes[index + 2] = (byte)(i >>> 8);
        bytes[index + 3] = (byte)i;
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

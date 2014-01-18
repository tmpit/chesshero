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
 * The CHESCOReader class is used to read raw data from an input stream and parse it as per the CHESCO messaging protocol
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 * @see com.kt.chesco.CHESCO
 */
public class CHESCOReader
{
    private CHESCOStreamReader reader;

	/**
	 * Initializes a newly created {@code CHESCOReader} object with an {@code InputStream} object it can read from
	 * @param stream An {@code InputStream} object this reader can read from
	 */
    public CHESCOReader(InputStream stream)
    {
        reader = new CHESCOStreamReader(stream);
    }

	/**
	 * Reads and parses a CHESCO message from this reader's input stream
	 * @return {@code Object} that can be either a {@code HashMap} or an {@code ArrayList}
	 * @throws InputMismatchException Thrown when the format of the received data does not conform to CHESCO
	 * @throws IOException
	 */
    public Object read() throws InputMismatchException, IOException
    {
        int type = reader.get();

        switch (type)
        {
			case CHESCO.TYPE_NULL:
				return null;

            case CHESCO.TYPE_BOOL:
                return new Boolean(reader.get() != 0);

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

/**
 * The CHESCOStreamReader class is used to conveniently read bytes from an input stream
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
class CHESCOStreamReader
{
    private InputStream istream;

	/**
	 * Initializes a newly created {@code CHESCOStreamReader} object with an {@code InputStream}
	 * object it can read from
	 * @param stream The {@code InputStream} object this reader can read from
	 */
    public CHESCOStreamReader(InputStream stream)
    {
        istream = stream;
    }

	/**
	 * Reads one byte from this reader's input stream
	 * @return The byte that was read
	 * @throws IOException
	 */
    public int get() throws IOException
    {
        int aByte = istream.read();

        if (-1 == aByte)
        {
            throw new EOFException();
        }

        return aByte;
    }

	/**
	 * Reads {@code count} number of bytes from this reader's input stream
	 * @param count The number of bytes to read
	 * @return The array of bytes read. The length of the array is always {@code count}
	 * @throws IOException
	 */
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

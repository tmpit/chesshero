import com.kt.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Toshko on 12/7/13.
 */
public class Main
{
    static Socket sock;

    public static void main(String []args)
    {
        try
        {
            sock = new Socket(InetAddress.getLocalHost(), 4848);
            writeMessage(new AuthMessage(Message.TYPE_LOGIN, new Credentials("tttt", "pppp")));
            readMessage();
        }
        catch (Throwable e)
        {
            System.out.println("Exception: " + e);
        }
    }

    public static byte[] readBytesWithLength(int len) throws IOException, EOFException
    {
        InputStream stream = sock.getInputStream();
        int bytesRead;

        byte data[] = new byte[len];

        do
        {   // The docs are ambiguous as to whether this will definitely try to read len or can return less than len
            // so just in case iterating until len is read or shit happens
            bytesRead = stream.read(data, 0, len);
            if (-1 == bytesRead)
            {
                throw new EOFException();
            }
        }
        while (bytesRead != len);

        return data;
    }

    public static void writeMessage(Message msg)
    {
        try
        {
            byte body[] = msg.toData();
            byte header[] = Utils.bytesFromShort((short) body.length);

            sock.getOutputStream().write(header);
            sock.getOutputStream().write(body);

            SLog.write("Message written");
        }
        catch (IOException e)
        {
            SLog.write("Exception raised while writing to socket: " + e);
        }
    }

    public static Message readMessage()
    {
        try
        {
            byte header[] = readBytesWithLength(2);
            short bodyLen = Utils.shortFromBytes(header, 0);
            byte body[] = readBytesWithLength(bodyLen);

            Message msg = Message.fromData(body);
            SLog.write("Message read: " + msg);

            if ((msg.getFlags() & Message.FLAG_INNERMSG) != 0)
            {
                SLog.write("Inner message: " + msg.getInnerMessage());
            }
            return msg;
        }
        catch (IOException e)
        {
            SLog.write("Exception while reading: " + e);
        }
        catch (ChessHeroException e)
        {
            SLog.write("Exception while reading: " + e);
        }

        return null;
    }
}

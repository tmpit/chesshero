package com.kt;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/9/13
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientConnection extends Thread
{
    public final static int READ_TIMEOUT = 15 * 1000; // In milliseconds

    private Socket sock = null;

    ClientConnection(Socket sock)
    {
        this.sock = sock;
    }

    private void closeSocket()
    {
        SLog.write("Closing socket...");

        if (sock.isClosed())
        {
            SLog.write("Socket already closed");
            return;
        }

        try
        {
            sock.close();
            SLog.write("Socket closed");
        }
        catch (IOException e)
        {
            SLog.write("Error closing socket: " + e.getMessage());
        }
    }

    public void run()
    {
        try
        {
            // Set timeout for the first message, if someone connected, he must say something
            sock.setSoTimeout(READ_TIMEOUT);

            while (true)
            {
                SLog.write("Reading header");

                // Read header
                byte header[] = readBytesWithLength(2);
                short bodyLen = Utils.shortFromBytes(header, 0);

                SLog.write("Header read, body length is: " + bodyLen);

                if (0 == bodyLen)
                {   // An error has occurred during header reading or header is invalid, end the task
                    closeSocket();
                    return;
                }

                // Set timeout for the body
                sock.setSoTimeout(READ_TIMEOUT);

                SLog.write("Reading body");

                // Read body
                byte body[] = readBytesWithLength(bodyLen);

                SLog.write("Body read");

                Message msg = Message.fromData(body);

                SLog.write("Received message: " + msg);
                // Pass message wherever

                // Remove timeout when listening for header
                sock.setSoTimeout(0);
            }
        }
        catch (SocketTimeoutException e)
        {
            SLog.write("Read timed out");
            closeSocket();
        }
        catch (EOFException e)
        {
            SLog.write("Socket reached unexpected EOF??? - " + e);
            closeSocket();
        }
        catch (IOException e)
        {
            SLog.write("Error reading: " + e);
            closeSocket();
        }
        catch (ChessHeroException e)
        {
            SLog.write("Parsing error, code: " + e.getCode());
            closeSocket();
        }
        catch (Exception e)
        {
            SLog.write("Surprise exception: " + e);
            closeSocket();
        }
    }

    private byte[] readBytesWithLength(int len) throws IOException, EOFException
    {
        InputStream stream = sock.getInputStream();
        int bytesRead = 0;
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
}

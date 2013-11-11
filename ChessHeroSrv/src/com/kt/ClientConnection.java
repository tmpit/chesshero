package com.kt;

import java.io.EOFException;
import java.io.IOException;
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
public class ClientConnection implements Runnable
{
    private Socket sock = null;
    private Parser parser = null;

    ClientConnection(Socket sock)
    {
        try
        {
            sock.setSoTimeout(Config.SILENCE_TIMEOUT);
            this.sock = sock;
        }
        catch (SocketException e)
        {
            SLog.write("Error setting socket timeout: " + e.getMessage());
            closeConnection();
        }
    }

    private Parser getParser()
    {
        if (null == parser)
        {
            parser = new Parser();
        }

        return parser;
    }

    private void closeConnection()
    {
        try
        {
            SLog.write("Closing connection...");
            sock.close();
            SLog.write("Connection closed");
        }
        catch (IOException e)
        {
            SLog.write("Error closing connection: " + e.getMessage());
        }
    }

    public void run()
    {
        if (null == sock)
        {   // Set timeout in the constructor hasn't succeeded, end the task
            return;
        }

        try
        {   // The first byte will be the body length
            int bodyLen = sock.getInputStream().read();
            sock.setSoTimeout(0); // Reset timeout back to no timeout after we've read the request header

            SLog.write("body len: " + bodyLen);

            readBodyWithLength(bodyLen);
        }
        catch (SocketTimeoutException e)
        {
            SLog.write("Initial timeout reached, closing socket");
            closeConnection();
        }
        catch (IOException e)
        {
            SLog.write("Error reading: " + e.getMessage());
            closeConnection();
        }
    }

    public void readBodyWithLength(int len)
    {
        byte bodyData[] = new byte[len];

        try
        {
            int bytesRead = 0;

            do
            {   // The docs are ambiguous as to whether this will definitely try to read len or can return less than len
                // so just in case iterating until body is read or shit happens
                bytesRead = sock.getInputStream().read(bodyData, 0, len);

                if (-1 == bytesRead)
                {   // -1 == OEF
                    throw new EOFException();
                }
            }
            while (bytesRead != len);

            Object message = getParser().messageFromData(bodyData);
        }
        catch (EOFException e)
        {
            SLog.write("Unexpected end of file reached while reading from socket");
            closeConnection();
        }
        catch (IOException e)
        {
            SLog.write("Could not read body of len: " + len);
            closeConnection();
        }
    }
}

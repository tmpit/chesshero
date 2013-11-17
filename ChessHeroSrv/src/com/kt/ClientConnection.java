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

    ClientConnection(Socket sock)
    {
        this.sock = sock;
    }

    private void closeConnection()
    {
        if (sock.isClosed())
        {
            return;
        }

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
        try
        {
            // Set timeout for the first message, if someone connected, he must say something
            sock.setSoTimeout(Config.READ_TIMEOUT);

            while (true)
            {
                short bodyLen = readHeader();

                if (0 == bodyLen)
                {   // An error has occurred during header reading or header is invalid, end the task
                    closeConnection();
                    return;
                }

                // Set timeout for the body
                sock.setSoTimeout(Config.READ_TIMEOUT);

                byte bodyData[] = readBodyWithLength(bodyLen);

                if (0 == bodyData.length)
                {   // An error has occurred during body reading, end the task
                    closeConnection();
                    return;
                }

                Message msg = Message.fromData(bodyData);
                SLog.write("Received message: " + msg);
                // Pass message wherever

                // Remove timeout when listening for header
                sock.setSoTimeout(0);
            }
        }
        catch (IOException e)
        {
            SLog.write("Could not set socket timeout: " + e);
            closeConnection();
        }
        catch (ChessHeroException e)
        {
            SLog.write("Parsing error, code: " + e.getCode());
            closeConnection();
        }
        catch (Exception e)
        {
            SLog.write("Unhandled exception: " + e);
            closeConnection();
        }
    }

    private short readHeader()
    {
        try
        {   // The first two bytes will be the body length
            byte headerData[] = new byte[2];
            int bytesRead = 0;

            do
            {   // The docs are ambiguous as to whether this will definitely try to read len or can return less than len
                // so just in case iterating until len is read or shit happens
                bytesRead = sock.getInputStream().read(headerData, 0, 2);
                if (-1 == bytesRead)
                {
                    throw new EOFException();
                }
            }
            while (bytesRead != 2);

            ByteBuffer buf = ByteBuffer.allocate(2);
            buf.put(headerData);

            return buf.getShort();
        }
        catch (EOFException e)
        {
            SLog.write("Unexpected end of file reached while reading");
            closeConnection();
        }
        catch (SocketTimeoutException e)
        {
            SLog.write("Header read timed out");
            closeConnection();
        }
        catch (IOException e)
        {
            SLog.write("Error reading: " + e.getMessage());
            closeConnection();
        }

        return 0;
    }

    private byte[] readBodyWithLength(short len)
    {
        byte bodyData[] = new byte[len];

        try
        {
            int bytesRead = 0;

            do
            {   // The docs are ambiguous as to whether this will definitely try to read len or can return less than len
                // so just in case iterating until len is read or shit happens
                bytesRead = sock.getInputStream().read(bodyData, 0, len);

                if (-1 == bytesRead)
                {   // -1 == OEF
                    throw new EOFException();
                }
            }
            while (bytesRead != len);
        }
        catch (EOFException e)
        {
            SLog.write("Unexpected end of file reached while reading");
            closeConnection();
        }
        catch (SocketTimeoutException e)
        {
            SLog.write("Body read timed out");
            closeConnection();
        }
        catch (IOException e)
        {
            SLog.write("Could not read body of len: " + len);
            closeConnection();
        }

        return bodyData;
    }
}

package com.kt;

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
        try
        {
            sock.setSoTimeout(Config.SILENCE_TIMEOUT);
            this.sock = sock;
        }
        catch (SocketException e)
        {
            SLog.write("Error setting socket timeout: " + e.getMessage());
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
        }
        catch (SocketTimeoutException e)
        {
            SLog.write("Initial timeout reached, closing socket");
            closeConnection();
        }
        catch (IOException e)
        {
            SLog.write("Error reading: " + e.getMessage());
        }
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
}

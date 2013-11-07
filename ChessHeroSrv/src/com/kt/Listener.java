package com.kt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/6/13
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */

public class Listener
{
    private ServerSocket _sock;

    public ListenerDelegate delegate;

    Listener()
    {
        try
        {
            _sock = new ServerSocket(Config.SERVER_SOCK_PORT);
            listen();
        }
        catch (IOException e)
        {
            SLog.write("Server socket did fail to bind or to accept client connection: " + e.getMessage());
            System.exit(1);
        }
    }

    private void listen() throws IOException
    {
        while (true)
        {
            Socket clientSock = _sock.accept();

            if (delegate != null)
            {
                delegate.listenerDidReceiveClientConnection(clientSock);
            }
        }
    }
}

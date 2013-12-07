package com.kt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/6/13
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */

public class Server
{
    public final static int SERVER_SOCK_PORT = 4848;

    private ServerSocket _sock;

    Server()
    {
        try
        {
            _sock = new ServerSocket(SERVER_SOCK_PORT);
            SLog.write("Server socket bound");

            listen();
        }
        catch (IOException e)
        {
            SLog.write("Server socket failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void listen() throws IOException
    {
        while (true)
        {
            SLog.write("Listening...");
            Socket clientSock = _sock.accept();
            SLog.write("Client socket accepted");

            ClientConnection connection = new ClientConnection(clientSock);
            connection.start();
        }
    }
}
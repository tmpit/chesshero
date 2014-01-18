package com.kt;

import com.kt.utils.SLog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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

    private ServerSocket sock;

    public Server()
    {
        try
        {
			sock = new ServerSocket(SERVER_SOCK_PORT);
        }
        catch (IOException e)
        {
            SLog.write("Server socket failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public void listen()
    {
        while (true)
        {
			try
			{
				SLog.write("Listening...");
				Socket clientSock = sock.accept();
				SLog.write("Client socket accepted");

				ClientConnection connection = new ClientConnection(clientSock);
				connection.start();
			}
			catch (IOException e)
			{
				SLog.write("Exception raised accepting client socket or initializing connection: " + e);
				e.printStackTrace();
			}
        }
    }
}

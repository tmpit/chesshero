package com.kt;

import com.kt.utils.SLog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Server is a wrapper around a {@code ServerSocket} that also dispatches
 * {@code ClientConnection} objects with accepted client {@code Socket}s
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class Server
{
    public final static int SERVER_SOCK_PORT = 4848;

    private ServerSocket sock;

	/**
	 * Creates this instance's {@code ServerSocket} and binds it a port
	 */
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

	/**
	 * Starts accepting sockets. This method never returns
	 */
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

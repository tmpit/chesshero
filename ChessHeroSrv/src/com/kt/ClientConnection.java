package com.kt;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

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

    private boolean running = true;
    private Socket sock = null;
    private boolean hasAuthenticated = false;
    private Database db = null;

    ClientConnection(Socket sock)
    {
        this.sock = sock;
        db = new Database();
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

            while (running)
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

                handleMessage(Message.fromData(body));

                // Remove timeout when listening for header
                sock.setSoTimeout(0);
            }

            closeSocket();

            if (db.getKeepAlive())
            {
                db.setKeepAlive(false);
            }
        }
        catch (SocketTimeoutException e)
        {
            SLog.write("Read timed out");
            running = false;
        }
        catch (EOFException e)
        {
            SLog.write("Socket reached unexpected EOF??? - " + e);
            running = false;
        }
        catch (IOException e)
        {
            SLog.write("Error reading: " + e);
            running = false;
        }
        catch (ChessHeroException e)
        {
            int code = e.getCode();
            SLog.write("Chess hero exception: " + code);
            writeMessage(new ResultMessage(code));
            running = false;
        }
        catch (Exception e)
        {
            SLog.write("Surprise exception: " + e);
            running = false;
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

    private void writeMessage(Message msg)
    {
        try
        {
            sock.getOutputStream().write(msg.toData());
        }
        catch (IOException e)
        {
            SLog.write("Exception raised while writing to socket: " + e);
            running = false;
        }
    }

    private void handleMessage(Message msg) throws ChessHeroException
    {
        SLog.write("Received message: " + msg);

        short type = msg.getType();

        if (!hasAuthenticated && type != Message.TYPE_LOGIN && type != Message.TYPE_REGISTER)
        {
            SLog.write("Client attempting unauthorized action");
            throw new ChessHeroException(Result.AUTH_REQUIRED);
        }

        switch(type)
        {
            case Message.TYPE_REGISTER:
                handleRegister((AuthMessage)msg);
                break;
        }
    }

    private void handleRegister(AuthMessage msg) throws ChessHeroException
    {
        try
        {
            Credentials credentials = msg.getCredentials();

            if (!Credentials.isNameValid(credentials.getName()))
            {
                writeMessage(new ResultMessage(Result.INVALID_NAME));
                return;
            }

            if (!Credentials.isPassValid(credentials.getPass()))
            {
                writeMessage(new ResultMessage(Result.INVALID_PASS));
                return;
            }

            db.setKeepAlive(true);

            if (db.userExists(credentials.getName()))
            {
                writeMessage(new ResultMessage(Result.USER_EXISTS));
                return;
            }

            boolean success = db.insertUser(credentials);

            if (!success)
            {
                throw new ChessHeroException(Result.INTERNAL_ERROR);
            }

            hasAuthenticated = true;
            writeMessage(new ResultMessage(Result.OK));
        }
        catch (Exception e)
        {
            SLog.write(e);
            throw new ChessHeroException(Result.INTERNAL_ERROR);
        }
        finally
        {
            db.setKeepAlive(false);
        }
    }
}

package Client.Communication;

import com.kt.Message;
import com.kt.SLog;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/23/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Connection
{
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 4848;
    private static final int CONNECTION_TIMEOUT = 15 * 1000; // In milliseconds

    private static Connection singleton = null;

    private ClientSocket sock;
    private ArrayList<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

    private boolean isConnecting = false;
    private boolean isDisconnecting = false;
    private boolean isWriting = false;

    private ReadTask currentReadTask = null;

    public static synchronized Connection getSingleton()
    {
        if (null == singleton)
        {
            singleton = new Connection();
        }

        return singleton;
    }

    public void addEventListener(ConnectionListener listener)
    {
        listeners.add(listener);
    }

    public void connect()
    {
        if (isDisconnecting)
        {
            SLog.write("Attempting to connect when socket is disconnecting");
            return;
        }

        if (isConnecting || (sock != null && sock.isConnected()))
        {
            SLog.write("Attempting to connect when socket is connecting, or already connected");
            return;
        }

        isConnecting = true;

        new BackgroundTask()
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    sock = new ClientSocket(SERVER_ADDRESS, SERVER_PORT, CONNECTION_TIMEOUT);
                    success = true;
                }
                catch (IOException e)
                {
//                    SLog.write("Failed to connect: " + e);
                }

                return null;
            }

            @Override
            public void done()
            {
                isConnecting = false;

                if (success)
                {
                    for (ConnectionListener listener : listeners)
                    {
                        listener.socketConnected();
                    }
                }
                else
                {
                    for (ConnectionListener listener : listeners)
                    {
                        listener.socketFailedToConnect();
                    }
                }
            }

        }.execute();
    }

    public void disconnect()
    {
        if (isConnecting)
        {
            SLog.write("Attempting to disconnect when socket is connecting");
            return;
        }

        if (!sock.isConnected() || isDisconnecting)
        {
            SLog.write("Attempting to disconnect when socket is disconnecting or already disconnected");
            return;
        }

        isDisconnecting = true;

        new BackgroundTask()
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    sock.disconnect();
                    success = true;
                }
                catch (IOException e)
                {
//                    SLog.write("Failed to disconnect: " + e);
                }

                return null;
            }

            @Override
            public void done()
            {
                isDisconnecting = false;

                if (success)
                {
                    notifySocketDisconnected(false);
                }
            }
        }.execute();
    }

    public void readMessage(int timeout)
    {
        if (isConnecting || isDisconnecting)
        {
            SLog.write("Attempting to read message when socket is connecting or disconnecting");
            return;
        }

        if (!sock.isConnected())
        {
            SLog.write("Attempting to read message when socket is not connected");
            return;
        }

        if (currentReadTask != null)
        {
            SLog.write("Attempting to read message when socket is already reading");
            return;
        }

        currentReadTask = new ReadTask(timeout)
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    if (sock.getTimeout() != timeout)
                    {
                        sock.setTimeout(timeout);
                        didChangeTimeout = true;
                        lastTimeout = sock.getTimeout();
                    }

                    sock.setTimeout(timeout);
                    this.msg = sock.readMessage();
                    this.success = true;

                    if (didChangeTimeout)
                    {
                        sock.setTimeout(lastTimeout);
                    }
                }
                catch (EOFException e)
                {
//                    SLog.write("EOF reached while reading: " + e);
                    this.pipeClosed = true;
                }
                catch (Throwable e)
                {
//                    SLog.write("Failed to read message: " + e);
                }

                return null;
            }

            @Override
            public void done()
            {
                currentReadTask = null;

                if (!this.success)
                {
                    if (!sock.isConnected() || this.pipeClosed)
                    {   // EOF reached or socket is not accessible - close socket properly and notify
                        try
                        {
                            sock.disconnect();
                        }
                        catch (IOException ignore)
                        {
                        }

                        notifySocketDisconnected(true);

                        return;
                    }

                    if (0 == timeout)
                    {
                        SLog.write("Failed to read message, retrying...");
                        readMessage(0); // Something bad happened, retry
                    }

                    return;
                }

                for (ConnectionListener listener : listeners)
                {
                    listener.messageRead(this.msg);
                }
            }
        };

        currentReadTask.execute();
    }

    public void writeMessage(Message msg, int timeout)
    {
        if (isConnecting || isDisconnecting)
        {
            SLog.write("Attempting to write message when socket is connecting or disconnecting");
            return;
        }

        if (!sock.isConnected())
        {
            SLog.write("Attempting to write message when socket is not connected");
            return;
        }

        if (isWriting)
        {
            SLog.write("Attempting to write message while another is already being written");
            return;
        }

        isWriting = true;

        new WriteTask(msg, timeout)
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    if (sock.getTimeout() != timeout)
                    {
                        sock.setTimeout(timeout);
                        didChangeTimeout = true;
                        lastTimeout = sock.getTimeout();
                    }

                    sock.writeMessage(this.msg);
                    this.success = true;

                    if (didChangeTimeout)
                    {
                        sock.setTimeout(lastTimeout);
                    }
                }
                catch (SocketException e)
                {
//                    SLog.write("Socket exception raised while writing: " + e);
                    this.pipeClosed = true;
                }
                catch (IOException e)
                {
//                    SLog.write("Failed to write message: " + e);
                }

                return null;
            }

            @Override
            public void done()
            {
                isWriting = false;

                for (ConnectionListener listener : listeners)
                {
                    listener.messageWritten(this.success, this.msg);
                }

                if (!this.success && (!sock.isConnected() || this.pipeClosed))
                {   // Broken pipe or the socket cannot be accessed - close the socket properly and notify
                    try
                    {
                        sock.disconnect();
                    }
                    catch (IOException ignore)
                    {
                    }

                    notifySocketDisconnected(true);
                }
            }
        }.execute();
    }

    // Important!!!
    // Only attempt to use before you are expecting a message to arrive
    // It will cancel the current read and if the socket has completed reading but the task
    // has not finished yet, the message will be lost.
    // Even worse, if the socket is still reading when this task is cancelled, unread bytes of
    // the message will remain in the buffer which not only means the message is lost, but also
    // that communication through this socket will almost certainly become impossible,
    // in which case you will have to disconnect and connect again to continue normal communication
    public boolean cancelCurrentRead()
    {
        if (null == currentReadTask)
        {
            SLog.write("Attempting to cancel read task when there is no current read task");
            return true;
        }

        boolean cancelled = currentReadTask.cancel(true);

        if (cancelled)
        {
            currentReadTask = null;
        }

        return cancelled;
    }

    private void notifySocketDisconnected(boolean error)
    {
        for (ConnectionListener listener : listeners)
        {
            listener.socketDisconnected(error);
        }
    }

    private abstract class BackgroundTask extends SwingWorker<Void, Void>
    {
        protected boolean success = false;
    }

    private abstract class ReadTask extends BackgroundTask
    {
        protected Message msg;

        protected int timeout;
        protected int lastTimeout;
        protected boolean didChangeTimeout = false;

        protected boolean pipeClosed = false;

        ReadTask(int timeout)
        {
            this.timeout = timeout;
        }
    }

    private abstract class WriteTask extends ReadTask
    {
        WriteTask(Message msg, int timeout)
        {
            super(timeout);
            this.msg = msg;
        }
    }
}

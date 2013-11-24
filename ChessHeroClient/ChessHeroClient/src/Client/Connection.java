package Client;

import com.kt.ChessHeroException;
import com.kt.Message;
import com.kt.Result;
import com.kt.SLog;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/23/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Connection
{
    private static final String SERVER_ADDRESS = "192.168.153.200";
    private static final int SERVER_PORT = 4848;

    private static Connection singleton = null;

    private ClientSocket sock;
    private Vector<ConnectionListener> listeners = new Vector<ConnectionListener>();

    private boolean isConnecting = false;
    private boolean isDisconnecting = false;

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

    public void connect() throws IOException
    {
        if (isDisconnecting)
        {
            SLog.write("Attempting to connect when socket is disconnecting");
            return;
        }

        if (sock.isConnected() || isConnecting)
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
                    sock = new ClientSocket(SERVER_ADDRESS, SERVER_PORT);
                    success = true;
                }
                catch (IOException e)
                {
                    SLog.write("Failed to connect: " + e);
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

    public void disconnect() throws IOException
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
                    SLog.write("Failed to disconnect: " + e);
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

    public void readMessage()
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

        new ReadTask()
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    this.msg = sock.readMessage();
                    this.success = true;
                }
                catch (Throwable e)
                {
                    SLog.write(e);
                }

                return null;
            }

            @Override
            public void done()
            {
                if (!this.success)
                {
                    if (!sock.isConnected())
                    {
                        SLog.write("Socket disconnected unexpectedly");
                        notifySocketDisconnected(true);

                        return;
                    }

                    SLog.write("Failed to read message, retrying...");
                    readMessage(); // Something bad happened, retry
                    return;
                }

                for (ConnectionListener listener : listeners)
                {
                    listener.messageRead(this.msg);
                }
            }
        }.execute();
    }

    public void writeMessage(Message msg)
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

        new WriteTask(msg)
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    sock.writeMessage(this.msg);
                    this.success = true;
                }
                catch (IOException e)
                {
                    SLog.write(e);
                }

                return null;
            }

            @Override
            public void done()
            {
                for (ConnectionListener listener : listeners)
                {
                    listener.messageWritten(this.success, this.msg);
                }

                if (!this.success && !sock.isConnected())
                {
                    notifySocketDisconnected(true);
                }
            }
        }.execute();
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
    }

    private abstract class WriteTask extends ReadTask
    {
        WriteTask(Message msg)
        {
            this.msg = msg;
        }
    }
}

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
    private boolean didConnect = false;

    public static synchronized Connection getSingleton() throws IOException
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
        if (sock.isConnected() || isConnecting)
        {
            SLog.write("Attempting to connect socket when socket is connecting or already connected");
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
                    done = true;
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
                if (done)
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

                didConnect = done;
                isConnecting = false;
            }

        }.execute();
    }

    public void disconnect() throws IOException
    {
        if (!sock.isConnected())
        {
            SLog.write("Attempting to close socket when socket is already closed");
            return;
        }

        new BackgroundTask()
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    sock.disconnect();
                    done = true;
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
                if (done)
                {
                    for (ConnectionListener listener : listeners)
                    {
                        listener.socketDisconnected(false);
                    }
                }
            }
        }.execute();
    }

    public void readMessage()
    {
        new ReadTask()
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    this.msg = sock.readMessage();
                    this.done = true;
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
                if (!this.done)
                {
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
        new WriteTask(msg)
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    sock.writeMessage(this.msg);
                    this.done = true;
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
                    listener.messageWritten(this.done, this.msg);
                }
            }
        }.execute();
    }

    private abstract class BackgroundTask extends SwingWorker<Void, Void>
    {
        protected boolean done = false;
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

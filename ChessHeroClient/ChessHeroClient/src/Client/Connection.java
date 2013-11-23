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
    private static final String SERVER_ADDRESS = "domain.com";
    private static final int SERVER_PORT = 4848;

    private static Connection singleton = null;

    private ClientSocket sock;
    private Vector<ConnectionListener> listeners = new Vector<ConnectionListener>();

    public static synchronized Connection getSingleton() throws IOException
    {
        if (null == singleton)
        {
            singleton = new Connection();
        }

        return singleton;
    }

    Connection() throws IOException
    {
        sock = new ClientSocket(SERVER_ADDRESS, SERVER_PORT);
    }

    public void addEventListener(ConnectionListener listener)
    {
        listeners.add(listener);
    }

    public void readMessage()
    {
        new ReadTask() {
            @Override
            public Void doInBackground()
            {
                try
                {
                    this.msg = sock.readMessage();
                    this.messageResolved = true;
                    return null;
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
                if (!this.messageResolved)
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
        new WriteTask(msg) {
            @Override
            public Void doInBackground()
            {
                try
                {
                    sock.writeMessage(this.msg);
                    this.messageResolved = true;
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
                    listener.messageWritten(this.messageResolved ,this.msg);
                }
            }
        }.execute();
    }

    private abstract class ReadTask extends SwingWorker<Void, Void>
    {
        protected boolean messageResolved = false;

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

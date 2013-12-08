package Client.Communication;

import com.kt.Config;
import com.kt.utils.SLog;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.*;
import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/23/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Connection
{
//    private static final String SERVER_ADDRESS = "95.111.43.117";
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 4848;
    private static final int CONNECTION_TIMEOUT = 15 * 1000; // In milliseconds
    private static final int READ_TIMEOUT = 15 * 1000; // In milliseconds
    private static final int WRITE_TIMEOUT = 15 * 1000; // In milliseconds

    private static Connection singleton = null;

    private ClientSocket sock;
    private ArrayList<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
    private ArrayList<Object> readResponses = new ArrayList<Object>();

    private boolean verbose = false;

    private boolean isConnecting = false;
    private boolean isListening = false;
    private boolean shouldNotifyDisconnection = false; // Used to make sure listeners are notified about the disconnection after the last request has completed

    private RequestTask currentRequestTask = null;
    private Timer timeoutTimer = null;

    public static synchronized Connection getSingleton()
    {
        if (null == singleton)
        {
            singleton = new Connection();
        }

        return singleton;
    }

    public synchronized boolean isVerbose()
    {
        return verbose;
    }

    public synchronized void setVerbose(boolean flag)
    {
        verbose = flag;
    }

    public void addEventListener(ConnectionListener listener)
    {
        listeners.add(listener);
    }

    public void removeEventListener(ConnectionListener listener)
    {
        listeners.remove(listener);
    }

    public void connect()
    {
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
                    log("Failed to connect: " + e);
                }

                return null;
            }

            @Override
            public void done()
            {
                isConnecting = false;

                if (success)
                {
                    listen();

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
        doDisconnect(true);
    }

    private void doDisconnect(boolean notify)
    {
        if (isConnecting)
        {
            SLog.write("Attempting to disconnect when socket is connecting");
            return;
        }

        if (sock != null && !sock.isConnected())
        {
            SLog.write("Attempting to disconnect when socket is disconnecting or already disconnected");
            return;
        }

        new DisconnectTask(sock, notify)
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
                    log("Failed to disconnect: " + e);
                }

                return null;
            }

            @Override
            public void done()
            {
                if (success && notify)
                {
                    notifySocketDisconnected(false);
                }
            }
        }.execute();

        sock = null;
    }

    private void listen()
    {
        if (isConnecting)
        {
            SLog.write("Attempting to listen for messages when socket is connecting");
            return;
        }

        if (sock != null && !sock.isConnected())
        {
            SLog.write("Attempting to listen for messages when socket is not connected");
            return;
        }

        if (isListening)
        {
            SLog.write("Attempting to listen for messages when already listening");
            return;
        }

        isListening = true;

        new ListenTask()
        {
            @Override
            protected Void doInBackground()
            {
                while (true)
                {
                    try
                    {
                        if (sock.getTimeout() != 0)
                        {   // Do not timeout
                            sock.setTimeout(0);
                        }

                        HashMap<String, Object> msg = (HashMap<String, Object>)sock.read();
                        publish(msg);
                    }
                    catch (SocketException e)
                    {
                        log("Socket exception while listening: " + e);
                        break;
                    }
                    catch (EOFException e)
                    {
                        log("EOF reached while listening: " + e);
                        break;
                    }
                    catch (IOException e)
                    {
                        log("IO exception while listening: " + e);
                        break;
                    }
                    catch (Throwable e)
                    {
                        log("Exception thrown while listening for messages: " + e);
                    }
                }

                return null;
            }

            @Override
            protected void process (List<HashMap<String, Object>> messages)
            {
                for (HashMap<String, Object> msg : messages)
                {
                    if (msg.containsKey("push"))
                    {   // This message is pushed
                        for (ConnectionListener listener : listeners)
                        {
                            listener.didReceiveMessage(msg);
                        }
                    }
                    else
                    {   // This message is a response from a request of ours
                        synchronized (readResponses)
                        {
                            readResponses.add(msg);
                        }
                    }
                }
            }

            @Override protected void done()
            {
                isListening = false;
                doDisconnect(false);

                if (null == currentRequestTask)
                {
                    notifySocketDisconnected(true);
                }
                else
                {
                    shouldNotifyDisconnection = true;
                }
            }
        }.execute();
    }

    public void sendRequest(Request request)
    {
        if (isConnecting)
        {
            SLog.write("Attempting to send request when socket is connecting");
            return;
        }

        if (sock != null && !sock.isConnected())
        {
            SLog.write("Attempting to send request when socket is not connected");
            return;
        }

        if (currentRequestTask != null)
        {
            SLog.write("Attempting to send request while another is being processed");
            return;
        }

        currentRequestTask = new RequestTask(request)
        {
            @Override
            protected Void doInBackground()
            {
                try
                {
                    if (sock.getTimeout() != WRITE_TIMEOUT)
                    {
                        sock.setTimeout(WRITE_TIMEOUT);
                    }

                    sock.write(this.request);

                    while (null == this.response)
                    {
                        if (hasTimedOut())
                        {
                            log("Read operation timed out");
                            break;
                        }

                        synchronized (readResponses)
                        {
                            if (!readResponses.isEmpty())
                            {
                                this.response = (HashMap<String, Object>) readResponses.get(0);
                                readResponses.remove(0);
                            }
                        }

                        if (null == this.response)
                        {
                            Thread.sleep(4); // Wait until the listen task reads the message
                        }
                    }
                }
                catch (SocketException e)
                {
                    log("Socket exception raised while writing: " + e);
                }
                catch (IOException e)
                {
                    log("Failed sending request: " + e);
                }
                catch (InterruptedException e)
                {
                    log("Failed sending request: " + e);
                }

                return null;
            }

            @Override
            protected void done()
            {
                timeoutTimer.cancel();
                timeoutTimer = null;
                currentRequestTask = null;

                if (this.response != null)
                {
                    for (ConnectionListener listener : listeners)
                    {
                        listener.requestDidComplete(true, this.request, this.response);
                    }
                }
                else
                {
                    for (ConnectionListener listener : listeners)
                    {
                        listener.requestDidComplete(false, this.request, null);
                    }
                }

                if (shouldNotifyDisconnection)
                {
                    shouldNotifyDisconnection = false;
                    notifySocketDisconnected(true);
                }
            }
        };

        timeoutTimer = new Timer();
        timeoutTimer.schedule(new RequestTimeoutTask(currentRequestTask)
        {
            @Override
            public void run()
            {
                this.task.setTimedOut(true);
            }
        }, READ_TIMEOUT + WRITE_TIMEOUT);

        currentRequestTask.execute();
    }

    private void log(String str)
    {
        if (Config.DEBUG && isVerbose())
        {
            SLog.write(str);
        }
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

    private abstract class DisconnectTask extends BackgroundTask
    {
        protected ClientSocket sock;
        protected boolean notify;

        public DisconnectTask(ClientSocket sock, boolean notify)
        {
            this.sock = sock;
            this.notify = notify;
        }
    }

    private abstract class ListenTask extends SwingWorker<Void, HashMap<String, Object>>
    {
    }

    private abstract class RequestTask extends SwingWorker<Void, Void>
    {
        protected Request request;
        protected HashMap<String, Object> response;

        private boolean timedOut = false;

        public RequestTask(Request request)
        {
            this.request = request;
        }

        public synchronized void setTimedOut(boolean flag)
        {
            timedOut = flag;
        }

        public synchronized boolean hasTimedOut()
        {
            return timedOut;
        }
    }

    private abstract class RequestTimeoutTask extends TimerTask
    {
        protected RequestTask task;

        public RequestTimeoutTask(RequestTask task)
        {
            this.task = task;
        }
    }
}

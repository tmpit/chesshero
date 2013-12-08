package Client.Communication;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/23/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionListener
{
    // Invoked when client has attempted to connect and connection has been established
    public void socketConnected();

    // Invoked when client has attempted to connect but connection could not be established
    public void socketFailedToConnect();

    // Invoked when client disconnects from the server
    // error parameter will be false when disconnection is the result of the client invoking disconnect()
    // error parameter will be true when the clients loses connection to the server (e.g. the server closes the connection)
    public void socketDisconnected(boolean error);

    // Invoked when server pushes a message to the client
    public void didReceiveMessage(HashMap<String, Object> message);

    // Invoked when a request is sent and receives its response,
    // response parameter will be null if success == false
    public void requestDidComplete(boolean success, Request request, HashMap<String, Object> response);
}

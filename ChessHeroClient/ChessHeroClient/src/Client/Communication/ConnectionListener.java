package Client.Communication;

import com.kt.Message;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/23/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionListener
{
    public void socketConnected();
    public void socketFailedToConnect();
    public void socketDisconnected(boolean error);

    public void didReceiveMessage(Message msg);
    public void requestDidComplete(boolean success, Message request, Message response); // response will be null if success == false
}

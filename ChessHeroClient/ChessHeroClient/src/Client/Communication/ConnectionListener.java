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

    public void messageRead(Message msg);
    public void messageWritten(boolean result, Message msg);
}

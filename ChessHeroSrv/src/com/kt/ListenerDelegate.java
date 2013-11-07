package com.kt;

import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/7/13
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ListenerDelegate
{
    public void listenerDidReceiveClientConnection(Socket sock);
}

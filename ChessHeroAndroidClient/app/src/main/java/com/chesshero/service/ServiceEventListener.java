package com.chesshero.service;

import java.util.HashMap;

/**
 * Created by Toshko on 11/26/14.
 */
public interface ServiceEventListener
{
	void serviceDidConnect();
	void serviceDidFailToConnect();
	void serviceDidDisconnect();
	void serviceDidCompleteRequest(ServiceRequest request, HashMap<String, Object> response);
	void serviceDidReceivePushMessage(HashMap<String, Object> message);
}

package com.chesshero.service;

import java.util.HashMap;

/**
 * Created by Toshko on 11/26/14.
 */
public interface ServiceEventListener
{
	// Invoked when the service successfully establishes a connection to the server
	void serviceDidConnect();

	// Invoked when the service fails to establish a connection to the server
	void serviceDidFailToConnect();

	// Invoked when the service closes the connection to the server wither due of a connection failure or due to a disconnect() call on the ServiceProxy
	void serviceDidDisconnect();

	// Invoked when the service executes a request
	// The 'request' object will be the same one passed to the sendRequest() method
	// The 'response' object will be null if the request timed out or if there has been a connection failure
	void serviceDidCompleteRequest(ServiceRequest request, HashMap<String, Object> response);

	// Invoked when the service receives a push message from the server
	void serviceDidReceivePushMessage(HashMap<String, Object> message);
}

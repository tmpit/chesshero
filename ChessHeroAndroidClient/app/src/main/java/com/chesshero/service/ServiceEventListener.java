package com.chesshero.service;

import java.util.HashMap;

/**
 * Created by Toshko on 11/26/14.
 *
 * Interface that must be implemented in order to observer events from a {@code ServerCommunicationService}
 */
public interface ServiceEventListener
{
	/**
	 * Invoked when the service successfully establishes a connection to the server
	 */
	void serviceDidConnect();

	/**
	 * Invoked when the service fails to establish a connection to the server
	 */
	void serviceDidFailToConnect();

	/**
	 * Invoked when the service closes the connection to the server either due of a connection failure or due to a
	 * {@code disconnect()} call on the {@code ServerCommunicationService.Proxy}
	 */
	void serviceDidDisconnect();

	/**
	 * Invoked when the service completes executing a request
	 * @param request This is the same request object passed to the {@code sendRequest()} method on the
	 *                {@code ServerCommunicationService.Proxy}
	 * @param response The response as sent by the server. Can be {@code null} if the request timed out or if there
	 *                 has been a connection failure
	 */
	void serviceDidCompleteRequest(ServiceRequest request, HashMap<String, Object> response);

	/**
	 * Invoked when the service receives a push message from the server
	 * @param message The push message as sent by the server
	 */
	void serviceDidReceivePushMessage(HashMap<String, Object> message);
}

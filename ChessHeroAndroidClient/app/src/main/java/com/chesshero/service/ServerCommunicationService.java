package com.chesshero.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import com.kt.utils.SLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Toshko on 11/24/14.
 */
public class ServerCommunicationService extends Service
{
	// ========================================================
	// Connection configuration
	// ========================================================

	// The address the service connects to
	private static final String SERVER_ADDRESS = "192.168.100.3";

	// The port the service connect to
	private static final int SERVER_PORT = 4848;

	// The maximum amount of time in milliseconds the service will wait for a connection to the server to be established
	// After that time has passed, connection attempt will be considered failed
	private static final int CONNECTION_TIMEOUT = 15 * 1000;

	// The maximum amount of time in milliseconds the service will wait for a request to be sent to the server
	// After that time has passed, the request is considered failed
	private static final int WRITE_TIMEOUT = 15 * 1000;

	// The maximum amount of time in milliseconds the service will wait for a response to a request after the request is sent to the server
	// After that time has passed, the request is considered failed
	private static final int READ_TIMEOUT = 15 * 1000;


	// A Handler subclass used to synchronize access to the service and its state
	// Only it interacts directly with the service object so all communication with the service from the outside goes through it
	// A Proxy object uses it to schedule connect(), disconnect() and sendRequest() calls on the service object
	// It is also passed as a callback handler to all Task subclasses
	private WorkDispatchHandler workDispatchHandler;

	// ========================================================
	// WorkDispatchHandler action identifiers
	// A Message object is used to wrap an action and a parameter as follows:
	// - its 'what' property will hold one of the following action codes
	// - its 'obj' property will hold a parameter object
	// ========================================================

	// Sent when a Proxy client invokes connect() on the proxy
	private static final int WORK_DISPATCH_MSG_ACTION_CONNECT = 1;

	// Sent when a Proxy client invokes disconnect() on the proxy
	private static final int WORK_DISPATCH_MSG_ACTION_DISCONNECT = 2;

	// Sent when a Proxy client invokes sendRequest() on the proxy
	// Message has a ServiceRequest parameter object
	private static final int WORK_DISPATCH_MSG_ACTION_REQUEST = 3;

	// Sent after a request has been successfully written to the server
	private static final int WORK_DISPATCH_MSG_ACTION_READ_TIMEOUT = 4;

	// A Handler subclass initialized with the main looper
	// It manages ServiceEventListener's and invokes ServiceEventListener callbacks, so all communication with the event listeners goes through it
	// A Proxy object uses it to add and remove ServiceEventListener's
	// The service object uses it to schedule ServiceEventListener callbacks
	private NotificationHandler notificationHandler;

	// ========================================================
	// NotificationHandler action identifiers
	// A Message object is used to wrap an action and a parameter as follows:
	// - its 'what' property will hold one of the following action codes
	// - its 'obj' property will hold a parameter object
	// ========================================================

	// Sent when a Proxy client invokes addEventListener() on the proxy
	private static final int NOTIFICATION_MSG_ACTION_ADD_LISTENER = 1;

	// Sent when a Proxy client invokes removeEventListener() on the proxy
	private static final int NOTIFICATION_MSG_ACTION_RM_LISTENER = 2;

	// Used to schedule serviceDidConnect() on event listeners
	private static final int NOTIFICATION_MSG_ACTION_CONNECT = 3;

	// Used to schedule serviceDidFailToConnect() on event listeners
	private static final int NOTIFICATION_MSG_ACTION_CONNECT_FAIURE = 4;

	// Used to schedule serviceDidDisconnect() on event listeners
	private static final int NOTIFICATION_MSG_ACTION_DISCONNECT = 5;

	// Used to schedule serviceDidCompleteRequest() on event listeners
	// Message has an Object[] parameter object which contains a ServiceRequest object at index 0 and a HashMap<String, Object> object at index 1
	private static final int NOTIFICATION_MSG_ACTION_RESPONSE = 6;

	// Used to schedule serviceDidReceivePushMessage() on event listeners
	// Message has a HashMap<String, Object> parameter object
	private static final int NOTIFICATION_MSG_ACTION_PUSH = 7;


	// ========================================================
	// Connectivity state constants
	// ========================================================

	// Disconnected state
	private static final int STATE_DISCONNECTED = 1;

	// Connecting state
	private static final int STATE_CONNECTING = 2;

	// Connected state
	private static final int STATE_CONNECTED = 3;

	// The current connectivity state of the service
	private int state = STATE_DISCONNECTED;

	// The executor that handles all network-related tasks
	private ExecutorService executor;

	// The current socket instance
	// Initialized only when in STATE_CONNECTED
	private CHESCOSocket socket;

	// The current ConnectTask instance
	// Initialized only when in STATE_CONNECTING
	private ConnectTask currentConnectTask;

	// The current ListenTask instance
	// Initialized only when in STATE_CONNECTED
	private ListenTask currentListenTask;

	// The current SendTask instance
	// Might be initialized only when in STATE_CONNECTED
	private SendTask currentSendTask;

	// The current ServiceRequest the service is executing
	// Might be initialized only when in STATE_CONNECTED
	private ServiceRequest currentRequest = null;

	// The number of response (not push) messages the service should ignore
	// This is incremented whenever a response read timeout occurs and decremented when above zero and a response is received
	private int responseMessagesToIgnore = 0;


	// Closes the socket, ignoring all exceptions and releases its memory
	// Assumes the socket variable is initialized
	private void closeSocket()
	{
		try
		{
			socket.getSocket().close();
		}
		catch (Throwable e)
		{
			log("exception thrown on socket close: " + e);
		}

		socket = null;
	}

	// Submits a ConnectTask to the executor
	// No-op if the service is already connected or connecting
	private void connect()
	{
		if (state != STATE_DISCONNECTED)
		{
			return;
		}

		state = STATE_CONNECTING;

		currentConnectTask = new ConnectTask(SERVER_ADDRESS, SERVER_PORT, CONNECTION_TIMEOUT)
		{
			@Override
			public void onFinish()
			{
				if (currentConnectTask != this)
				{	// Do not modify state if this is a lingering callback from the previous ConnectTask
					return;
				}

				currentConnectTask = null;

				if (this.isCancelled())
				{	// disconnect() has been invoked, we do not care if we have established a connection or not
					return;
				}

				if (this.isCompleted())
				{	// Connection has been established - modify state, start a ListenTask and notify listeners
					socket = this.getSocket();
					state = STATE_CONNECTED;
					startListening();
					notifyEventListenersForConnect();
				}
				else
				{	// Failure - notify listeners
					notifyEventListenersForConnectFailure();
				}
			}
		};

		currentConnectTask.setCallbackHandler(workDispatchHandler);
		executor.submit(currentConnectTask);
	}

	// Closes the socket, cancels all running tasks and resets state
	// No-op if the service is already disconnected
	private void disconnect()
	{
		if (STATE_DISCONNECTED == state)
		{
			return;
		}

		if (STATE_CONNECTING == state)
		{	// We are currently connecting - cancel the ConnectTask
			currentConnectTask.cancel();
		}
		else
		{	// We are connected - close the socket, cancel all running tasks and notify event listeners
			closeSocket();
			currentListenTask.cancel();

			if (currentSendTask != null)
			{
				currentSendTask.cancel();
			}

			notifyEventListenersForDisconnect();
		}

		// Reset state
		fill();
		state = STATE_DISCONNECTED;
	}

	// Starts a ListenTask
	// Assumes we are in a connected state
	private void startListening()
	{
		currentListenTask = new ListenTask(socket)
		{
			@Override
			public void onMessage(Map message)
			{
				if (message.containsKey("push"))
				{	// Push message received - notify event listeners
					notifyEventListenersForPushMessage((HashMap)message);
				}
				else if (!feed())
				{	// Only handle the response if it has not timed out
					completeRequestWithResponse((HashMap)message);
				}
			}

			@Override
			public void onFinish()
			{
				if (this != currentListenTask)
				{	// Do not modify state if this is a lingering callback from the previous ListenTask
					return;
				}

				if (!this.isCancelled())
				{	// Task failed due to connection failure - disconnect the service
					disconnect();
				}

				currentListenTask = null;
			}
		};

		currentListenTask.setCallbackHandler(workDispatchHandler);
		executor.submit(currentListenTask);
	}

	// Starts a SendTask
	// Assumes 'request' is not null
	private void sendRequest(ServiceRequest request)
	{
		if (state != STATE_CONNECTED || isRequesting())
		{	// Only handle one request at a time
			return;
		}

		currentSendTask = new SendTask(socket, request.getParameters(), WRITE_TIMEOUT)
		{
			@Override
			public void onFinish()
			{
				if (currentSendTask != this)
				{	// Do not modify state if this is a lingering callback from the previous SendTask
					return;
				}

				if (!this.isCompleted() || this.isCancelled())
				{	// Always complete the request if it cannot proceed any further
					completeRequestWithResponse(null);

					if (!this.isCancelled())
					{	// Connection failure - disconnect the service
						disconnect();
					}
				}
				else
				{	// We have successfully written the request, start a read timeout
					scheduleReadTimeout();
				}

				currentSendTask = null;
			}
		};

		executor.submit(currentSendTask);
		startRequest(request);
	}

	private void startRequest(ServiceRequest request)
	{
		currentRequest = request;
	}

	private boolean isRequesting()
	{
		return currentRequest != null;
	}

	private void completeRequestWithResponse(HashMap response)
	{
		cancelReadTimeout();
		notifyEventListenersForRequestCompletion(currentRequest, response);
		currentRequest = null;
	}

	private void scheduleReadTimeout()
	{
		Message msg = workDispatchHandler.obtainMessage(WORK_DISPATCH_MSG_ACTION_READ_TIMEOUT);
		workDispatchHandler.sendMessageDelayed(msg, READ_TIMEOUT);
	}

	private void cancelReadTimeout()
	{
		workDispatchHandler.removeMessages(WORK_DISPATCH_MSG_ACTION_READ_TIMEOUT);
	}

	private void readDidTimeout()
	{
		starve();
		completeRequestWithResponse(null);
	}

	private void starve()
	{
		responseMessagesToIgnore++;
	}

	private boolean feed()
	{
		if (0 == responseMessagesToIgnore)
		{
			return false;
		}

		responseMessagesToIgnore--;
		return true;
	}

	private void fill()
	{
		responseMessagesToIgnore = 0;
	}

	private void notifyEventListenersForConnect()
	{
		Message msg = notificationHandler.obtainMessage(NOTIFICATION_MSG_ACTION_CONNECT);
		notificationHandler.sendMessage(msg);
	}

	private void notifyEventListenersForConnectFailure()
	{
		Message msg = notificationHandler.obtainMessage(NOTIFICATION_MSG_ACTION_CONNECT_FAIURE);
		notificationHandler.sendMessage(msg);
	}

	private void notifyEventListenersForDisconnect()
	{
		Message msg = notificationHandler.obtainMessage(NOTIFICATION_MSG_ACTION_DISCONNECT);
		notificationHandler.sendMessage(msg);
	}

	private void notifyEventListenersForRequestCompletion(ServiceRequest request, HashMap response)
	{
		Object[] bag = {request, response};
		Message msg = notificationHandler.obtainMessage(NOTIFICATION_MSG_ACTION_RESPONSE, bag);
		notificationHandler.sendMessage(msg);
	}

	private void notifyEventListenersForPushMessage(HashMap push)
	{
		Message msg = notificationHandler.obtainMessage(NOTIFICATION_MSG_ACTION_PUSH, push);
		notificationHandler.sendMessage(msg);
	}

	private void log(String text)
	{
		SLog.write("[ServerCommunicationService] : " + text);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		HandlerThread thread = new HandlerThread("com.chesshero.scs.handler", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		workDispatchHandler = new WorkDispatchHandler(thread.getLooper());
		notificationHandler = new NotificationHandler();

		executor = Executors.newCachedThreadPool(new ServiceThreadFactory());
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new Proxy();
	}

	public class Proxy extends Binder
	{
		public void addEventListener(ServiceEventListener listener)
		{
			Message msg = notificationHandler.obtainMessage(NOTIFICATION_MSG_ACTION_ADD_LISTENER, listener);
			notificationHandler.sendMessage(msg);
		}

		public void removeEventListener(ServiceEventListener listener)
		{
			Message msg = notificationHandler.obtainMessage(NOTIFICATION_MSG_ACTION_RM_LISTENER, listener);
			notificationHandler.sendMessage(msg);
		}

		public void connect()
		{
			Message msg = workDispatchHandler.obtainMessage(WORK_DISPATCH_MSG_ACTION_CONNECT);
			workDispatchHandler.sendMessage(msg);
		}

		public void disconnect()
		{
			Message msg = workDispatchHandler.obtainMessage(WORK_DISPATCH_MSG_ACTION_DISCONNECT);
			workDispatchHandler.sendMessage(msg);
		}

		public void sendRequest(ServiceRequest request)
		{
			Message msg = workDispatchHandler.obtainMessage(WORK_DISPATCH_MSG_ACTION_REQUEST, request);
			workDispatchHandler.sendMessage(msg);
		}
	}

	private class WorkDispatchHandler extends Handler
	{
		WorkDispatchHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case WORK_DISPATCH_MSG_ACTION_CONNECT:
					ServerCommunicationService.this.connect();
					break;

				case WORK_DISPATCH_MSG_ACTION_DISCONNECT:
					ServerCommunicationService.this.disconnect();
					break;

				case WORK_DISPATCH_MSG_ACTION_REQUEST:
					if (msg.obj != null && msg.obj instanceof ServiceRequest) {
						ServerCommunicationService.this.sendRequest((ServiceRequest) msg.obj);
					} else {
						log("attempting to send a request with invalid or missing ServiceRequest object");
					}
					break;

				case WORK_DISPATCH_MSG_ACTION_READ_TIMEOUT:
					ServerCommunicationService.this.readDidTimeout();
					break;

				default:
					log("invalid message action");
			}
		}
	}

	private class NotificationHandler extends Handler
	{
		private ArrayList<ServiceEventListener> eventListeners = new ArrayList<ServiceEventListener>();

		NotificationHandler()
		{
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case NOTIFICATION_MSG_ACTION_ADD_LISTENER:
					if (msg.obj instanceof ServiceEventListener) {
						addEventListener((ServiceEventListener)msg.obj);
					} else {
						log("attempting to add a listener that does not implement ServiceEventListener interface");
					}
					break;

				case NOTIFICATION_MSG_ACTION_RM_LISTENER:
					if (msg.obj instanceof ServiceEventListener) {
						removeEventListener((ServiceEventListener) msg.obj);
					} else {
						log("attempting to remove a listener that does not implement ServiceEventListener interface");
					}
					break;

				case NOTIFICATION_MSG_ACTION_CONNECT:
					notifyConnect();
					break;

				case NOTIFICATION_MSG_ACTION_CONNECT_FAIURE:
					notifyConnectFailure();
					break;

				case NOTIFICATION_MSG_ACTION_DISCONNECT:
					notifyDisconnect();
					break;

				case NOTIFICATION_MSG_ACTION_RESPONSE:
					Object[] bag = (Object[])msg.obj;
					notifyRequestCompletion((ServiceRequest)bag[0], (HashMap<String, Object>)bag[1]);
					break;

				case NOTIFICATION_MSG_ACTION_PUSH:
					notifyPush((HashMap<String, Object>)msg.obj);
					break;
			}
		}

		public void addEventListener(ServiceEventListener listener)
		{
			eventListeners.add(listener);
		}

		public void removeEventListener(ServiceEventListener listener)
		{
			eventListeners.remove(listener);
		}

		public void notifyConnect()
		{
			for (int i = eventListeners.size() - 1; i >= 0; i--)
			{
				eventListeners.get(i).serviceDidConnect();
			}
		}

		public void notifyConnectFailure()
		{
			for (int i = eventListeners.size() - 1; i >= 0; i--)
			{
				eventListeners.get(i).serviceDidFailToConnect();
			}
		}

		public void notifyDisconnect()
		{
			for (int i = eventListeners.size() - 1; i >= 0; i--)
			{
				eventListeners.get(i).serviceDidDisconnect();
			}
		}

		public void notifyRequestCompletion(ServiceRequest request, HashMap<String, Object> response)
		{
			for (int i = eventListeners.size() - 1; i >= 0; i--)
			{
				eventListeners.get(i).serviceDidCompleteRequest(request, response);
			}
		}

		public void notifyPush(HashMap<String, Object> push)
		{
			for (int i = eventListeners.size() - 1; i >= 0; i--)
			{
				eventListeners.get(i).serviceDidReceivePushMessage(push);
			}
		}
	}
}

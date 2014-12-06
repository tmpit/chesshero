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
	// Connection configuration
	private static final String SERVER_ADDRESS = "192.168.100.3";
	private static final int SERVER_PORT = 4848;
	private static final int CONNECTION_TIMEOUT = 15 * 1000; // In milliseconds
	private static final int READ_TIMEOUT = 15 * 1000; // In milliseconds
	private static final int WRITE_TIMEOUT = 15 * 1000; // In milliseconds

	// WorkDispatchHandler message action identifiers. Class is defined at bottom of source file
	private static final int WORK_DISPATCH_MSG_ACTION_CONNECT = 1;
	private static final int WORK_DISPATCH_MSG_ACTION_DISCONNECT = 2;
	private static final int WORK_DISPATCH_MSG_ACTION_REQUEST = 3;
	private static final int WORK_DISPATCH_MSG_ACTION_READ_TIMEOUT = 4;

	// NotificationHandler message action identifiers. Class is defined at bottom of source file
	private static final int NOTIFICATION_MSG_ACTION_ADD_LISTENER = 1;
	private static final int NOTIFICATION_MSG_ACTION_RM_LISTENER = 2;
	private static final int NOTIFICATION_MSG_ACTION_CONNECT = 3;
	private static final int NOTIFICATION_MSG_ACTION_CONNECT_FAIURE = 4;
	private static final int NOTIFICATION_MSG_ACTION_DISCONNECT = 5;
	private static final int NOTIFICATION_MSG_ACTION_RESPONSE = 6;
	private static final int NOTIFICATION_MSG_ACTION_PUSH = 7;

	// When NOTIFICATION_MSG_ACTION_RESPONSE, obj property is a HashMap. The two objects inside can be accessed using these keys
	private static final String NOTIFICATION_MSG_OBJ_REQUEST_KEY = "request";
	private static final String NOTIFICATION_MSG_OBJ_RESPONSE_KEY = "response";

	private static final int STATE_DISCONNECTED = 1;
	private static final int STATE_CONNECTING = 2;
	private static final int STATE_CONNECTED = 3;

	private int state = STATE_DISCONNECTED;

	private WorkDispatchHandler workDispatchHandler;
	private NotificationHandler notificationHandler;
	private ExecutorService executor;

	private CHESCOSocket socket;
	private ConnectTask currentConnectTask;
	private ListenTask currentListenTask;
	private SendTask currentSendTask;
	private ServiceRequest currentRequest = null;

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
				{
					return;
				}

				currentConnectTask = null;

				if (this.isCancelled())
				{
					return;
				}

				if (this.isCompleted())
				{
					socket = this.getSocket();
					state = STATE_CONNECTED;
					startListening();
					notifyEventListenersForConnect();
				}
				else
				{
					notifyEventListenersForConnectFailure();
				}
			}
		};

		currentConnectTask.setCallbackHandler(workDispatchHandler);
		executor.submit(currentConnectTask);
	}

	private void disconnect()
	{
		if (STATE_DISCONNECTED == state)
		{
			return;
		}

		if (STATE_CONNECTING == state)
		{
			currentConnectTask.cancel();
		}
		else
		{
			closeSocket();
			currentListenTask.cancel();

			if (currentSendTask != null)
			{
				currentSendTask.cancel();
			}

			notifyEventListenersForDisconnect();
		}

		state = STATE_DISCONNECTED;
	}

	private void startListening()
	{
		currentListenTask = new ListenTask(socket)
		{
			@Override
			public void onMessage(Map message)
			{
				if (message.containsKey("push"))
				{
					notifyEventListenersForPushMessage((HashMap)message);
				}
				else if (isRequesting())
				{
					completeRequestWithResponse((HashMap)message);
				}
			}

			@Override
			public void onFinish()
			{
				if (this != currentListenTask)
				{
					return;
				}

				if (!this.isCancelled())
				{
					disconnect();
				}

				currentListenTask = null;
			}
		};

		currentListenTask.setCallbackHandler(workDispatchHandler);
		executor.submit(currentListenTask);
	}

	private void sendRequest(ServiceRequest request)
	{
		if (state != STATE_CONNECTED || isRequesting())
		{
			return;
		}

		currentSendTask = new SendTask(socket, request.getParameters(), WRITE_TIMEOUT)
		{
			@Override
			public void onFinish()
			{
				if (currentSendTask != this)
				{
					return;
				}

				if (!this.isCompleted() || this.isCancelled())
				{
					completeRequestWithResponse(null);

					if (!this.isCancelled())
					{
						disconnect();
					}
				}
				else
				{
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
		completeRequestWithResponse(null);
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
		HashMap<String, Object> bag = new HashMap<String, Object>();
		bag.put(NOTIFICATION_MSG_OBJ_REQUEST_KEY, request);
		bag.put(NOTIFICATION_MSG_OBJ_RESPONSE_KEY, response);
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
		return new ServiceProxy();
	}

	public class ServiceProxy extends Binder
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
					HashMap<String, Object> bag = (HashMap<String, Object>)msg.obj;
					ServiceRequest request = (ServiceRequest)bag.get(NOTIFICATION_MSG_OBJ_REQUEST_KEY);
					HashMap<String, Object> response = (HashMap<String, Object>)bag.get(NOTIFICATION_MSG_OBJ_RESPONSE_KEY);
					notifyRequestCompletion(request, response);
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

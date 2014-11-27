package com.chesshero.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Toshko on 11/24/14.
 */
public class ServerCommunicationService extends Service
{
	// Connection configuration
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int SERVER_PORT = 4848;
	private static final int CONNECTION_TIMEOUT = 15 * 1000; // In milliseconds
	private static final int READ_TIMEOUT = 15 * 1000; // In milliseconds
	private static final int WRITE_TIMEOUT = 15 * 1000; // In milliseconds

	// WorkDispatchHandler message action identifiers. Class is defined at bottom of source file
	private static final int WORK_DISPATCH_MSG_ACTION_CONNECT = 1;
	private static final int WORK_DISPATCH_MSG_ACTION_DISCONNECT = 2;
	private static final int WORK_DISPATCH_MSG_ACTION_REQUEST = 3;

	// NotificationHandler message action identifiers. Class is defined at bottom of source file
	private static final int NOTIFICATION_MSG_ACTION_ADD_LISTENER = 1;
	private static final int NOTIFICATION_MSG_ACTION_RM_LISTENER = 2;
	private static final int NOTIFICATION_MSG_ACTION_CONNECT = 3;
	private static final int NOTIFICATION_MSG_ACTION_DISCONNECT = 4;
	private static final int NOTIFICATION_MSG_ACTION_RESPONSE = 5;
	private static final int NOTIFICATION_MSG_ACTION_PUSH = 6;

	// When NOTIFICATION_MSG_ACTION_RESPONSE, obj property is a HashMap. The two objects inside can be accessed using these keys
	private static final String NOTIFICATION_MSG_OBJ_REQUEST_KEY = "request";
	private static final String NOTIFICATION_MSG_OBJ_RESPONSE_KEY = "response";

	private WorkDispatchHandler workDispatchHandler;
	private NotificationHandler notificationHandler;

	private void connect()
	{

	}

	private void disconnect()
	{

	}

	private void sendRequest(ServiceRequest request)
	{

	}

	private void notifyEventListenersForConnect()
	{
		Message msg = notificationHandler.obtainMessage(NOTIFICATION_MSG_ACTION_CONNECT);
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

	@Override
	public void onCreate()
	{
		super.onCreate();

		HandlerThread thread = new HandlerThread("com.chesshero.scs.handler", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		workDispatchHandler = new WorkDispatchHandler(thread.getLooper());
		notificationHandler = new NotificationHandler();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return super.onStartCommand(intent, flags, startId);
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
						Log.w("Invalid or missing ServiceRequest object", "");
					}
					break;

				default:
					Log.w("Invalid message action", "");
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
						Log.w("Object does not implement ServiceEventListener interface", "");
					}
					break;

				case NOTIFICATION_MSG_ACTION_RM_LISTENER:
					if (msg.obj instanceof ServiceEventListener) {
						removeEventListener((ServiceEventListener) msg.obj);
					} else {
						Log.w("Object does not implement ServiceEventListener interface", "");
					}
					break;

				case NOTIFICATION_MSG_ACTION_CONNECT:
					notifyConnect();
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

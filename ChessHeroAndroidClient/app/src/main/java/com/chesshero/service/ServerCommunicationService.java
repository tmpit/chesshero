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
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int SERVER_PORT = 4848;
	private static final int CONNECTION_TIMEOUT = 15 * 1000; // In milliseconds
	private static final int READ_TIMEOUT = 15 * 1000; // In milliseconds
	private static final int WRITE_TIMEOUT = 15 * 1000; // In milliseconds

	private static final int MSG_ACTION_CONNECT = 1;
	private static final int MSG_ACTION_DISCONNECT = 2;
	private static final int MSG_ACTION_REQUEST = 3;

	private MessageHandler messageHandler;

	private ArrayList<ServiceEventListener> eventListeners = new ArrayList<ServiceEventListener>();

	private void connect()
	{

	}

	private void disconnect()
	{

	}

	private void sendRequest(ServiceRequest request)
	{

	}

	private synchronized void addEventListener(ServiceEventListener listener)
	{
		eventListeners.add(listener);
	}

	private synchronized void removeEventListener(ServiceEventListener listener)
	{
		eventListeners.remove(listener);
	}

	private void onHandleMessage(Message msg)
	{
		switch (msg.what)
		{
			case MSG_ACTION_CONNECT:
				connect();
				break;

			case MSG_ACTION_DISCONNECT:
				disconnect();
				break;

			case MSG_ACTION_REQUEST:
				if (msg.obj != null && msg.obj instanceof ServiceRequest) {
					sendRequest((ServiceRequest) msg.obj);
				} else {
					Log.w("Invalid or missing ServiceRequest object", "");
				}
				break;

			default:
				Log.w("Invalid message action", "");
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		HandlerThread thread = new HandlerThread("com.chesshero.scs.handler", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		messageHandler = new MessageHandler(thread.getLooper());
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
			ServerCommunicationService.this.addEventListener(listener);
		}

		public void removeEventListener(ServiceEventListener listener)
		{
			ServerCommunicationService.this.removeEventListener(listener);
		}

		public void connect()
		{
			Message msg = messageHandler.obtainMessage(MSG_ACTION_CONNECT);
			messageHandler.sendMessage(msg);
		}

		public void disconnect()
		{
			Message msg = messageHandler.obtainMessage(MSG_ACTION_DISCONNECT);
			messageHandler.sendMessage(msg);
		}

		public void sendRequest(ServiceRequest request)
		{
			Message msg = messageHandler.obtainMessage(MSG_ACTION_REQUEST, request);
			messageHandler.sendMessage(msg);
		}
	}

	private class MessageHandler extends Handler
	{
		MessageHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			ServerCommunicationService.this.onHandleMessage(msg);
		}
	}
}

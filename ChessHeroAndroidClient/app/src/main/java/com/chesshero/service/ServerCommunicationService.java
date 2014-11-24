package com.chesshero.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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

	@Override
	public void onCreate()
	{
		super.onCreate();
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
		return new ProxyBinder();
	}

	public class ProxyBinder extends Binder
	{

	}
}

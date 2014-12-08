package com.chesshero.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.chesshero.client.parsers.LoginResponseParser;
import com.chesshero.service.ServerCommunicationService;
import com.chesshero.service.ServiceEventListener;
import com.chesshero.service.ServiceRequest;
import com.kt.api.Action;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Toshko on 12/7/14.
 */
public class Client implements ServiceEventListener
{
	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder)
		{
			log("connected to service");

			serviceProxy = (ServerCommunicationService.Proxy)iBinder;
			serviceProxy.addEventListener(Client.this);
			connectedToService = true;
			serviceProxy.connect();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			log("disconnected from service");

			serviceProxy = null;
			connectedToService = false;
		}
	};

	private Context context;

	private ServerCommunicationService.Proxy serviceProxy;
	private boolean connectedToService = false;

	private User user = null;

	private boolean shouldAutomaticallyLoginOnConnect = false;
	private ServiceRequest cachedLoginRequest;

	private boolean executingRequest = false;

	protected Client(Context context)
	{
		this.context = context;
		Intent intent = new Intent(context, ServerCommunicationService.class);
		context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public User getUser()
	{
		return user;
	}

	public boolean isLoggedIn()
	{
		return user != null;
	}

	public void login(String userName, String password)
	{
		if (isLoggedIn())
		{
			log("attempting to login while a user is already logged in");
			return;
		}

		if (null == userName || null == password)
		{
			log("attempting to login with missing username or password");
			return;
		}

		if (!connectedToService || !serviceProxy.isConnected())
		{
			if (connectedToService && !serviceProxy.isConnecting())
			{
				serviceProxy.connect();
			}

			shouldAutomaticallyLoginOnConnect = true;
			cachedLoginRequest = ServiceRequestFactory.createLoginRequest(userName, password);
			return;
		}

		if (shouldAutomaticallyLoginOnConnect)
		{
			return;
		}

		maybeSendRequest(ServiceRequestFactory.createLoginRequest(userName, password));
	}

	private void maybeSendRequest(ServiceRequest request)
	{
		if (executingRequest)
		{
			return;
		}

		executingRequest = true;
		serviceProxy.sendRequest(request);
	}

	// ===============================================================
	private void loginDidComplete(LoginResponseParser parser)
	{
		executingRequest = false;

		if (parser.success)
		{
			user = new User(parser.userID, parser.userName);
		}

		notifyLoginCompletion(parser.result, user);
	}

	// ===============================================================
	@Override
	public void serviceDidConnect()
	{
		log("service did connect");

		if (shouldAutomaticallyLoginOnConnect)
		{
			shouldAutomaticallyLoginOnConnect = false;
			maybeSendRequest(cachedLoginRequest);
		}
	}

	@Override
	public void serviceDidFailToConnect()
	{
		log("service did fail to connect");

		if (shouldAutomaticallyLoginOnConnect)
		{
			shouldAutomaticallyLoginOnConnect = false;
			loginDidComplete(getLoginResponseParser().parse(null));
		}
	}

	@Override
	public void serviceDidDisconnect()
	{
		log("service did disconnect");

		user = null;
	}

	@Override
	public void serviceDidCompleteRequest(ServiceRequest request, HashMap<String, Object> response)
	{
		log("service did complete request");

		switch(request.getAction())
		{
			case Action.LOGIN:
				loginDidComplete(getLoginResponseParser().parse(response));
				break;
		}
	}

	@Override
	public void serviceDidReceivePushMessage(HashMap<String, Object> message)
	{

	}

	// ===============================================================
	private LoginResponseParser cachedLoginParser = null;

	private LoginResponseParser getLoginResponseParser()
	{
		if (null == cachedLoginParser)
		{
			cachedLoginParser = new LoginResponseParser();
		}

		return cachedLoginParser;
	}

	// ===============================================================
	private ArrayList<ClientEventListener> eventListeners = new ArrayList<ClientEventListener>();

	public void addEventListener(ClientEventListener listener)
	{
		eventListeners.add(listener);
	}

	public void removeEventListener(ClientEventListener listener)
	{
		eventListeners.remove(listener);
	}

	private void notifyLoginCompletion(Integer result, User user)
	{
		for (int i = eventListeners.size() - 1; i >= 0; i--)
		{
			eventListeners.get(i).clientDidCompleteLogin(result, user);
		}
	}

	// ===============================================================
	private void log(String text)
	{
		Log.e("Client", text);
	}
}

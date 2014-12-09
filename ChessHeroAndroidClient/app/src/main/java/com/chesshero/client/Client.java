package com.chesshero.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.chesshero.client.parsers.CreateGameResponseParser;
import com.chesshero.client.parsers.LoginResponseParser;
import com.chesshero.client.parsers.ParserCache;
import com.chesshero.client.parsers.ResponseParser;
import com.chesshero.event.EventCenter;
import com.chesshero.service.ServerCommunicationService;
import com.chesshero.service.ServiceEventListener;
import com.chesshero.service.ServiceRequest;
import com.kt.api.Action;
import com.kt.game.Color;
import com.kt.game.Game;
import com.kt.game.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Toshko on 12/7/14.
 */
public class Client implements ServiceEventListener
{
	public static class Event
	{
		public static final String LOGOUT = "client.event.logout";
		public static final String LOGIN_RESULT = "client.event.login";
		public static final String REGISTER_RESULT = "client.event.register";
		public static final String CREATE_GAME_RESULT = "client.event.creategame";
		public static final String CANCEL_GAME_RESULT = "client.event.cancelgame";
	}

	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder)
		{
			log("connected to service");

			serviceProxy = (ServerCommunicationService.Proxy)iBinder;
			serviceProxy.addEventListener(Client.this);
			connectedToService = true;

			if (shouldAutomaticallyConnect)
			{
				serviceProxy.connect();
			}
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

	private boolean shouldAutomaticallyConnect = false;
	private boolean shouldAutomaticallyLoginOnConnect = false;
	private ServiceRequest cachedLoginRequest;

	private boolean executingRequest = false;
	private boolean shouldFailNextResponse = false;

	private Player player = null;
	private Game game = null;

	protected Client(Context context)
	{
		this.context = context;
		Intent intent = new Intent(context, ServerCommunicationService.class);
		context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public boolean isLoggedIn()
	{
		return player != null;
	}

	public Player getPlayer()
	{
		return player;
	}

	public Game getGame()
	{
		return game;
	}

	public void register(String userName, String password)
	{
		doLogin(userName, password, true);
	}

	public void login(String userName, String password)
	{
		doLogin(userName, password, false);
	}

	public void logout()
	{
		shouldAutomaticallyConnect = false;
		shouldAutomaticallyLoginOnConnect = false;
		cachedLoginRequest = null;

		if (!connectedToService || !isLoggedIn())
		{
			return;
		}

		serviceProxy.disconnect();

		if (executingRequest)
		{
			shouldFailNextResponse = true;
		}

		if (player != null)
		{
			player = null;
			game = null;
			notifyLogout();
		}
	}

	public void createGame(String name, Color color, Integer timeout)
	{
		if (!isLoggedIn())
		{
			log("unauthorized attempt to create game");
			return;
		}

		if (null == name)
		{
			log("attempting to create game without providing name");
			return;
		}

		maybeSendRequest(RequestFactory.createCreateGameRequest(name, color.toString(), timeout));
	}

	public void cancelGame(Integer gameID)
	{
		if (!isLoggedIn())
		{
			log("unauthorized attempt to cancel game");
			return;
		}

		if (null == game || game.getState() != Game.STATE_PENDING)
		{
			log("invalid attempting to cancel game - game is missing or cannot be cancelled");
			return;
		}

		if (null == gameID)
		{
			log("attempting to cancel game without providing game id");
			return;
		}

		maybeSendRequest(RequestFactory.createCancelGameRequest(gameID));
	}

	private void doLogin(String userName, String password, boolean register)
	{
		if (isLoggedIn())
		{
			log("attempting to login while a player is already logged in");
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
			else if (!connectedToService)
			{
				shouldAutomaticallyConnect = true;
			}

			shouldAutomaticallyLoginOnConnect = true;

			if (register)
			{
				cachedLoginRequest = RequestFactory.createRegisterRequest(userName, password);
			}
			else
			{
				cachedLoginRequest = RequestFactory.createLoginRequest(userName, password);
			}

			return;
		}

		if (shouldAutomaticallyLoginOnConnect)
		{
			return;
		}

		if (register)
		{
			maybeSendRequest(RequestFactory.createRegisterRequest(userName, password));
		}
		else
		{
			maybeSendRequest(RequestFactory.createLoginRequest(userName, password));
		}
	}

	private void maybeSendRequest(ServiceRequest request)
	{
		if (executingRequest)
		{
			log("attempting to execute a request while another one is in progress");
			return;
		}

		executingRequest = true;
		serviceProxy.sendRequest(request);
	}

	// ===============================================================
	private void loginDidComplete(LoginResponseParser parser)
	{
		if (parser.success)
		{
			player = new Player(parser.userID, parser.userName);
		}

		notifyLoginCompletion(parser.result);
	}

	private void registerDidComplete(LoginResponseParser parser)
	{
		if (parser.success)
		{
			player = new Player(parser.userID, parser.userName);
		}

		notifyRegisterCompletetion(parser.result);
	}

	private void createGameDidComplete(CreateGameResponseParser parser)
	{
		if (parser.success)
		{
			game = new Game(parser.gameID, parser.gameName, parser.timeout);
			game.setState(Game.STATE_PENDING);
			player.join(game, Color.fromString(parser.color));
		}

		notifyGameCreateCompletion(parser.result);
	}

	private void cancelGameDidComplete(ResponseParser parser)
	{
		if (parser.success)
		{
			player.leave();
			game = null;
		}

		notifyGameCancelCompletion(parser.result);
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
			loginDidComplete(ParserCache.getLoginResponseParser().parse(null));
		}
	}

	@Override
	public void serviceDidDisconnect()
	{
		log("service did disconnect");
		logout();
	}

	@Override
	public void serviceDidCompleteRequest(ServiceRequest request, HashMap<String, Object> response)
	{
		log("service did complete request");

		executingRequest = false;

		if (shouldFailNextResponse)
		{
			shouldFailNextResponse = false;
			response = null;
		}

		switch(request.getAction())
		{
			case Action.LOGIN:
				loginDidComplete(ParserCache.getLoginResponseParser().parse(response));
				break;

			case Action.REGISTER:
				registerDidComplete(ParserCache.getLoginResponseParser().parse(response));
				break;

			case Action.CREATE_GAME:
				createGameDidComplete(ParserCache.getCreateGameResponseParser().parse(response));
				break;

			case Action.CANCEL_GAME:
				cancelGameDidComplete(ParserCache.getGenericResponseParser().parse(response));
				break;
		}
	}

	@Override
	public void serviceDidReceivePushMessage(HashMap<String, Object> message)
	{

	}

	// ===============================================================
	private void notifyLoginCompletion(Integer result)
	{
		EventCenter.getSingleton().postEvent(Event.LOGIN_RESULT, result);
	}

	private void notifyRegisterCompletetion(Integer result)
	{
		EventCenter.getSingleton().postEvent(Event.REGISTER_RESULT, result);
	}

	private void notifyLogout()
	{
		EventCenter.getSingleton().postEvent(Event.LOGOUT, null);
	}

	private void notifyGameCreateCompletion(Integer result)
	{
		EventCenter.getSingleton().postEvent(Event.CREATE_GAME_RESULT, result);
	}

	private void notifyGameCancelCompletion(Integer result)
	{
		EventCenter.getSingleton().postEvent(Event.CANCEL_GAME_RESULT, result);
	}

	// ===============================================================
	private void log(String text)
	{
		Log.e("Client", text);
	}
}

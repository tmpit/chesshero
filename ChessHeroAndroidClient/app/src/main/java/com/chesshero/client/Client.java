package com.chesshero.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.chesshero.client.parsers.*;
import com.chesshero.event.EventCenter;
import com.chesshero.service.ServerCommunicationService;
import com.chesshero.service.ServiceEventListener;
import com.chesshero.service.ServiceRequest;
import com.kt.api.Action;
import com.kt.game.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		public static final String PENDING_GAMES_LOAD_RESULT = "client.event.pendinggames";
		public static final String JOIN_GAME_RESULT = "client.event.joingame";
		public static final String EXIT_GAME_RESULT = "client.event.exitgame";
		public static final String MOVE_RESULT = "client.event.move";
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
	private GameController gameController = null;

	private List<GameTicket> cachedPendingGames = null;

	private GameTicket currentJoinGameTicket = null;
	private String currentMove = null;

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

	public List<GameTicket> getCachedPendingGames()
	{
		return cachedPendingGames;
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
			gameController = null;
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

	public void cancelGame()
	{
		if (!isLoggedIn())
		{
			log("unauthorized attempt to cancel game");
			return;
		}

		if (null == game)
		{
			log("attempting to cancel game without being in one");
			return;
		}

		maybeSendRequest(RequestFactory.createCancelGameRequest(game.getID()));
	}

	public void loadPendingGames()
	{
		if (!isLoggedIn())
		{
			log("unauthorized attempt to load pending games");
			return;
		}

		maybeSendRequest(RequestFactory.createFetchGamesRequest("pending", null, null));
	}

	public void joinGame(GameTicket ticket)
	{
		if (!isLoggedIn())
		{
			log("unauthorized attempt to join game");
			return;
		}

		if (null == ticket)
		{
			log("attempting to join game without providing a game ticket");
			return;
		}

		currentJoinGameTicket = ticket;
		maybeSendRequest(RequestFactory.createJoinGameRequest(ticket.gameID));
	}

	public void exitGame()
	{
		if (!isLoggedIn())
		{
			log("unauthorized attempt to exit game");
			return;
		}

		if (null == gameController)
		{
			log("attempting to exit game without being in one");
			return;
		}

		maybeSendRequest(RequestFactory.createExitGameRequest(game.getID()));
	}

	public void executeMove(Position from, Position to)
	{
		if (!isLoggedIn())
		{
			log("unauthorized attempt to execute move");
			return;
		}

		if (null == from || null == to)
		{
			log("attempting to execute a move without providing from position and/or to position");
			return;
		}

		if (null == gameController)
		{
			log("attempting to execute a move without being in a game");
			return;
		}

		currentMove = Position.boardPositionFromPosition(from) + Position.boardPositionFromPosition(to);
		maybeSendRequest(RequestFactory.createMoveRequest(currentMove));
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
			log("attempting to login without providing username and/or password");
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
			player.join(game, parser.color);
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

	private void fetchGamesDidComplete(FetchGamesResponseParser parser)
	{
		if (parser.success)
		{
			cachedPendingGames = parser.games;
		}

		notifyFetchPendingGamesLoadCompletion(parser.result);
	}

	private void joinGameDidComplete(ResponseParser parser)
	{
		if (!parser.success)
		{
			notifyJoinGameCompletion(parser.result);
			return;
		}

		game = new Game(currentJoinGameTicket.gameID, currentJoinGameTicket.gameName, currentJoinGameTicket.timeout);
		Player opponent = new Player(currentJoinGameTicket.opponentID, currentJoinGameTicket.opponentName);
		opponent.join(game, currentJoinGameTicket.opponentColor);
		player.join(game, currentJoinGameTicket.opponentColor.Opposite);

		gameController = new GameController(game);
		gameController.startGame();

		currentJoinGameTicket = null;

		notifyJoinGameCompletion(parser.result);
	}

	private void exitGameDidComplete(ResponseParser parser)
	{
		if (parser.success)
		{
			player.leave();
			game = null;
			gameController = null;
		}

		notifyExitGameCompletion(parser.result);
	}

	private void moveDidComplete(ResponseParser parser)
	{
		if (parser.success)
		{
			gameController.execute(game.getTurn(), currentMove);
			currentMove = null;
		}

		notifyMoveCompletion(parser.result);
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

			case Action.FETCH_GAMES:
				fetchGamesDidComplete(ParserCache.getFetchGamesResponseParser().parse(response));
				break;

			case Action.JOIN_GAME:
				joinGameDidComplete(ParserCache.getGenericResponseParser().parse(response));
				break;

			case Action.EXIT_GAME:
				exitGameDidComplete(ParserCache.getGenericResponseParser().parse(response));
				break;

			case Action.MOVE:
				moveDidComplete(ParserCache.getGenericResponseParser().parse(response));
				break;
		}
	}

	@Override
	public void serviceDidReceivePushMessage(HashMap<String, Object> message)
	{
		log("service did receive push message");
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

	private void notifyFetchPendingGamesLoadCompletion(Integer result)
	{
		EventCenter.getSingleton().postEvent(Event.PENDING_GAMES_LOAD_RESULT, result);
	}

	private void notifyJoinGameCompletion(Integer result)
	{
		EventCenter.getSingleton().postEvent(Event.JOIN_GAME_RESULT, result);
	}

	private void notifyExitGameCompletion(Integer result)
	{
		EventCenter.getSingleton().postEvent(Event.EXIT_GAME_RESULT, result);
	}

	private void notifyMoveCompletion(Integer result)
	{
		EventCenter.getSingleton().postEvent(Event.MOVE_RESULT, result);
	}

	// ===============================================================
	private void log(String text)
	{
		Log.e("Client", text);
	}
}

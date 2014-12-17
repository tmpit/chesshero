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
import com.kt.api.Push;
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
		public static final String LOGOUT = "client.result.logout";
		public static final String LOGIN_RESULT = "client.result.login";
		public static final String REGISTER_RESULT = "client.result.register";
		public static final String CREATE_GAME_RESULT = "client.result.creategame";
		public static final String CANCEL_GAME_RESULT = "client.result.cancelgame";
		public static final String PENDING_GAMES_LOAD_RESULT = "client.result.pendinggames";
		public static final String JOIN_GAME_RESULT = "client.result.joingame";
		public static final String EXIT_GAME_RESULT = "client.result.exitgame";
		public static final String MOVE_RESULT = "client.result.move";

		public static final String JOIN_GAME_PUSH = "client.push.joingame";
		public static final String END_GAME_PUSH = "client.push.endgame";
		public static final String MOVE_PUSH = "client.push.move";
	}

	private Context context;

	private ServerCommunicationService.Proxy serviceProxy;
	private boolean connectedToService = false;

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

	private boolean shouldAutomaticallyConnect = false;
	private boolean shouldAutomaticallySendRequestOnConnect = false;
	private ServiceRequest cachedRequest = null;

	private boolean executingRequest = false;
	private boolean shouldFailNextResponse = false;

	private Player player = null;
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
		return gameController.getGame();
	}

	public List<GameTicket> getCachedPendingGames()
	{
		return cachedPendingGames;
	}

	public void register(String userName, String password)
	{
		if (null == userName || null == password || 0 == userName.trim().length() || 0 == password.trim().length())
		{
			log("attempting to register without providing username and/or password");
			return;
		}

		trySendRequest(RequestFactory.createRegisterRequest(userName.trim(), password.trim()));
	}

	public void login(String userName, String password)
	{
		if (null == userName || null == password || 0 == userName.trim().length() || 0 == password.trim().length())
		{
			log("attempting to login without providing username and/or password");
			return;
		}

		trySendRequest(RequestFactory.createLoginRequest(userName.trim(), password.trim()));
	}

	public void logout()
	{
		shouldAutomaticallyConnect = false;
		shouldAutomaticallySendRequestOnConnect = false;
		cachedRequest = null;

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
			gameController = null;
			notifyLogout();
		}
	}

	public void createGame(String name, Color color)
	{
		if (null == name || 0 == name.trim().length())
		{
			log("attempting to create game without providing name");
			return;
		}

		trySendRequest(RequestFactory.createCreateGameRequest(name.trim(), color.toString(), Game.NO_TIMEOUT));
	}

	public void cancelGame()
	{
		if (null == gameController)
		{
			log("attempting to cancel game without being in one");
			return;
		}

		trySendRequest(RequestFactory.createCancelGameRequest(gameController.getGame().getID()));
	}

	public void loadPendingGames()
	{
		trySendRequest(RequestFactory.createFetchGamesRequest("pending", null, null));
	}

	public void joinGame(GameTicket ticket)
	{
		if (null == ticket)
		{
			log("attempting to join game without providing a game ticket");
			return;
		}

		currentJoinGameTicket = ticket;
		trySendRequest(RequestFactory.createJoinGameRequest(ticket.gameID));
	}

	public void exitGame()
	{
		if (null == gameController)
		{
			log("attempting to exit game without being in one");
			return;
		}

		trySendRequest(RequestFactory.createExitGameRequest(gameController.getGame().getID()));
	}

	public void executeMove(Position from, Position to)
	{
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
		trySendRequest(RequestFactory.createMoveRequest(currentMove));
	}

	private void trySendRequest(ServiceRequest request)
	{
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

			shouldAutomaticallySendRequestOnConnect = true;
			cachedRequest = request;

			return;
		}

		if (shouldAutomaticallySendRequestOnConnect || executingRequest)
		{
			log("attempting to send request while another one is executing");
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
			gameController = new GameController(new Game(parser.gameID, parser.gameName, parser.timeout), new MasterChessMoveExecutor());
			gameController.addPlayer(player, parser.color);
		}

		notifyGameCreateCompletion(parser.result);
	}

	private void cancelGameDidComplete(ResponseParser parser)
	{
		if (parser.success)
		{
			gameController.removePlayers();
			gameController = null;
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

		Game game = new Game(currentJoinGameTicket.gameID, currentJoinGameTicket.gameName, currentJoinGameTicket.timeout);
		Player opponent = new Player(currentJoinGameTicket.opponentID, currentJoinGameTicket.opponentName);

		gameController = new GameController(game, new MasterChessMoveExecutor());
		gameController.addPlayer(opponent, currentJoinGameTicket.opponentColor);
		gameController.addPlayer(player, currentJoinGameTicket.opponentColor.Opposite);
		gameController.startGame();

		currentJoinGameTicket = null;

		notifyJoinGameCompletion(parser.result);
	}

	private void exitGameDidComplete(ResponseParser parser)
	{
		if (parser.success)
		{
			gameController.removePlayers();
			gameController = null;
		}

		notifyExitGameCompletion(parser.result);
	}

	private void moveDidComplete(ResponseParser parser)
	{
		if (parser.success)
		{
			gameController.executeMove(player, currentMove);
			currentMove = null;
		}

		notifyMoveCompletion(parser.result);
	}

	private void didReceiveGameJoinPush(GameJoinPushParser parser)
	{
		Player opponent = new Player(parser.opponentID, parser.opponentName);
		gameController.addPlayer(opponent, player.getColor().Opposite);
		gameController.startGame();

		notifyJoinGamePush();
	}

	private void didReceiveGameEndPush(GameEndPushParser parser)
	{
		Player winner = null;

		if (parser.winnerID != null)
		{
			winner = parser.winnerID == player.getUserID() ? player : player.getOpponent();
		}

		gameController.endGame(winner, parser.gameEnding);

		notifyEndGamePush();
	}

	private void didReceiveGameMovePush(GameMovePushParser parser)
	{
		gameController.executeMove(player.getOpponent(), parser.move);

		notifyGameMovePush();
	}

	// ===============================================================
	@Override
	public void serviceDidConnect()
	{
		log("service did connect");

		if (shouldAutomaticallySendRequestOnConnect)
		{
			shouldAutomaticallySendRequestOnConnect = false;
			trySendRequest(cachedRequest);
		}
	}

	@Override
	public void serviceDidFailToConnect()
	{
		log("service did fail to connect");

		if (shouldAutomaticallySendRequestOnConnect)
		{
			shouldAutomaticallySendRequestOnConnect = false;
			serviceDidCompleteRequest(cachedRequest, null);
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

		Integer event = (Integer)message.get("event");

		switch (event)
		{
			case Push.GAME_JOIN:
				didReceiveGameJoinPush(ParserCache.getGameJoinPushParser().parse(message));
				break;

			case Push.GAME_END:
				didReceiveGameEndPush(ParserCache.getGameEndPushParser().parse(message));
				break;

			case Push.GAME_MOVE:
				didReceiveGameMovePush(ParserCache.getGameMovePushParser().parse(message));
				break;
		}
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
		EventCenter.getSingleton().postEvent(Event.LOGOUT);
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

	private void notifyJoinGamePush()
	{
		EventCenter.getSingleton().postEvent(Event.JOIN_GAME_PUSH);
	}

	private void notifyEndGamePush()
	{
		EventCenter.getSingleton().postEvent(Event.END_GAME_PUSH);
	}

	private void notifyGameMovePush()
	{
		EventCenter.getSingleton().postEvent(Event.MOVE_PUSH);
	}

	// ===============================================================
	private void log(String text)
	{
		Log.e("Client", text);
	}
}

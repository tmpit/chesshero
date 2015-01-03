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
	// These are event names for all events that the Client can notify observers about
	// Events are posted through the EventCenter
	// Each event may or may not have an Integer result code passed as user data to the EventCenter
	// All events that end in '_RESULT' have an associated result code
	// All result codes are from the com.kt.api.Result class
	public static class Event
	{
		// The user has been logged out
		// Only posted if a user had been logged in/registered beforehand
		public static final String LOGOUT = "client.result.logout";

		// Posted when a login request completes
		public static final String LOGIN_RESULT = "client.result.login";

		// Posted when a register request completes
		public static final String REGISTER_RESULT = "client.result.register";

		// Posted when a create game request completes
		public static final String CREATE_GAME_RESULT = "client.result.creategame";

		// Posted when a cancel game request completes
		public static final String CANCEL_GAME_RESULT = "client.result.cancelgame";

		// Posted when a pending games load request completes
		public static final String PENDING_GAMES_LOAD_RESULT = "client.result.pendinggames";

		// Posted when a join game request completes
		public static final String JOIN_GAME_RESULT = "client.result.joingame";

		// Posted when an exit game request completes
		public static final String EXIT_GAME_RESULT = "client.result.exitgame";

		// Posted when a move request completes
		public static final String MOVE_RESULT = "client.result.move";

		// Posted when a join game push message is received
		// When you receive this event, it means that an opponent has joined your game
		// and that the game has started
		public static final String JOIN_GAME_PUSH = "client.push.joingame";

		// Posted when an end game push message is received
		// When you receive this event, it means that the game has ended
		// All game state and information regarding how the game has ended is kept in the Game object
		public static final String END_GAME_PUSH = "client.push.endgame";

		// Posted when a move push message is received
		// When you receive this message, it means that your opponent has executed a move
		// You can fetch the move via the Game object's list of executed moves
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

	// Returns true if a user is logged in
	public boolean isLoggedIn()
	{
		return player != null;
	}

	// Returns the currently logged in user
	// A user exists after successful login or register and is destroyed on logout
	public Player getPlayer()
	{
		return player;
	}

	// Returns the last created/played game
	// Will return an existing game after a successful create game or join game request
	// The game is destroyed on logout or after a successful cancel game request and otherwise is cached until the next game creation
	// so even after a game has ended you can fetch the game object and query it for status
	// That is how, for example, you can find out who won the current game and how did it end
	public Game getGame()
	{
		return gameController.getGame();
	}

	// Returns the list of pending games cached from the last loadPendingGames request
	// Will be null if there are no games on the server or no load request has been made
	public List<GameTicket> getCachedPendingGames()
	{
		return cachedPendingGames;
	}

	// Attempts to register a user
	// Registration also logs the user in
	// You must provide a username and a password
	public void register(String userName, String password)
	{
		if (null == userName || null == password || 0 == userName.trim().length() || 0 == password.trim().length())
		{
			log("attempting to register without providing username and/or password");
			return;
		}

		trySendRequest(RequestFactory.createRegisterRequest(userName.trim(), password.trim()));
	}

	// Attempts to log a user in
	// You must provide a username and a password
	public void login(String userName, String password)
	{
		if (null == userName || null == password || 0 == userName.trim().length() || 0 == password.trim().length())
		{
			log("attempting to login without providing username and/or password");
			return;
		}

		trySendRequest(RequestFactory.createLoginRequest(userName.trim(), password.trim()));
	}

	// Logs out the user and clears game
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

	// Attempts to create a game
	// You must provide the game name
	// The player's color is optional and if not provided, a white color will be chosen
	public void createGame(String name, Color color)
	{
		if (null == name || 0 == name.trim().length())
		{
			log("attempting to create game without providing name");
			return;
		}

		trySendRequest(RequestFactory.createCreateGameRequest(name.trim(), color.toString(), Game.NO_TIMEOUT));
	}

	// Attempts to cancel the current game
	// You can cancel the current game only if you've created it and an opponent has not joined yet
	public void cancelGame()
	{
		if (null == gameController)
		{
			log("attempting to cancel game without being in one");
			return;
		}

		trySendRequest(RequestFactory.createCancelGameRequest(gameController.getGame().getID()));
	}

	// Attempts to load and cache the pending games
	// Pending games are games in which a player waits for an opponent to join
	public void loadPendingGames()
	{
		trySendRequest(RequestFactory.createFetchGamesRequest("pending", null, null));
	}

	// Attempts to join a game
	// You must provide a valid game ticket
	// A ticket can be acquired from the list returned by getCachedPendingGames method
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

	// Attempts to exit the current game
	// You can exit the game any time when the game is running
	// You cannot exit the game if you are waiting for an opponent to join
	// You lose the game when you successfully exit
	public void exitGame()
	{
		if (null == gameController)
		{
			log("attempting to exit game without being in one");
			return;
		}

		trySendRequest(RequestFactory.createExitGameRequest(gameController.getGame().getID()));
	}

	// Attempts to execute a move
	// You must provide from and to positions
	public void executeMove(Position from, Position to)
	{
		executeMove(from, to, null);
	}

	// Attempts to execute a move
	// You must provide from and to positions
	// The promotion is optional. You can always pass promotion and until the server requires it, it will be ignored
	// Otherwise, if the server requires it and no promotion is provided, the move will fail
	public void executeMove(Position from, Position to, Promotion promotion)
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

		if (promotion != null)
		{
			currentMove += promotion.toString();
		}

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
			gameController = new GameController(new Game(parser.gameID, parser.gameName, parser.timeout), new MasterChessMoveExecutor(), false);
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

		gameController = new GameController(game, new MasterChessMoveExecutor(), false);
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
		}

		notifyExitGameCompletion(parser.result);
	}

	private void moveDidComplete(GameMoveResponseParser parser)
	{
		if (parser.success)
		{
			gameController.executeMove(player, currentMove);
			currentMove = null;

			if (parser.playerTime != null)
			{
				gameController.setPlayerMillisPlayed(player, parser.playerTime);
			}
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

		if (parser.playerTime != null)
		{
			gameController.setPlayerMillisPlayed(player.getOpponent(), parser.playerTime);
		}

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
				moveDidComplete(ParserCache.getGameMoveResponseParser().parse(response));
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

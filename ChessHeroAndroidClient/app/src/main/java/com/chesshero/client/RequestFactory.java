package com.chesshero.client;

import com.chesshero.service.ServiceRequest;
import com.kt.api.Action;

/**
 * Created by Toshko on 12/8/14.
 *
 * A class used for generation of @{code ServiceRequest} objects that can be sent through a @{code ServerCommunicationService}.
 * @see com.kt.api.Action
 */
public class RequestFactory
{
	/**
	 * Creates a @{code ServiceRequest} object for a registration request
	 * @param userName A username with which to register. Must not be @{code null}
	 * @param password A password with which to register. Must not be @{code null}
	 * @return A @{code ServiceRequest} object for a registration request
	 */
	public static ServiceRequest createRegisterRequest(String userName, String password)
	{
		ServiceRequest request = new ServiceRequest(Action.REGISTER);
		request.addParameter("username", userName);
		request.addParameter("password", password);
		return request;
	}

	/**
	 * Creates a @{code ServiceRequest} object for a login request
	 * @param userName A username with which to login. Must not be @{code null}
	 * @param password A password with which to login. Must not be @{code null}
	 * @return A @{code ServiceRequest} object for a login request
	 */
	public static ServiceRequest createLoginRequest(String userName, String password)
	{
		ServiceRequest request = new ServiceRequest(Action.LOGIN);
		request.addParameter("username", userName);
		request.addParameter("password", password);
		return request;
	}

	/**
	 * Creates a @{code ServiceRequest} object for a create game request
	 * @param name The name of the game. Must not be @{code null}
	 * @param color The color which the creator of the game will play as. If @{code null} provided, white will be chosen
	 * @param timeout The game timeout measured in minutes. If @{code null} provided, the game will have no timeout
	 * @return A @{code ServiceRequest} object for a create game request
	 */
	public static ServiceRequest createCreateGameRequest(String name, String color, Integer timeout)
	{
		ServiceRequest request = new ServiceRequest(Action.CREATE_GAME);
		request.addParameter("gamename", name);

		if (color != null)
		{
			request.addParameter("color", color);
		}

		if (timeout != null)
		{
			request.addParameter("timeout", timeout);
		}

		return request;
	}

	/**
	 * Creates a @{code ServiceRequest} object for a cancel game request
	 * @param gameID The id of the game to cancel. Must not be @{code null}
	 * @return A @{code ServiceRequest} object for a cancel game request
	 */
	public static ServiceRequest createCancelGameRequest(Integer gameID)
	{
		ServiceRequest request = new ServiceRequest(Action.CANCEL_GAME);
		request.addParameter("gameid", gameID);
		return request;
	}

	/**
	 * Creates a @{code ServiceRequest} object for a fetch games request
	 * @param type The type of games to look for
	 *             @see com.kt.api.Action
	 * @param offset The offset within the result set to start fetching from
	 * @param limit The number of results to fetch starting from @{code offset}
	 * @return A @{code ServiceRequest} object for a fetch games request
	 */
	public static ServiceRequest createFetchGamesRequest(String type, Integer offset, Integer limit)
	{
		ServiceRequest request = new ServiceRequest(Action.FETCH_GAMES);

		if (type != null)
		{
			request.addParameter("type", type);
		}

		if (offset != null)
		{
			request.addParameter("offset", offset);
		}

		if (limit != null)
		{
			request.addParameter("limit", limit);
		}

		return request;
	}

	/**
	 * Creates a @{code ServiceRequest} object for a join game request
	 * @param gameID The game id of the game to join. Must not be @{code null}
	 * @return A @{code ServiceRequest} object for a join game request
	 */
	public static ServiceRequest createJoinGameRequest(Integer gameID)
	{
		ServiceRequest request = new ServiceRequest(Action.JOIN_GAME);
		request.addParameter("gameid", gameID);
		return request;
	}

	/**
	 * Creates a @{code ServiceRequest} object for an exit game request
	 * @param gameID The game id of the game to exit. Must not be @{code null}
	 * @return A @{code ServiceRequest} object for an exit game request
	 */
	public static ServiceRequest createExitGameRequest(Integer gameID)
	{
		ServiceRequest request = new ServiceRequest(Action.EXIT_GAME);
		request.addParameter("gameid", gameID);
		return request;
	}

	/**
	 * Creates a @{code ServiceRequest} object for an game move request
	 * @param move The encoded chess move. Must not be @{code null}
	 * @return A @{code ServiceRequest} object for an game move request
	 */
	public static ServiceRequest createMoveRequest(String move)
	{
		ServiceRequest request = new ServiceRequest(Action.MOVE);
		request.addParameter("move", move);
		return request;
	}
}

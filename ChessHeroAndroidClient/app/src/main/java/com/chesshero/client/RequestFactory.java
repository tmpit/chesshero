package com.chesshero.client;

import com.chesshero.service.ServiceRequest;
import com.kt.api.Action;

/**
 * Created by Toshko on 12/8/14.
 */
public class RequestFactory
{
	public static ServiceRequest createRegisterRequest(String userName, String password)
	{
		ServiceRequest request = new ServiceRequest(Action.REGISTER);
		request.addParameter("username", userName);
		request.addParameter("password", password);
		return request;
	}

	public static ServiceRequest createLoginRequest(String userName, String password)
	{
		ServiceRequest request = new ServiceRequest(Action.LOGIN);
		request.addParameter("username", userName);
		request.addParameter("password", password);
		return request;
	}

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

	public static ServiceRequest createCancelGameRequest(Integer gameID)
	{
		ServiceRequest request = new ServiceRequest(Action.CANCEL_GAME);
		request.addParameter("gameid", gameID);
		return request;
	}

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

	public static ServiceRequest createJoinGameRequest(Integer gameID)
	{
		ServiceRequest request = new ServiceRequest(Action.JOIN_GAME);
		request.addParameter("gameid", gameID);
		return request;
	}

	public static ServiceRequest createExitGameRequest(Integer gameID)
	{
		ServiceRequest request = new ServiceRequest(Action.EXIT_GAME);
		request.addParameter("gameid", gameID);
		return request;
	}

	public static ServiceRequest createMoveRequest(String move)
	{
		ServiceRequest request = new ServiceRequest(Action.MOVE);
		request.addParameter("move", move);
		return request;
	}
}

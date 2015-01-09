package com.chesshero.client.parsers;

/**
 * Created by Toshko on 12/9/14.
 */
public class ParserCache
{
	private static ResponseParser genericParser = null;

	public static ResponseParser getGenericResponseParser()
	{
		if (null == genericParser)
		{
			genericParser = new ResponseParser();
		}

		return genericParser;
	}

	private static LoginResponseParser loginParser = null;

	public static LoginResponseParser getLoginResponseParser()
	{
		if (null == loginParser)
		{
			loginParser = new LoginResponseParser();
		}

		return loginParser;
	}

	private static CreateGameResponseParser createGameParser = null;

	public static CreateGameResponseParser getCreateGameResponseParser()
	{
		if (null == createGameParser)
		{
			createGameParser = new CreateGameResponseParser();
		}

		return createGameParser;
	}

	private static FetchGamesResponseParser fetchGamesParser = null;

	public static FetchGamesResponseParser getFetchGamesResponseParser()
	{
		if (null == fetchGamesParser)
		{
			fetchGamesParser = new FetchGamesResponseParser();
		}

		return fetchGamesParser;
	}

	private static GameMoveResponseParser gameMoveResponseParser = null;

	public static GameMoveResponseParser getGameMoveResponseParser()
	{
		if (null == gameMoveResponseParser)
		{
			gameMoveResponseParser = new GameMoveResponseParser();
		}

		return gameMoveResponseParser;
	}

	private static GameJoinPushParser gameJoinParser = null;

	public static GameJoinPushParser getGameJoinPushParser()
	{
		if (null == gameJoinParser)
		{
			gameJoinParser = new GameJoinPushParser();
		}

		return gameJoinParser;
	}

	private static GameEndPushParser gameEndParser = null;

	public static GameEndPushParser getGameEndPushParser()
	{
		if (null == gameEndParser)
		{
			gameEndParser = new GameEndPushParser();
		}

		return gameEndParser;
	}

	private static GameMovePushParser gameMoveParser = null;

	public static GameMovePushParser getGameMovePushParser()
	{
		if (null == gameMoveParser)
		{
			gameMoveParser = new GameMovePushParser();
		}

		return gameMoveParser;
	}
}

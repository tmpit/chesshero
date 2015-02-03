package com.chesshero.client.parsers;

/**
 * Created by Toshko on 12/9/14.
 *
 * A class containing static references to parsers. Since parsers are reusable objects, this class lazy loads them on demand
 * and caches them for future use
 */
public class ParserCache
{
	private static ResponseParser genericParser = null;

	/**
	 * Gets a generic response parser. This parser's state may be cached from the previous request
	 * @return A @{code ResponseParser} instance
	 */
	public static ResponseParser getGenericResponseParser()
	{
		if (null == genericParser)
		{
			genericParser = new ResponseParser();
		}

		return genericParser;
	}

	private static LoginResponseParser loginParser = null;

	/**
	 * Gets a response parser for a response from a login request. This parser's state may be cached from the previous request
	 * @return A @{code LoginResponseParser} instance
	 */
	public static LoginResponseParser getLoginResponseParser()
	{
		if (null == loginParser)
		{
			loginParser = new LoginResponseParser();
		}

		return loginParser;
	}

	private static CreateGameResponseParser createGameParser = null;

	/**
	 * Gets a response parser for a response from a create game request. This parser's state may be cached from the previous request
	 * @return A @{code CreateGameResponseParser} instance
	 */
	public static CreateGameResponseParser getCreateGameResponseParser()
	{
		if (null == createGameParser)
		{
			createGameParser = new CreateGameResponseParser();
		}

		return createGameParser;
	}

	private static FetchGamesResponseParser fetchGamesParser = null;

	/**
	 * Gets a response parser for a response from a fetch games request. This parser's state may be cached from the previous request
	 * @return A @{code FetchGamesResponseParser} instance
	 */
	public static FetchGamesResponseParser getFetchGamesResponseParser()
	{
		if (null == fetchGamesParser)
		{
			fetchGamesParser = new FetchGamesResponseParser();
		}

		return fetchGamesParser;
	}

	private static GameMoveResponseParser gameMoveResponseParser = null;

	/**
	 * Gets a response parser for a response from a game move request. This parser's state may be cached from the previous request
	 * @return A @{code GameMoveResponseParser} instance
	 */
	public static GameMoveResponseParser getGameMoveResponseParser()
	{
		if (null == gameMoveResponseParser)
		{
			gameMoveResponseParser = new GameMoveResponseParser();
		}

		return gameMoveResponseParser;
	}

	private static GameJoinPushParser gameJoinParser = null;

	/**
	 * Gets a parser for a game join push message. This parser's state may be cached from the previous push message
	 * @return A @{code GameJoinPushParser} instance
	 */
	public static GameJoinPushParser getGameJoinPushParser()
	{
		if (null == gameJoinParser)
		{
			gameJoinParser = new GameJoinPushParser();
		}

		return gameJoinParser;
	}

	private static GameEndPushParser gameEndParser = null;

	/**
	 * Gets a parser for a game end push message. This parser's state may be cached from the previous push message
	 * @return A @{code GameEndPushParser} instance
	 */
	public static GameEndPushParser getGameEndPushParser()
	{
		if (null == gameEndParser)
		{
			gameEndParser = new GameEndPushParser();
		}

		return gameEndParser;
	}

	private static GameMovePushParser gameMoveParser = null;

	/**
	 * Gets a parser for a game move push message. This parser's state may be cached from the previous push message
	 * @return A @{code GameMovePushParser} instance
	 */
	public static GameMovePushParser getGameMovePushParser()
	{
		if (null == gameMoveParser)
		{
			gameMoveParser = new GameMovePushParser();
		}

		return gameMoveParser;
	}
}

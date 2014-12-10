package com.chesshero.client.parsers;

/**
 * Created by Toshko on 12/9/14.
 */
public class ParserCache
{
	private static ResponseParser genericParser = null;
	private static LoginResponseParser loginParser = null;
	private static CreateGameResponseParser createGameParser = null;
	private static FetchGamesResponseParser fetchGamesParser = null;
	private static ExitGameResponseParser exitGameParser = null;

	public static ResponseParser getGenericResponseParser()
	{
		if (null == genericParser)
		{
			genericParser = new ResponseParser();
		}

		return genericParser;
	}

	public static LoginResponseParser getLoginResponseParser()
	{
		if (null == loginParser)
		{
			loginParser = new LoginResponseParser();
		}

		return loginParser;
	}

	public static CreateGameResponseParser getCreateGameResponseParser()
	{
		if (null == createGameParser)
		{
			createGameParser = new CreateGameResponseParser();
		}

		return createGameParser;
	}

	public static FetchGamesResponseParser getFetchGamesResponseParser()
	{
		if (null == fetchGamesParser)
		{
			fetchGamesParser = new FetchGamesResponseParser();
		}

		return fetchGamesParser;
	}
}

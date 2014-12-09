package com.chesshero.client.parsers;

/**
 * Created by Toshko on 12/9/14.
 */
public class ParserCache
{
	private static ResponseParser cachedGenericParser = null;
	private static LoginResponseParser cachedLoginParser = null;
	private static CreateGameResponseParser cachedCreateGameParser = null;

	public static ResponseParser getGenericResponseParser()
	{
		if (null == cachedGenericParser)
		{
			cachedGenericParser = new ResponseParser();
		}

		return cachedGenericParser;
	}

	public static LoginResponseParser getLoginResponseParser()
	{
		if (null == cachedLoginParser)
		{
			cachedLoginParser = new LoginResponseParser();
		}

		return cachedLoginParser;
	}

	public static CreateGameResponseParser getCreateGameResponseParser()
	{
		if (null == cachedCreateGameParser)
		{
			cachedCreateGameParser = new CreateGameResponseParser();
		}

		return cachedCreateGameParser;
	}
}

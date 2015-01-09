package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 1/3/15.
 */
public class GameMoveResponseParser extends ResponseParser
{
	public Long playerTime;

	@Override
	protected void reset()
	{
		super.reset();

		playerTime = null;
	}

	@Override
	public GameMoveResponseParser parse(HashMap<String, Object> response)
	{
		super.parse(response);

		if (success && response.containsKey("playertime"))
		{
			playerTime = (Long)response.get("playertime");
		}

		return this;
	}
}

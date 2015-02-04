package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 1/3/15.
 *
 * Instances of this class can parse the response returned from a game move request as described in
 * {@link com.kt.api.Action}
 */
public class GameMoveResponseParser extends ResponseParser
{
	/**
	 * The current in-game time of the player executing the move. Can be {@code null} if the game has no timeout
	 */
	public Integer playerTime;

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
			playerTime = (Integer)response.get("playertime");
		}

		return this;
	}
}

package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 *
 * Instances of this class can parse a chess move push message as described in {@link com.kt.api.Push}
 */
public class GameMovePushParser extends PushParser
{
	/**
	 * The encoded chess move
	 */
	public String move;

	/**
	 * The current in-game time of the player executing the move. Can be {@code null} if the game has no timeout
	 */
	public Integer playerTime;

	@Override
	protected void reset()
	{
		move = null;
		playerTime = null;
	}

	@Override
	public GameMovePushParser parse(HashMap<String, Object> message)
	{
		super.parse(message);

		move = (String)message.get("move");

		if (message.containsKey("playertime"))
		{
			playerTime = (Integer)message.get("playertime");
		}

		return this;
	}
}

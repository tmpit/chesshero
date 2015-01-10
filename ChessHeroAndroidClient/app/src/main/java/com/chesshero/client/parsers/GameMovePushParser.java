package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 */
public class GameMovePushParser extends PushParser
{
	public String move;
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

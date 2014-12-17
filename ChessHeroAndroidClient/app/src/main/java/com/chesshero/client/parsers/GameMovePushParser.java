package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 */
public class GameMovePushParser extends PushParser
{
	public String move;

	@Override
	protected void reset()
	{
		move = null;
	}

	@Override
	public GameMovePushParser parse(HashMap<String, Object> message)
	{
		super.parse(message);

		move = (String)message.get("move");

		return this;
	}
}

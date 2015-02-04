package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 *
 * Instances of this class can parse a game join push message as described in {@link com.kt.api.Push}
 */
public class GameJoinPushParser extends PushParser
{
	/**
	 * The user id of the opponent that joined the game
	 */
	public Integer opponentID;

	/**
	 * The name of the opponent that joined the game
	 */
	public String opponentName;

	@Override
	protected void reset()
	{
		opponentID = null;
		opponentName = null;
	}

	@Override
	public GameJoinPushParser parse(HashMap<String, Object> message)
	{
		super.parse(message);

		opponentID = (Integer)message.get("opponentid");
		opponentName = (String)message.get("opponentname");

		return this;
	}
}

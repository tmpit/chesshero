package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 */
public class GameJoinPushParser extends PushParser
{
	public Integer opponentID;
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

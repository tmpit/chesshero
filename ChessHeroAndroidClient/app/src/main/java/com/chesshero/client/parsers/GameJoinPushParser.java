package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 */
public class GameJoinPushParser extends PushParser
{
	Integer opponentID;
	String opponentName;

	@Override
	protected void reset()
	{
		opponentID = null;
		opponentName = null;
	}

	@Override
	public PushParser parse(HashMap<String, Object> message)
	{
		super.parse(message);

		opponentID = (Integer)message.get("opponentid");
		opponentName = (String)message.get("opponentname");

		return this;
	}
}

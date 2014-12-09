package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/9/14.
 */
public class CreateGameResponseParser extends ResponseParser
{
	public Integer gameID;
	public String gameName;
	public String chatToken;
	public String color;
	public Integer timeout;

	@Override
	protected void reset()
	{
		super.reset();

		gameID = null;
		gameName = null;
		chatToken = null;
		color = null;
		timeout = null;
	}

	@Override
	public CreateGameResponseParser parse(HashMap<String, Object> response)
	{
		super.parse(response);

		if (!success)
		{
			return this;
		}

		gameID = (Integer)response.get("gameid");
		chatToken = (String)response.get("chattoken");
		gameName = (String)response.get("gamename");
		color = (String)response.get("color");
		timeout = (Integer)response.get("timeout");

		return this;
	}
}

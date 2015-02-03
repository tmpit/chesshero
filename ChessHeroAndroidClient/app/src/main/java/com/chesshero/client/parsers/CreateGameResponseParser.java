package com.chesshero.client.parsers;

import com.kt.game.Color;

import java.util.HashMap;

/**
 * Created by Toshko on 12/9/14.
 *
 * Instances of this class can parse the response returned from a create game request as described in
 * @{link com.kt.api.Action}
 */
public class CreateGameResponseParser extends ResponseParser
{
	/**
	 * The game id of the created game
	 */
	public Integer gameID;

	/**
	 * The name of the created game
	 */
	public String gameName;

	/**
	 * The game creator's chat token
	 */
	public String chatToken;

	/**
	 * The game creator's in-game color
	 */
	public Color color;

	/**
	 * The game timeout value (in minutes) of the created game
	 */
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
		color = Color.fromString((String)response.get("color"));
		timeout = (Integer)response.get("timeout");

		return this;
	}
}

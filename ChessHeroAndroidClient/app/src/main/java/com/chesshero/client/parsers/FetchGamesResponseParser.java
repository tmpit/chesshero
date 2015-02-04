package com.chesshero.client.parsers;

import com.kt.game.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Toshko on 12/10/14.
 *
 * Instances of this class can parse the response returned from a fetch games request as described in {@link com.kt.api.Action}
 */
public class FetchGamesResponseParser extends ResponseParser
{
	/**
	 * The list of {@code GameTicket} objects describing the pending games on the server. Can be {@code null} if there are no
	 * pending games
	 */
	public List<GameTicket> games;

	@Override
	protected void reset()
	{
		super.reset();

		games = null;
	}

	@Override
	public FetchGamesResponseParser parse(HashMap<String, Object> response)
	{
		super.parse(response);

		if (!success)
		{
			return this;
		}

		List<Map<String, Object>> gameEntries = (List<Map<String, Object>>)response.get("games");

		if (null == gameEntries || 0 == gameEntries.size())
		{
			return this;
		}

		games = new ArrayList<GameTicket>(gameEntries.size());

		for (Map<String, Object> entry : gameEntries)
		{
			Integer gameID = (Integer)entry.get("gameid");
			String gameName = (String)entry.get("gamename");
			Integer timeout = (Integer)entry.get("timeout");
			Integer userID = (Integer)entry.get("userid");
			String userName = (String)entry.get("username");
			String userColor = (String)entry.get("usercolor");

			games.add(new GameTicket(gameID, gameName, timeout, userID, userName, Color.fromString(userColor)));
		}

		return this;
	}
}

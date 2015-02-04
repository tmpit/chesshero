package com.chesshero.client.parsers;

import com.kt.game.Game;
import com.kt.game.Player;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 *
 * Instances of this class can parse an end game push message as described in {@link com.kt.api.Push}
 */
public class GameEndPushParser extends PushParser
{
	/**
	 * The user id of the winner of the game
	 */
	public Integer winnerID;

	/**
	 * Describes how the game has ended
	 */
	public Game.Ending gameEnding;

	@Override
	protected void reset()
	{
		winnerID = null;
		gameEnding = null;
	}

	@Override
	public GameEndPushParser parse(HashMap<String, Object> message)
	{
		super.parse(message);

		winnerID = (Integer)message.get("winner");

		if (message.containsKey("suddendeath"))
		{
			gameEnding = Game.Ending.SUDDED_DEATH;
		}
		else if (message.containsKey("checkmate"))
		{
			gameEnding = Game.Ending.CHECKMATE;
		}
		else
		{
			gameEnding = Game.Ending.SURRENDER;
		}

		return this;
	}
}

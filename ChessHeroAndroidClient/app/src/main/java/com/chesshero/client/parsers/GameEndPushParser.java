package com.chesshero.client.parsers;

import com.kt.game.Game;
import com.kt.game.Player;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 */
public class GameEndPushParser extends PushParser
{
	public Player winner;
	public Game.Ending gameEnding;

	@Override
	protected void reset()
	{
		winner = null;
		gameEnding = null;
	}

	@Override
	public GameEndPushParser parse(HashMap<String, Object> message)
	{
		super.parse(message);

		winner = (Player)message.get("winner");

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

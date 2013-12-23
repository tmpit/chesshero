package com.kt.game;

import com.kt.api.Result;

/**
 * Created by Toshko on 12/23/13.
 */
public class GameController
{
	private Game game;

	public GameController(Game game)
	{
		this.game = game;
		game.controller = this;
	}

	public int execute(Position from, Position to)
	{
		return Result.OK;
	}

	// Used for prematurely ending a game
	public void endGame(Player winner)
	{
		if (Game.STATE_FINISHED == game.getState())
		{
			return;
		}

		game.setState(Game.STATE_FINISHED);
		game.winner = winner;
	}
}

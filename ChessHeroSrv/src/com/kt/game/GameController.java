package com.kt.game;

import com.kt.api.Result;

/**
 * Created by Toshko on 12/9/13.
 */
public class GameController
{
    private Game game;

    public GameController(Game game)
    {
        this.game = game;
        game.controller = this;
    }

	public Player getWinner()
	{
		return null;
	}

	public int execute(Position from, Position to)
	{
		return Result.OK;
	}
}

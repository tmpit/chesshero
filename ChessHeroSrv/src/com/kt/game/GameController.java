package com.kt.game;

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
}

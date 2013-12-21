package com.kt.game;

import com.kt.ClientConnection;

/**
 * Created by Toshko on 12/9/13.
 */
public class Player
{
    private int userID;
    private String name;
    private Game game;

    public enum Color {NONE, WHITE, BLACK}
    private Color color = Color.NONE;

    public Player(int userID, String name, ClientConnection connection)
    {
        this.userID = userID;
        this.name = name;
    }

    public int getUserID()
    {
        return userID;
    }

    public String getName()
    {
        return name;
    }

    public Game getGame()
    {
        return game;
    }

    public Color getColor()
    {
        return color;
    }

    public boolean join(Game game, Color color)
    {
        if (null == game.player1)
        {
            game.player1 = this;
            this.game = game;
            this.color = color;
            return true;
        }

        if (null == game.player2)
        {
            game.player2 = this;
            this.game = game;
            this.color = color;
            return true;
        }

        return false;
    }

    public void leave()
    {
        if (null == game)
        {
            return;
        }

        if (game.player1 == this)
        {
            game.player1 = null;
        }

        if (game.player2 == this)
        {
            game.player2 = null;
        }

        game = null;
        color = Color.NONE;
    }

    public Player getOpponent()
    {
        if (null == game)
        {
            return null;
        }

        if (game.player1 == this)
        {
            return game.player2;
        }

        return game.player1;
    }

	public String toString()
	{
		return "<Player :: userid: " + userID + ", name: " + name + ", color: " + (color == Color.WHITE ? "white" : "black") + ">";
	}
}

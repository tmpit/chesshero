package com.kt.game;

import com.kt.ClientConnection;

/**
 * Created by Toshko on 12/9/13.
 */
public class Player
{
    private int userID;
    private String name;
    private ClientConnection connection;
    private Game game;

    public Player(int userID, String name, ClientConnection connection)
    {
        this.userID = userID;
        this.name = name;
        this.connection = connection;
    }

    public int getUserID()
    {
        return userID;
    }

    public String getName()
    {
        return name;
    }

    public ClientConnection getConnection()
    {
        return connection;
    }

    public Game getGame()
    {
        return game;
    }

    public boolean join(Game game)
    {
        if (null == game.player1)
        {
            game.player1 = this;
            this.game = game;
            return true;
        }

        if (null == game.player2)
        {
            game.player2 = this;
            this.game = game;
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
}

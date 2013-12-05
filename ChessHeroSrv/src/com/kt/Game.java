package com.kt;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 12/5/13
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game
{
    public static short STATE_INIT      = 0; // Just initialized, not yet in the list
    public static short STATE_PENDING   = 1; // Inside the list with created games
    public static short STATE_STARTED   = 2; // The game is being played
    public static short STATE_FINISHED  = 3; // The game has finished

    private static ArrayList<Game> games = new ArrayList<Game>();
    private static Database database = new Database();

    private int gameID;

    private String name;

    private int user1ID;
    private int user2ID;

    private ClientConnection client1;
    private ClientConnection client2;

    private short state = STATE_INIT;

    public static synchronized void addGame(Game game)
    {
        games.add(game);
        game.state = STATE_PENDING;
    }

    public Game(int gameID, String name, ClientConnection initiator)
    {
        this.name = name;
        this.gameID = gameID;
        this.client1 = initiator;
        this.user1ID = initiator.getUserID();
    }

    public short getState()
    {
        return state;
    }
}

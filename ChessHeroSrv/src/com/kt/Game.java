package com.kt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

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
    public static short STATE_STARTING  = 2; // The game is about to be started
    public static short STATE_STARTED   = 3; // The game is being played
    public static short STATE_FINISHED  = 4; // The game has finished

    private static HashMap<Integer, Game> games = new HashMap<Integer, Game>();
    private static Database database = new Database();

    private int gameID;

    private String name;

    private int user1ID;
    private int user2ID;

    private ClientConnection client1;
    private ClientConnection client2;

    private short state = STATE_INIT;

    public static void addGame(Game game)
    {
        synchronized (games)
        {
            games.put(game.gameID, game);
        }
    }

    public static void removeGame(int gameID)
    {
        synchronized (games)
        {
            games.remove(gameID);
        }
    }

    public static Game getGame(int gameID)
    {
        synchronized (games)
        {
            return games.get(gameID);
        }
    }

    public static String generateChatToken(int gameID, int userID, String gameName) throws NoSuchAlgorithmException
    {
        String cat1 = gameName + gameID + userID;

        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte tokenData[] = digest.digest(cat1.getBytes());

        Formatter formatter = new Formatter();

        for (byte b : tokenData)
        {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public Game(int gameID, String name, ClientConnection initiator)
    {
        this.name = name;
        this.gameID = gameID;
        this.client1 = initiator;
        this.user1ID = initiator.getUserID();
    }

    public void join(ClientConnection secondPlayer)
    {
        client2 = secondPlayer;
        user2ID = secondPlayer.getUserID();
    }

    public int getGameID()
    {
        return gameID;
    }

    public String getName()
    {
        return name;
    }

    public short getState()
    {
        return state;
    }

    public void setState(short state)
    {
        this.state = state;
    }

    public ClientConnection getOtherClient(ClientConnection not)
    {
        if (client1 == not)
        {
            return client2;
        }

        return client1;
    }

    public int getOtherUserID(int not)
    {
        if (user1ID == not)
        {
            return user2ID;
        }

        return user1ID;
    }
}

package com.kt.game;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    public static final short STATE_INIT      = 0; // Just initialized, not yet in the list
    public static final short STATE_PENDING   = 1; // Inside the list with created games
    public static final short STATE_STARTED   = 2; // The game is being played
    public static final short STATE_FINISHED  = 3; // The game has finished

    public static final int MIN_NAME_LENGTH = 3;
    public static final int MAX_NAME_LENGTH = 256;

    public static boolean isGameNameValid(String name)
    {
        int length = name.trim().length();
        return (length >= MIN_NAME_LENGTH && length <= MAX_NAME_LENGTH);
    }

	private static HashMap<Integer, Game> games = new HashMap<Integer, Game>();

	public static void addGame(Game game)
	{
		synchronized (games)
		{
			games.put(game.id, game);
		}
	}

	public static Game removeGame(int gameID)
	{
		synchronized(games)
		{
			Game game = games.get(gameID);
			games.remove(gameID);
			return game;
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

    private int id;

    private String name;

    protected Player player1;
    protected Player player2;

    private short state = STATE_INIT;

    protected GameController controller;

    public Game(int gameID, String name)
    {
        this.name = name;
        this.id = gameID;
    }

    public int getID()
    {
        return id;
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

    public GameController getController()
    {
        return controller;
    }

    public Player getPlayer1()
    {
        return player1;
    }

    public Player getPlayer2()
    {
        return player2;
    }
}

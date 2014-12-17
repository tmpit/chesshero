package com.kt.game;

import com.kt.game.ChessPiece;

import java.util.ArrayList;

/**
 * The Player class represents a user with user id, username and game-specific data such as the color of the player,
 * their chess piece set, which game they are in and how long they have played in there
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class Player
{
	private int userID;
	private String name;

	protected Game game;
	protected Color color = Color.NONE;
	protected long millisPlayed = 0;

	protected ChessPieceSet chessPieceSet;

	/**
	 * Initializes a newly created {@code Player} instance with a user id and a username
	 * @param userID An {@code int}
	 * @param name A {@code String}
	 */
	public Player(int userID, String name)
	{
		this.userID = userID;
		this.name = name;
	}

	/**
	 * Compares this {@code Player} with the specified {@code Player} and returns true if they are the same user
	 * @param player A {@code Player} to compare against
	 * @return true if this and the specified {@code Player} are the same user
	 */
	public boolean equals(Player player)
	{
		return userID == player.userID;
	}

	/**
	 * Gets the user id of the player
	 * @return An {@code int}
	 */
	public int getUserID()
	{
		return userID;
	}

	/**
	 * Gets the username of the player
	 * @return A {@code String}
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the game the player is playing in
	 * @return A {@code Game}
	 */
	public Game getGame()
	{
		return game;
	}

	/**
	 * Gets the color the player has picked for the game they are in
	 * @return A {@code Color}
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Gets the number of milliseconds that this player has played in the game they are in
	 * @return A {@code long}
	 */
	public long getMillisPlayed()
	{
		return millisPlayed;
	}

	/**
	 * Gets the opponent of the player within the game they are in (if any)
	 * @return A {@code Player} representing the opponent, or null if this player is not in a game
	 */
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

	/**
	 * Gets the chess piece set of the player
	 * @return A {@code ChessPieceSet}
	 */
	public ChessPieceSet getChessPieceSet()
	{
		return chessPieceSet;
	}

	/**
	 * Attempts to take a piece from the player
	 * @param piece The {@code ChessPiece} to take
	 * @return true if the chess piece was taken successfully, false if the chess piece was not active
	 * in the first place
	 */
	protected boolean takePiece(ChessPiece piece)
	{
		return chessPieceSet.take(piece);
	}

	/**
	 * Adds a piece to the player's active pieces
	 * @param piece A {@code ChessPiece} to add
	 */
	protected void addPiece(ChessPiece piece)
	{
		chessPieceSet.add(piece);
	}

	@Override
	public String toString()
	{
		return "<Player :: userid: " + userID + ", name: " + name + ", color: " + (color == Color.WHITE ? "white" : "black") + ">";
	}
}


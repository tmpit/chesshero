package com.kt.game;

import com.kt.game.ChessPiece;

import java.util.ArrayList;

/**
 * Created by Toshko on 12/9/13.
 */
public class Player
{
	private int userID;
	private String name;

	private Game game;
	private Color color = Color.NONE;
	protected long lastMoveTimestampMillis;

	private ChessPieceSet chessPieceSet;

	public Player(int userID, String name)
	{
		this.userID = userID;
		this.name = name;
	}

	public boolean equals(Player player)
	{
		return userID == player.userID;
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
		boolean player1;

		if (!(player1 = null == game.player1) && game.player2 != null)
		{	// Both players are in the game
			return false;
		}

		if (player1)
		{
			game.player1 = this;
		}
		else
		{
			game.player2 = this;
		}

		ChessPieceSet set = (color == Color.WHITE ? game.whiteChessPieceSet : game.blackChessPieceSet);

		this.game = game;
		this.color = color;
		this.chessPieceSet = set;

		ArrayList<ChessPiece> pieces = set.getActivePieces();
		for (ChessPiece piece : pieces)
		{
			piece.setOwner(this);
		}

		return true;
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
		chessPieceSet = null;
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

	public ChessPieceSet getChessPieceSet()
	{
		return chessPieceSet;
	}

	protected boolean takePiece(ChessPiece piece)
	{
		return chessPieceSet.take(piece);
	}

	protected void addPiece(ChessPiece piece)
	{
		chessPieceSet.add(piece);
	}

	public String toString()
	{
		return "<Player :: userid: " + userID + ", name: " + name + ", color: " + (color == Color.WHITE ? "white" : "black") + ">";
	}
}


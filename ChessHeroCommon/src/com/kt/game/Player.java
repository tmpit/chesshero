package com.kt.game;

import com.kt.game.chesspieces.ChessPiece;

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

	private ArrayList<ChessPiece> activePieces;
	private ArrayList<ChessPiece> takenPieces;

	public Player(int userID, String name)
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
		ArrayList pieces = (color == Color.WHITE ? Game.initialWhiteChessPieces(this) : Game.initialBlackChessPieces(this));
		return join(game, color, pieces, null);
	}

	public boolean join(Game game, Color color, ArrayList<ChessPiece> active, ArrayList<ChessPiece> taken)
	{
		if (null == game.player1)
		{
			game.player1 = this;
			this.game = game;
			this.color = color;
			this.activePieces = active;
			this.takenPieces = taken;
			return true;
		}

		if (null == game.player2)
		{
			game.player2 = this;
			this.game = game;
			this.color = color;
			this.activePieces = active;
			this.takenPieces = taken;
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

	public ArrayList<ChessPiece> getActivePieces()
	{
		return activePieces;
	}

	public ArrayList<ChessPiece> getTakenPieces()
	{
		return takenPieces;
	}

	public String toString()
	{
		return "<Player :: userid: " + userID + ", name: " + name + ", color: " + (color == Color.WHITE ? "white" : "black") + ">";
	}
}


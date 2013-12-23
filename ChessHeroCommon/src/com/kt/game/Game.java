package com.kt.game;

import com.kt.game.chesspieces.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Toshko on 12/23/13.
 */
public class Game
{
	public static final short STATE_INIT      = 0; // Just initialized, not yet in the list
	public static final short STATE_PENDING   = 1; // Inside the list with created games
	public static final short STATE_STARTED   = 2; // The game is being played
	public static final short STATE_FINISHED  = 3; // The game has finished

	public static final int MIN_NAME_LENGTH = 3;
	public static final int MAX_NAME_LENGTH = 256;

	public static final int BOARD_SIDE = 8;

	public static ArrayList<ChessPiece> initialWhiteChessPieces(Player owner)
	{
		return aSetOfChessPieces(owner, true);
	}

	public static ArrayList<ChessPiece> initialBlackChessPieces(Player owner)
	{
		return aSetOfChessPieces(owner, false);
	}

	private static ArrayList<ChessPiece> aSetOfChessPieces(Player owner, boolean bottom)
	{
		int pawnRow = (bottom ? 1 : 6);
		int mainRow = (bottom ? 0 : 7);

		ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>(16);

		for (int i = 0; i < 8; i++)
		{
			pieces.add(new Pawn(new Position(i, pawnRow), owner));
		}

		pieces.add(new Rook(new Position(0, mainRow), owner));
		pieces.add(new Rook(new Position(7, mainRow), owner));
		pieces.add(new Knight(new Position(1, mainRow), owner));
		pieces.add(new Knight(new Position(6, mainRow), owner));
		pieces.add(new Bishop(new Position(2, mainRow), owner));
		pieces.add(new Bishop(new Position(5, mainRow), owner));
		pieces.add(new Queen(new Position(3, mainRow), owner));
		pieces.add(new King(new Position(4, mainRow), owner));

		return pieces;
	}

	public static boolean isGameNameValid(String name)
	{
		int length = name.trim().length();
		return (length >= MIN_NAME_LENGTH && length <= MAX_NAME_LENGTH);
	}

	private short state = STATE_INIT;

	private int id;
	private String name;

	protected Player player1;
	protected Player player2;

	protected GameController controller;

	protected BoardField board[][] = new BoardField[BOARD_SIDE][BOARD_SIDE];

	protected Player turn = null;
	protected Player inCheck = null;
	protected Player winner = null;

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

	public Player getWinner()
	{
		return winner;
	}

	public void initializeBoard()
	{
		ArrayList<ChessPiece> player1Pieces = player1.getActivePieces();

		for (ChessPiece piece : player1Pieces)
		{
			Position pos = piece.getPosition();
			BoardField field = board[pos.x][pos.y];
			field.setChessPiece(piece);
		}

		ArrayList<ChessPiece> player2Pieces = player2.getActivePieces();

		for (ChessPiece piece : player2Pieces)
		{
			Position pos = piece.getPosition();
			BoardField field = board[pos.x][pos.y];
			field.setChessPiece(piece);
		}
	}
}

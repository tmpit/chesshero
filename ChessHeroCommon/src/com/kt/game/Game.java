package com.kt.game;

import com.kt.game.chesspieces.*;
import com.kt.utils.SLog;

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

	public static ChessPieceSet initialWhiteChessPieces(Player owner)
	{
		return aSetOfChessPieces(owner, Color.WHITE, true);
	}

	public static ChessPieceSet initialBlackChessPieces(Player owner)
	{
		return aSetOfChessPieces(owner, Color.BLACK, false);
	}

	private static ChessPieceSet aSetOfChessPieces(Player owner, Color color, boolean bottom)
	{
		int pawnRow = (bottom ? 1 : 6);
		int mainRow = (bottom ? 0 : 7);

		ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>(16);

		for (int i = 0; i < 8; i++)
		{
			pieces.add(new Pawn(new Position(i, pawnRow), owner, color));
		}

		pieces.add(new Rook(new Position(0, mainRow), owner, color));
		pieces.add(new Rook(new Position(7, mainRow), owner, color));
		pieces.add(new Knight(new Position(1, mainRow), owner, color));
		pieces.add(new Knight(new Position(6, mainRow), owner, color));
		pieces.add(new Bishop(new Position(2, mainRow), owner, color));
		pieces.add(new Bishop(new Position(5, mainRow), owner, color));
		pieces.add(new Queen(new Position(3, mainRow), owner, color));
		pieces.add(new King(new Position(4, mainRow), owner, color));

		return new ChessPieceSet(pieces);
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

	private BoardField board[][] = new BoardField[BOARD_SIDE][BOARD_SIDE];

	protected GameController controller;

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

	protected void initializeBoard()
	{
		Color color = Color.BLACK;

		for (int i = 0; i < BOARD_SIDE; i++)
		{
			for (int j = 0; j < BOARD_SIDE; j++)
			{
				board[i][j] = new BoardField(new Position(i, j), color);
				color = color.Opposite;
			}

			color = color.Opposite;
		}

		ArrayList<ChessPiece> player1Pieces = player1.getChessPieceSet().getActivePieces();

		for (ChessPiece piece : player1Pieces)
		{
			Position pos = piece.getPosition();
			BoardField field = board[pos.x][pos.y];
			field.setChessPiece(piece);
		}

		ArrayList<ChessPiece> player2Pieces = player2.getChessPieceSet().getActivePieces();

		for (ChessPiece piece : player2Pieces)
		{
			Position pos = piece.getPosition();
			BoardField field = board[pos.x][pos.y];
			field.setChessPiece(piece);
		}
	}

	protected BoardField[][] getBoard()
	{
		return board;
	}

	@Override
	public String toString()
	{
		String base = "<Game :: id: " + id + ", name: " + name + ", players: " + player1 + " --- " + player2 + ">";

		if (state != STATE_STARTED)
		{
			return base;
		}

		String description = "\n   ";

		for (int i = 0; i < BOARD_SIDE; i++)
		{
			description += String.format(" %c ", (char)('a' + i));
		}

		description += "\n   ------------------------\n";

		for (int i = BOARD_SIDE - 1; i >= 0; i--)
		{
			description += (i + 1) + " |";

			for (int j = 0; j < BOARD_SIDE; j++)
			{
				description += board[j][i].toString();
			}

			description += "| " + (i + 1) + '\n';
		}

		description += "   ------------------------\n   ";

		for (int i = 0; i < BOARD_SIDE; i++)
		{
			description += String.format(" %c ", (char)('a' + i));
		}

		return base + description;
	}
}

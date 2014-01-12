package com.kt.game;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Toshko on 12/23/13.
 */
public class Game
{
	public static final short STATE_INIT     	= 0; // Initialized
	public static final short STATE_PENDING   	= 1; // Waiting for second player
	public static final short STATE_ACTIVE 		= 2; // Game is being played
	public static final short STATE_FINISHED	= 3; // Game has ended
	public static final short STATE_PAUSED		= 4; // Game has been paused

	public static final int MIN_NAME_LENGTH = 3;
	public static final int MAX_NAME_LENGTH = 256;

	public static final int BOARD_SIDE = 8;

	private static ChessPieceSet aSetOfChessPieces(Color color, boolean bottom)
	{
		int pawnRow = (bottom ? 1 : 6);
		int mainRow = (bottom ? 0 : 7);

		ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>(16);

		for (int i = 0; i < 8; i++)
		{
			pieces.add(new Pawn(new Position(i, pawnRow), color));
		}

		pieces.add(new Rook(new Position(0, mainRow), color));
		pieces.add(new Rook(new Position(7, mainRow), color));
		pieces.add(new Knight(new Position(1, mainRow), color));
		pieces.add(new Knight(new Position(6, mainRow), color));
		pieces.add(new Bishop(new Position(2, mainRow), color));
		pieces.add(new Bishop(new Position(5, mainRow), color));
		pieces.add(new Queen(new Position(3, mainRow), color));
		pieces.add(new King(new Position(4, mainRow), color));

		return new ChessPieceSet(pieces);
	}

	private static ChessPieceSet test_aSetOfChessPieces(Color color, boolean bottom)
	{
		ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>(16);

		if (bottom)
		{
			pieces.add(new Pawn(new Position(0, 6), color));
//			pieces.add(new Pawn(new Position(1, 1), color));
//			pieces.add(new Pawn(new Position(2, 1), color));
//			pieces.add(new Pawn(new Position(3, 1), color));
//			pieces.add(new Pawn(new Position(4, 1), color));
//			pieces.add(new Pawn(new Position(5, 1), color));
//			pieces.add(new Pawn(new Position(6, 1), color));
//			pieces.add(new Pawn(new Position(7, 1), color));
//			pieces.add(new Rook(new Position(0, 0), color));
//			pieces.add(new Rook(new Position(7, 0), color));
//			pieces.add(new Knight(new Position(1, 0), color));
//			pieces.add(new Knight(new Position(6, 0), color));
//			pieces.add(new Bishop(new Position(2, 0), color));
//			pieces.add(new Bishop(new Position(5, 0), color));
//			pieces.add(new Queen(new Position(3, 0), color));
			pieces.add(new King(new Position(4, 0), color));
		}
		else
		{
//			pieces.add(new Pawn(new Position(0, 6), color));
//			pieces.add(new Pawn(new Position(1, 6), color));
//			pieces.add(new Pawn(new Position(2, 6), color));
//			pieces.add(new Pawn(new Position(3, 6), color));
//			pieces.add(new Pawn(new Position(4, 6), color));
//			pieces.add(new Pawn(new Position(5, 6), color));
//			pieces.add(new Pawn(new Position(6, 6), color));
//			pieces.add(new Pawn(new Position(7, 6), color));
//			pieces.add(new Rook(new Position(0, 7), color));
//			pieces.add(new Rook(new Position(7, 7), color));
//			pieces.add(new Knight(new Position(1, 7), color));
//			pieces.add(new Knight(new Position(6, 7), color));
//			pieces.add(new Bishop(new Position(2, 7), color));
//			pieces.add(new Bishop(new Position(5, 7), color));
//			pieces.add(new Queen(new Position(3, 7), color));
			pieces.add(new King(new Position(4, 7), color));
		}

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

	protected ChessPieceSet whiteChessPieceSet;
	protected ChessPieceSet blackChessPieceSet;

	protected Player player1;
	protected Player player2;

	private BoardField board[][] = new BoardField[BOARD_SIDE][BOARD_SIDE];

	protected GameController controller;

	protected Player turn = null;

	protected Pawn lastPawnRunner = null;

	protected Player inCheck = null;

	protected ArrayList<ChessPiece> attackers = new ArrayList<ChessPiece>(2);

	protected Player winner = null;
	protected boolean checkmate = false;

	protected boolean saved = false;

	public Game(int gameID, String name)
	{
		this(gameID, name, null);
	}

	public Game(int gameID, String name, byte data[])
	{
		this.name = name;
		this.id = gameID;

		if (null == data)
		{
			whiteChessPieceSet = aSetOfChessPieces(Color.WHITE, true);
			blackChessPieceSet = aSetOfChessPieces(Color.BLACK, false);
		}
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

	public boolean wasSaved()
	{
		return saved;
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

	public Player getTurn()
	{
		return turn;
	}

	public Player getWinner()
	{
		return winner;
	}

	public boolean isCheckmate()
	{
		return checkmate;
	}

	public ArrayList<ChessPiece> getAttackers()
	{
		return attackers;
	}

	protected void initializeBoard()
	{
		// Create board
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

		// Assign pieces to their positions
		ArrayList<ChessPiece> whitePieces = whiteChessPieceSet.getActivePieces();

		for (ChessPiece piece : whitePieces)
		{
			Position pos = piece.getPosition();
			BoardField field = board[pos.x][pos.y];
			field.setChessPiece(piece);
		}

		ArrayList<ChessPiece> blackPieces = blackChessPieceSet.getActivePieces();

		for (ChessPiece piece : blackPieces)
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

		if (state != STATE_ACTIVE)
		{
			return base;
		}

		String description = "\n   ";

		for (int i = 0; i < BOARD_SIDE; i++)
		{
			description += String.format(" %c ", (char)('a' + i));
		}

		description += "\n  +------------------------+\n";

		for (int i = BOARD_SIDE - 1; i >= 0; i--)
		{
			description += (i + 1) + " |";

			for (int j = 0; j < BOARD_SIDE; j++)
			{
				description += board[j][i].toString();
			}

			description += "| " + (i + 1) + '\n';
		}

		description += "  +------------------------+\n   ";

		for (int i = 0; i < BOARD_SIDE; i++)
		{
			description += String.format(" %c ", (char)('a' + i));
		}

		return base + description;
	}

	public byte[] toData()
	{
		ArrayList<ChessPiece> player1Pieces = player1.getChessPieceSet().getActivePieces();
		ArrayList<ChessPiece> player2Pieces = player2.getChessPieceSet().getActivePieces();

		int maxSize = 64; // The maximum possible size of the serialized game
		ByteBuffer buffer = ByteBuffer.allocate(maxSize);

		for (ChessPiece piece : player1Pieces)
		{
			buffer.put(piece.toData());
		}
		for (ChessPiece piece : player2Pieces)
		{
			buffer.put(piece.toData());
		}

		byte bufferData[] = buffer.array();
		int bufferOffset = buffer.arrayOffset();

		if (bufferOffset == maxSize)
		{
			return bufferData;
		}

		byte trimmed[] = new byte[bufferOffset];
		System.arraycopy(bufferData, 0, trimmed, 0, bufferOffset);

		return trimmed;
	}
}

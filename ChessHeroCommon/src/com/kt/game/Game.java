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

		if (null == data || !parseChessPieceData(data))
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
		ArrayList<ChessPiece> whitePieces = whiteChessPieceSet.getActivePieces();
		ArrayList<ChessPiece> blackPieces = blackChessPieceSet.getActivePieces();

		// A serialized game consists of all active pieces on the board
		// Each piece takes 2 bytes so the maximum number of bytes a game can be represented by is 64 bytes
		int maxSize = 64;
		ByteBuffer buffer = ByteBuffer.allocate(maxSize);

		for (ChessPiece piece : whitePieces)
		{
			buffer.put(piece.toData());
		}
		for (ChessPiece piece : blackPieces)
		{
			buffer.put(piece.toData());
		}

		byte bufferData[] = buffer.array();
		int bufferOffset = buffer.position();

		if (bufferOffset == maxSize)
		{
			return bufferData;
		}

		byte trimmed[] = new byte[bufferOffset];
		System.arraycopy(bufferData, 0, trimmed, 0, bufferOffset);

		return trimmed;
	}

	private boolean parseChessPieceData(byte data[])
	{
		int dataLength = data.length;

		if (0 == dataLength || (dataLength % 2) != 0)
		{	// No data or data is not divisible by 2 (each chess piece takes 2 bytes) - invalid format
			return false;
		}

		ArrayList<ChessPiece> whiteActive = new ArrayList<ChessPiece>();
		ArrayList<ChessPiece> blackActive = new ArrayList<ChessPiece>();
		// 8 pawns, 2 rooks, 2 knights, 2 bishops, 1 queen, 1 king - indexes correspond to ChessPiece.Tags
		int whiteMax[] = new int[]{8, 2, 2, 2, 1, 1};
		int blackMax[] = new int[]{8, 2, 2, 2, 1, 1};
		int dataLenght = data.length;

		// Initialize active pieces
		for (int i = 0; i < dataLenght; i += 2)
		{	// Each chess piece is 2 bytes
			ChessPiece piece = ChessPiece.chessPieceFromData(new byte[]{data[i], data[i + 1]});
			if (null == piece)
			{	// Invalid format
				return false;
			}

			if (Color.WHITE == piece.getColor())
			{
				whiteActive.add(piece);
				whiteMax[piece.getTag()]--;
			}
			else
			{
				blackActive.add(piece);
				blackMax[piece.getTag()]--;
			}
		}

		// Check if kings are present
		if (whiteMax[ChessPiece.Tag.KING] != 0 || blackMax[ChessPiece.Tag.KING] != 0)
		{	// One or more kings are missing
			return false;
		}

		// Initialize taken pieces
		ArrayList<ChessPiece> whiteTaken = new ArrayList<ChessPiece>();
		ArrayList<ChessPiece> blackTaken = new ArrayList<ChessPiece>();

		// Iterate only up to the queen, kings cannot be taken and we know they are both present
		for (int tag = 0; tag < 5; tag++)
		{
			int whiteLeft = whiteMax[tag];

			if (whiteLeft < 0)
			{	// There are more active white pieces of this type than there should be
				return false;
			}

			while ((whiteLeft--) != 0)
			{
				whiteTaken.add(ChessPiece.aChessPiece(tag, Position.ZERO, Color.WHITE));
			}

			int blackLeft = blackMax[tag];

			if (blackLeft < 0)
			{	// There are more active black pieces of this type than there should be
				return false;
			}

			while ((blackLeft--) != 0)
			{
				blackTaken.add(ChessPiece.aChessPiece(tag, Position.ZERO, Color.BLACK));
			}
		}

		whiteChessPieceSet = new ChessPieceSet(whiteActive, whiteTaken);
		blackChessPieceSet = new ChessPieceSet(blackActive, blackTaken);

		return true;
	}
}

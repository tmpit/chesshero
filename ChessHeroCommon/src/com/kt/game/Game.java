package com.kt.game;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * The Game class represents the state of a chess game and groups all game-related components such as players
 * and chess board
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class Game
{
	/**
	 * The {@code Game} instance has just been initialized
	 */
	public static final short STATE_INIT     	= 0;

	/**
	 * The game has just been created and the player inside it is waiting for an opponent to join
	 */
	public static final short STATE_PENDING   	= 1;

	/**
	 * The game is being played
	 */
	public static final short STATE_ACTIVE 		= 2;

	/**
	 * The game has ended
	 */
	public static final short STATE_FINISHED	= 3;

	/**
	 * The game has been paused. This can only happen during the save game routine
	 */
	public static final short STATE_PAUSED		= 4;

	/**
	 * This game is loaded from a saved state and there is only one player inside it
	 * waiting for their opponent to join and resume the game
	 */
	public static final short STATE_WAITING		= 5;

	public static final int MIN_NAME_LENGTH = 3;
	public static final int MAX_NAME_LENGTH = 256;

	public static final int MIN_TIMEOUT = 3; // In minutes
	public static final int MAX_TIMEOUT = 30;
	public static final int NO_TIMEOUT = 0;
	public static final int DEFAULT_TIMEOUT = NO_TIMEOUT;

	public static final int BOARD_SIDE = 8;

	/**
	 * Creates and returns the initial chess piece set with the specified color and initial positions
	 * either on the top or the bottom of the game board
	 * @param color A {@code Color}
	 * @param bottom true if the chess pieces should be positioned at the bottom, false for top
	 * @return A {@code ChessPieceSet}
	 */
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

	/**
	 * Validates a name for a game
	 * @param name The {@code String} to validate
	 * @return true if the name is a valid game name, false if not
	 */
	public static boolean isGameNameValid(String name)
	{
		int length = name.trim().length();
		return (length >= MIN_NAME_LENGTH && length <= MAX_NAME_LENGTH);
	}

	private short state = STATE_INIT;

	private int id;
	private String name;
	private int timeout;
	private GameClock clock = null;

	protected ChessPieceSet whiteChessPieceSet;
	protected ChessPieceSet blackChessPieceSet;

	protected Player player1;
	protected Player player2;
    private Player whitePlayer;
    private Player blackPlayer;

	private BoardField board[][] = new BoardField[BOARD_SIDE][BOARD_SIDE];

	protected GameController controller;

	protected Player turn = null;

	protected Pawn lastPawnRunner = null;

	protected Player inCheck = null;

	protected ArrayList<ChessPiece> attackers = new ArrayList<ChessPiece>(2);

	protected Player winner = null;
	protected boolean checkmate = false;

	protected boolean saved = false;

	/**
	 * Initializes a newly created {@code Game} instance with a game id, name and timeout
	 * @param gameID An {@code int}
	 * @param name A {@code String}
	 * @param timeout An {@code int}
	 */
	public Game(int gameID, String name, int timeout)
	{
		this(gameID, name, timeout, null);
	}

	/**
	 * Initializes a newly created {@code Game} instance with a game id, name, timeout and loads the state
	 * of the chess board from {@code byte} array. If parsing of the data fails, the game board is initialized
	 * with the initial chess pieces and positions as if this is a new game
	 * @param gameID An {@code int}
	 * @param name A {@code String}
	 * @param timeout An {@code int}
	 * @param data A {@code byte} array with the chess board data
	 */
	public Game(int gameID, String name, int timeout, byte data[])
	{
		this.name = name;
		this.id = gameID;
		this.timeout = timeout;

		if (null == data || !parseChessPieceData(data))
		{
			whiteChessPieceSet = aSetOfChessPieces(Color.WHITE, true);
			blackChessPieceSet = aSetOfChessPieces(Color.BLACK, false);
		}

		if (timeout != NO_TIMEOUT)
		{
			clock = new GameClock(this);
		}
	}

	/**
	 * Gets the id of the game
	 * @return An {@code int}
	 */
	public int getID()
	{
		return id;
	}

	/**
	 * Gets the name of the game
	 * @return A {@code String}
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the timeout of the game
	 * @return An {@code int}
	 */
	public int getTimeout()
	{
		return timeout;
	}

	/**
	 * Gets the game clock of the game
	 * @return A {@code GameClock}
	 */
	public GameClock getClock()
	{
		return clock;
	}

	/**
	 * Gets the state of the game
	 * @return A {@code short} that is one of the state constants for the game
	 */
	public short getState()
	{
		return state;
	}

	/**
	 * Sets the state of the game
	 * @param state A {@code short} that is one of the state constants for the game
	 */
	public void setState(short state)
	{
		this.state = state;
	}

	/**
	 * True if the game has been saved, false if not. Used to determine how to finalize the game when closing
	 * after saving it
	 * @return True if the game has been saved, false if not
	 */
	public boolean wasSaved()
	{
		return saved;
	}

	/**
	 * Gets the game controller
	 * @return A {@code GameController}
	 */
	public GameController getController()
	{
		return controller;
	}

	/**
	 * Gets the first player to join the game
	 * @return A {@code Player}
	 */
	public Player getPlayer1()
	{
		return player1;
	}

	/**
	 * Gets the second player to join the game
	 * @return A {@code Player}
	 */
	public Player getPlayer2()
	{
		return player2;
	}

    public Player getWhitePlayer()
    {
        if (null == player1 || null == player2)
        {
            return null;
        }

        if (null == whitePlayer)
        {
            whitePlayer = player1.getColor().equals(Color.WHITE) ? player1 : player2;
        }

        return whitePlayer;
    }

    public Player getBlackPlayer()
    {
        if (null == player1 || null == player2)
        {
            return null;
        }

        if (null == blackPlayer)
        {
            blackPlayer = player1.getColor().equals(Color.BLACK) ? player1 : player2;
        }

        return blackPlayer;
    }

	/**
	 * Gets the player whose turn it is
	 * @return A {@code Player}
	 */
	public Player getTurn()
	{
		return turn;
	}

	/**
	 * Gets whoever has won the game
	 * @return A {@code Player}
	 */
	public Player getWinner()
	{
		return winner;
	}

    public BoardField getField(Position position)
    {
        return this.board[position.getX()][position.getY()];
    }

	/**
	 * True if the game has ended due to a checkmate, false if not. Used to determine how to finalize a game after
	 * it has finished
	 * @return True if the game has ended due to a checkmate, false if not
	 */
	public boolean isCheckmate()
	{
		return checkmate;
	}

	/**
	 * Gets the attackers of the king of the player whose turn it is now
	 * @return An {@code ArrayList} with {@code ChessPiece} instances or null if the king is not being attacked
	 */
	public ArrayList<ChessPiece> getAttackers()
	{
		return attackers;
	}

	/**
	 * Creates the chess board, assigns positions, colors and chess pieces to each field
	 */
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

	/**
	 * Gets the chess board
	 * @return A two-dimensional array of {@code BoardField} instances
	 */
	public BoardField[][] getBoard()
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

	/**
	 * Serializes all active chess pieces and returns a {@code byte} array
	 * @return A {@code byte} array containing the chess board data
	 */
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

	/**
	 * Attempts to parse the specified data as serialized chess piece data. On success it creates
	 * black and white chess piece sets and assigns them to the {@code whiteChessPieceSet} and
	 * {@code blackChessPieceSet} fields
	 * @param data The data to parse in the form of a {@code byte} array
	 * @return True if the data has been parsed successfully, false if not
	 */
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

		// Initialize active pieces
		for (int i = 0; i < dataLength; i += 2)
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

		// Check kings
		if (whiteMax[ChessPiece.Tag.KING] != 0 || blackMax[ChessPiece.Tag.KING] != 0)
		{	// One or more kings are missing or someone has more than one king
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
				whiteTaken.add(ChessPiece.aChessPiece(tag, Position.ZERO.clone(), Color.WHITE));
			}

			int blackLeft = blackMax[tag];

			if (blackLeft < 0)
			{	// There are more active black pieces of this type than there should be
				return false;
			}

			while ((blackLeft--) != 0)
			{
				blackTaken.add(ChessPiece.aChessPiece(tag, Position.ZERO.clone(), Color.BLACK));
			}
		}

		whiteChessPieceSet = new ChessPieceSet(whiteActive, whiteTaken);
		blackChessPieceSet = new ChessPieceSet(blackActive, blackTaken);

		return true;
	}
}

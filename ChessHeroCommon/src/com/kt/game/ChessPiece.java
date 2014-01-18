package com.kt.game;

/**
 * The ChessPiece class is the base class for all chess pieces in the game of chess
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public abstract class ChessPiece
{
	/**
	 * The Tag class groups unique type identifiers for every type of chess piece
	 */
	public class Tag
	{
		public static final byte PAWN = 0;
		public static final byte ROOK = 1;
		public static final byte KNIGHT = 2;
		public static final byte BISHOP = 3;
		public static final byte QUEEN = 4;
		public static final byte KING = 5;
	}

	/**
	 * Parses a chess piece from a byte array
	 * @param data A {@code byte} array
	 * @return A newly created {@code ChessPiece} instance or null if the data could not be parsed
	 * due to invalid format
	 */
	public static ChessPiece chessPieceFromData(byte data[])
	{
		if (data.length != 2)
		{
			return null;
		}

		byte meta = data[0]; // First byte contains the piece tag, color and whether it has moved
		byte tag = (byte)(meta & 0x7); // Take the first 3 bits

		boolean white = (meta & 0x8) != 0; // 4th bit is 1 for white 0 for black
		boolean moved = (meta & 0x10) != 0; // 5th bit signifies if the piece has moved

		Position position = Position.positionFromData(data[1]); // Second byte contains position

		if (null == position)
		{
			return null;
		}

		Color color = (white ? Color.WHITE : Color.BLACK);
		ChessPiece piece = aChessPiece(tag, position, color);

		if (piece != null)
		{
			piece.moved = moved;
		}

		return piece;
	}

	/**
	 * Creates a {@code ChessPiece} instance with the specified tag, position and color
	 * @param tag A constant from the {@code Tag} class
	 * @param position A {@code Position}
	 * @param color A {@code Color}
	 * @return A newly created {@code ChessPiece} instance or null if the {@code tag} value is invalid
	 */
	public static ChessPiece aChessPiece(int tag, Position position, Color color)
	{
		switch (tag)
		{
			case Tag.PAWN: 		return new Pawn(position, color);
			case Tag.ROOK: 		return new Rook(position, color);
			case Tag.KNIGHT: 	return new Knight(position, color);
			case Tag.BISHOP: 	return new Bishop(position, color);
			case Tag.QUEEN: 	return new Queen(position, color);
			case Tag.KING: 		return new King(position, color);
			default: 			return null;
		}
	}

	protected Position position;
	protected Color color;
	private Player owner = null;
	private byte tag;
	private MovementSet movementSet;

	private boolean moved = false;

	/**
	 * Initializes a newly created {@code ChessPiece} instance with a tag, position, color and movement set
	 * @param tag A constant from the {@code Tag} class
	 * @param position A {@code Position}
	 * @param color A {@code Color}
	 * @param movementSet A {@code MovementSet}
	 */
	public ChessPiece(byte tag, Position position, Color color, MovementSet movementSet)
	{
		this.tag = tag;
		this.position = position;
		this.color = color;
		this.movementSet = movementSet;
	}

	/**
	 * Gets the position of this chess piece on the chess board
	 * @return A {@code Position}
	 */
	public Position getPosition()
	{
		return position;
	}

	/**
	 * Sets the position of this chess piece on the chess board
	 * @param newPos A {@code Position}
	 */
	void setPosition(Position newPos)
	{
		position = newPos;
		moved = true;
	}

	/**
	 * Gets the color of this chess piece
	 * @return A {@code Color}
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Gets the owner of this chess piece
	 * @return A {@code Player}
	 */
	public Player getOwner()
	{
		return owner;
	}

	/**
	 * Sets the owner of this chess piece
	 * @param owner A {@code Player}
	 */
	void setOwner(Player owner)
	{
		this.owner = owner;
	}

	/**
	 * Gets the movement set of this chess piece
	 * @return A {@code MovementSet}
	 */
	public MovementSet getMovementSet()
	{
		return movementSet;
	}

	/**
	 * Gets the tag of this chess piece
	 * @return A constant from the {@code Tag} class
	 */
	public byte getTag()
	{
		return tag;
	}

	/**
	 * Returns true if this chess piece has moved within the game board it is in, false otherwise
	 * @return true if this chess piece has moved, false otherwise
	 */
	public boolean hasMoved()
	{
		return moved;
	}

	/**
	 * Returns true if this chess piece can move from its current position to the specified position
	 * considering whether it would be taking another chess piece there or not. In other words this method
	 * returns true if this chess piece can move in the fashion described by its current position and
	 * destination position. This method is abstracted from the game board, meaning that the check
	 * is not performed in the context of a game board, thus the chess piece will return true if it can
	 * move in the specified fashion even if the destination position is outside of the game board.
	 * It is up to the calling code to perform additional validation of the destination position
	 * @param pos A {@code Position} instance representing the destination position
	 * @param take Pass true if this chess piece would be taking another one with this move
	 * @return true if this chess piece can move in the fashion described by its current position
	 * and the destination position, false if not
	 */
	protected abstract boolean isMoveValid(Position pos, boolean take);

	/**
	 * Serializes a {@code ChessPiece} object to a {@code byte} array
	 * @return The resulting {@code byte} array. The length of the array is always 2
	 */
	public byte[] toData()
	{
		byte meta = tag;

		if (Color.WHITE == color)
		{
			meta |= 1 << 3;
		}
		if (moved)
		{
			meta |= 1 << 4;
		}

		return new byte[] {meta, position.toData()};
	}
}

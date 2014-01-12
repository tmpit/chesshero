package com.kt.game;

/**
 * Created by Toshko on 12/23/13.
 */
public abstract class ChessPiece
{
	public class Tag
	{
		public static final byte PAWN = 0;
		public static final byte ROOK = 1;
		public static final byte KNIGHT = 2;
		public static final byte BISHOP = 3;
		public static final byte QUEEN = 4;
		public static final byte KING = 5;
	}

	protected Position position;
	protected Color color;
	private Player owner = null;
	private byte tag;
	private MovementSet movementSet;

	private boolean moved = false;

	public ChessPiece(byte tag, Position position, Color color, MovementSet movementSet)
	{
		this.tag = tag;
		this.position = position;
		this.color = color;
		this.movementSet = movementSet;
	}

	public Position getPosition()
	{
		return position;
	}

	void setPosition(Position newPos)
	{
		position = newPos;
		moved = true;
	}

	public Color getColor()
	{
		return color;
	}

	public Player getOwner()
	{
		return owner;
	}

	void setOwner(Player owner)
	{
		this.owner = owner;
	}

	public MovementSet getMovementSet()
	{
		return movementSet;
	}

	public byte getTag()
	{
		return tag;
	}

	public boolean hasMoved()
	{
		return moved;
	}

	protected abstract boolean isMoveValid(Position pos, boolean take);

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

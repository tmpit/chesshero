package com.kt.game.chesspieces;

import com.kt.game.Color;
import com.kt.game.MovementSet;
import com.kt.game.Player;
import com.kt.game.Position;
import com.kt.utils.SLog;

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
	protected Player owner;
	protected byte tag;
	protected MovementSet movementSet;

	private boolean moved = false;

	public ChessPiece(byte tag, Position position, Player owner, Color color, MovementSet movementSet)
	{
		this.tag = tag;
		this.position = position;
		this.owner = owner;
		this.color = color;
		this.movementSet = movementSet;
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position newPos)
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

	public abstract boolean isMoveValid(Position pos, boolean take);

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

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
	protected Position position;
	protected Color color;
	protected Player owner;

	protected MovementSet movementSet;

	public ChessPiece(Position position, Player owner, Color color, MovementSet movementSet)
	{
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

	public abstract boolean isMoveValid(Position pos, boolean take);
}

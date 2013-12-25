package com.kt.game.chesspieces;

import com.kt.game.Color;
import com.kt.game.MovementSet;
import com.kt.game.Player;
import com.kt.game.Position;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Toshko on 12/23/13.
 */
public class King extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.LEFT, MovementSet.DOWN, MovementSet.RIGHT,
			MovementSet.UP_LEFT, MovementSet.UP_RIGHT, MovementSet.DOWN_LEFT, MovementSet.DOWN_RIGHT
	)), true);

	private boolean moved = false;

	public King(Position position, Player owner, Color color)
	{
		super(position, owner, color, set);
	}

	@Override
	public void setPosition(Position newPos)
	{
		super.setPosition(newPos);
		moved = true;
	}

	public boolean hasMoved()
	{
		return moved;
	}

	@Override
	public boolean isMoveValid(Position pos)
	{
		return Math.abs(position.getX() - pos.getX()) < 2 && Math.abs(position.getY() - pos.getY()) < 2; // No more than one step in any direction
	}

	@Override
	public String toString()
	{
		return (Color.WHITE == color ? "K" : "k");
	}

}

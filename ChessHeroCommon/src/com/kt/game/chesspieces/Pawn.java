package com.kt.game.chesspieces;

import com.kt.game.Color;
import com.kt.game.MovementSet;
import com.kt.game.Player;
import com.kt.game.Position;
import com.kt.utils.SLog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Toshko on 12/23/13.
 */
public class Pawn extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.UP2, MovementSet.UP_LEFT, MovementSet.UP_RIGHT
	)), true);

	private boolean moved = false;

	public Pawn(Position position, Player owner, Color color)
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
		int vertical = pos.getY() - position.getY();
		int horizontal = Math.abs(pos.getX() - position.getX());

		if (Color.BLACK == color)
		{	// Black is positioned at the top, flip the sign
			vertical *= -1;
		}

		return (0 == horizontal && 2 == vertical) || (1 == vertical && (0 == horizontal || 1 == horizontal));
	}

	@Override
	public String toString()
	{
		return (Color.WHITE == color ? "P" : "p");
	}
}

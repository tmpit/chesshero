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
public class Pawn extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.UP2, MovementSet.UP_LEFT, MovementSet.UP_RIGHT
	)), true);

	protected boolean hasMoved = false;

	public Pawn(Position position, Player owner)
	{
		super(position, owner, set);
	}

	@Override
	public boolean isMoveValid(Position pos)
	{
		int vertical = pos.getX() - position.getX();
		int horizontal = Math.abs(pos.getY() - position.getY());

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

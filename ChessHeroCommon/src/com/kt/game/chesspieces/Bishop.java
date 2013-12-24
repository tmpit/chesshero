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
public class Bishop extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP_LEFT, MovementSet.UP_RIGHT, MovementSet.DOWN_LEFT, MovementSet.DOWN_RIGHT
	)));

	public Bishop(Position position, Player owner)
	{
		super(position, owner, set);
	}

	@Override
	public boolean isMoveValid(Position pos)
	{
		return position.isDiagonalTo(pos);
	}

	@Override
	public String toString()
	{
		return (Color.WHITE == color ? "B" : "b");
	}
}

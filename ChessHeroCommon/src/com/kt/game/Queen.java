package com.kt.game;

import com.kt.game.Color;
import com.kt.game.MovementSet;
import com.kt.game.Player;
import com.kt.game.Position;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Toshko on 12/23/13.
 */
public class Queen extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.LEFT, MovementSet.DOWN, MovementSet.RIGHT,
			MovementSet.UP_LEFT, MovementSet.UP_RIGHT, MovementSet.DOWN_LEFT, MovementSet.DOWN_RIGHT
	)));

	public Queen(Position position, Player owner, Color color)
	{
		super(Tag.QUEEN, position, owner, color, set);
	}

	@Override
	public boolean isMoveValid(Position pos, boolean take)
	{
		return position.isHorizontalOrVerticalTo(pos) || position.isDiagonalTo(pos);
	}

	@Override
	public String toString()
	{
		return (Color.WHITE == color ? "Q" : "q");
	}
}

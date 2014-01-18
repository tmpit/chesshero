package com.kt.game;

import com.kt.game.Color;
import com.kt.game.MovementSet;
import com.kt.game.Player;
import com.kt.game.Position;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class Queen extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.LEFT, MovementSet.DOWN, MovementSet.RIGHT,
			MovementSet.UP_LEFT, MovementSet.UP_RIGHT, MovementSet.DOWN_LEFT, MovementSet.DOWN_RIGHT
	)));

	/**
	 * Initializes a newly created {@code Queen} object with a position and color
	 * @param position A {@code Position}
	 * @param color A {@code Color}
	 */
	public Queen(Position position, Color color)
	{
		super(Tag.QUEEN, position, color, set);
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

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
public class Rook extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.LEFT, MovementSet.DOWN, MovementSet.RIGHT
	)));

	/**
	 * Initializes a newly created {@code Rook} object with a position and color
	 * @param position A {@code Position}
	 * @param color A {@code Color}
	 */
	public Rook(Position position, Color color)
	{
		super(Tag.ROOK, position, color, set);
	}

	@Override
	public boolean isMoveValid(Position pos, boolean take)
	{
		return position.isHorizontalOrVerticalTo(pos);
	}

	@Override
	public String toString()
	{
		return (Color.WHITE == color ? "R" : "r");
	}
}

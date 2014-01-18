package com.kt.game;

import com.kt.game.Color;
import com.kt.game.MovementSet;
import com.kt.game.Player;
import com.kt.game.Position;
import com.kt.utils.SLog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class Pawn extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.UP2, MovementSet.UP_LEFT, MovementSet.UP_RIGHT
	)), true);

	/**
	 * Initializes a newly created {@code Pawn} object with a position and color
	 * @param position A {@code Position}
	 * @param color A {@code Color}
	 */
	public Pawn(Position position, Color color)
	{
		super(Tag.PAWN, position, color, set);
	}

	@Override
	public boolean isMoveValid(Position pos, boolean take)
	{
		int vertical = pos.getY() - position.getY();
		int horizontal = Math.abs(pos.getX() - position.getX());

		if (Color.BLACK == color)
		{	// Black is positioned at the top, flip the sign
			vertical *= -1;
		}

		if (take)
		{
			return 1 == horizontal && 1 == vertical;
		}

		return 0 == horizontal && (1 == vertical || 2 == vertical);
	}

	@Override
	public String toString()
	{
		return (Color.WHITE == color ? "P" : "p");
	}
}

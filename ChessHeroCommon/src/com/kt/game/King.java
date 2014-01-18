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
public class King extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.UP, MovementSet.LEFT, MovementSet.DOWN, MovementSet.RIGHT,
			MovementSet.UP_LEFT, MovementSet.UP_RIGHT, MovementSet.DOWN_LEFT, MovementSet.DOWN_RIGHT
	)), true);

	/**
	 * Initializes a newly created {@code King} object with a position and color
	 * @param position A {@code Position}
	 * @param color A {@code Color}
	 */
	public King(Position position, Color color)
	{
		super(Tag.KING, position, color, set);
	}

	@Override
	public boolean isMoveValid(Position pos, boolean take)
	{
		return Math.abs(position.getX() - pos.getX()) < 2 && Math.abs(position.getY() - pos.getY()) < 2; // No more than one step in any direction
	}

	@Override
	public String toString()
	{
		return (Color.WHITE == color ? "K" : "k");
	}

}

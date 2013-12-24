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
public class Knight extends ChessPiece
{
	static MovementSet set = new MovementSet(new ArrayList<Position>(Arrays.asList(
			MovementSet.LEFT2_UP,
			MovementSet.LEFT2_DOWN,
			MovementSet.RIGHT2_UP,
			MovementSet.RIGHT2_DOWN,
			MovementSet.UP2_LEFT,
			MovementSet.DOWN2_LEFT,
			MovementSet.UP2_RIGHT,
			MovementSet.DOWN2_RIGHT
	)));

	public Knight(Position position, Player owner, Color color)
	{
		super(position, owner, color, set);
	}

	@Override
	public boolean isMoveValid(Position pos)
	{
		int myX = position.getX();
		int myY = position.getY();
		int x = pos.getX();
		int y = pos.getY();
		pos.subtract(position); // Subtract current position to get relative offset

		if (x > myX && y > myY)
		{
			return pos.equals(MovementSet.RIGHT2_UP) || pos.equals(MovementSet.UP2_RIGHT);
		}
		if (x < myX && y < myY)
		{
			return pos.equals(MovementSet.LEFT2_DOWN) || pos.equals(MovementSet.DOWN2_LEFT);
		}
		if (x > myX && y < myY)
		{
			return pos.equals(MovementSet.RIGHT2_DOWN) || pos.equals(MovementSet.DOWN2_RIGHT);
		}

		return pos.equals(MovementSet.UP2_LEFT) || pos.equals(MovementSet.LEFT2_UP);
	}

	@Override
	public String toString()
	{
		return (Color.WHITE == color ? "N" : "n");
	}
}

package com.kt.game;

import java.util.Collection;

/**
 * Created by Toshko on 12/23/13.
 */
public class MovementSet
{
	// Vertical and horizontal
	public static final Position UP = new Position(0, 1);
	public static final Position UP2 = new Position(0, 2);
	public static final Position LEFT = new Position(-1, 0);
	public static final Position DOWN = new Position(0, -1);
	public static final Position DOWN2 = new Position(0, -2);
	public static final Position RIGHT = new Position(1, 0);

	// Diagonal
	public static final Position UP_LEFT = new Position(-1, 1);
	public static final Position DOWN_LEFT = new Position(-1, -1);
	public static final Position DOWN_RIGHT = new Position(1, -1);
	public static final Position UP_RIGHT = new Position(1, 1);

	// Knight
	public static final Position LEFT2_UP = new Position(-2, 1);
	public static final Position LEFT2_DOWN = new Position(-2, -1);
	public static final Position RIGHT2_UP = new Position(2, 1);
	public static final Position RIGHT2_DOWN = new Position(2, -1);
	public static final Position UP2_LEFT = new Position(-1, 2);
	public static final Position UP2_RIGHT = new Position(1, 2);
	public static final Position DOWN2_LEFT = new Position(-1, -2);
	public static final Position DOWN2_RIGHT = new Position(1, -2);

	// Calculates the smallest delta between two positions
	// Only works for positions that are diagonal, horizontal or vertical relative to one another,
	// so before using this method make sure to use validation methods in Position class
	public static Position directionFromPositions(Position from, Position to)
	{
		int dx = to.x - from.x;
		int dy = to.y - from.y;

		if (dx != 0)
		{
			dx /= Math.abs(dx);
		}
		if (dy != 0)
		{
			dy /= Math.abs(dy);
		}

		return new Position(dx, dy);
	}

	private Collection<Position> set;
	private boolean single = false;

	public MovementSet(Collection<Position> set)
	{
		this.set = set;
	}

	public MovementSet(Collection<Position> set, boolean single)
	{
		this.set = set;
		this.single = single;
	}

	public Collection<Position> getSet()
	{
		return set;
	}

	public boolean isSingle()
	{
		return single;
	}
}

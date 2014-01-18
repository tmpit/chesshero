package com.kt.game;

import java.util.Collection;

/**
 * @author Todor Pitekov
 * @author Kiril Tabakov
 *
 * The MovementSet class reprensents a container for all the directions a certain chess piece
 * can move in
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

	/**
	 * Calculates the smallest delta between two positions. Works as expected only for positions that are
	 * diagonal, horizontal and vertical relative to one another. This method cannot be used to calculate
	 * a direction for the {@code Knight} chess piece as this chess piece does not move in a certain direction
	 * @param from A {@code Position} for the starting position
	 * @param to A {@code Position} for the destination position
	 * @return A {@code Position} representing the direction in which one has to travel between {@code from} and
	 * {@code to}
	 */
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
	private boolean recursive = true;

	/**
	 * Initializes a newly created {@code MovementSet} instance with a collection of {@code Position}
	 * objects representing directions
	 * @param set A {@code Collection}
	 */
	public MovementSet(Collection<Position> set)
	{
		this.set = set;
	}

	/**
	 * Initializes a newly created {@code MovementSet} instance with a collection of {@code Position}
	 * objects representing directions and a flag signifying whether movement in a direction from
	 * {@code set} is recursive or not
	 * @param set
	 */
	public MovementSet(Collection<Position> set, boolean recursive)
	{
		this.set = set;
		this.recursive = recursive;
	}

	/**
	 * Gets the collection containing all directions
	 * @return A {@code Collection} containing {@code Position} objects
	 */
	public Collection<Position> getSet()
	{
		return set;
	}

	/**
	 * Gets the recursive value of this movement set
	 * @return A {@code boolean}
	 */
	public boolean isRecursive()
	{
		return recursive;
	}
}

package com.kt.game;

/**
 * Created by Toshko on 12/20/13.
 */
public class Position implements Cloneable
{
	// Create a Position object from a board position string e.g. A1, H3, C1, etc.
	// Returns null if the board position is not valid
	public static Position positionFromBoardPosition(String pos)
	{
		if (pos.length() != 2)
		{
			return null;
		}

		pos = pos.toLowerCase();

		// x is offset from character 'a'
		int x = pos.charAt(0) - 'a';

		if (x < 0 || x > 7)
		{	// Invalid position
			return null;
		}

		// Minus one to convert into index
		int y = Character.getNumericValue(pos.charAt(1)) - 1;

		if (y < 0 || y > 7)
		{	// Invalid position
			return null;
		}

		return new Position(x, y);
	}

	// Returns position in the format [horizontal position : vertical position] e.g. A1, H3, etc.
	// Returns null if position object has invalid parameters
	public static String boardPositionFromPosition(Position position)
	{
		int x = position.x;
		int y = position.y;

		if (x < 0 || x > 7 || y < 0 || y > 7)
		{	// Invalid position
			return null;
		}

		// Add one to the second character as it is an index
		return String.format("%c%c", (char)('a' + x), Character.forDigit(y + 1, 10));
	}

	protected int x;
	protected int y;

	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public boolean equals(Position pos)
	{
		return (pos.x == x && pos.y == y);
	}

	public Position plus(Position pos)
	{
		return new Position(x + pos.x, y + pos.y);
	}

	public Position minus(Position pos)
	{
		return new Position(x - pos.x, y - pos.y);
	}

	public void add(Position pos)
	{
		x += pos.x;
		y += pos.y;
	}

	public void subtract(Position pos)
	{
		x -= pos.x;
		y -= pos.y;
	}

	public boolean isHorizontalOrVerticalTo(Position pos)
	{
		return x == pos.x || y == pos.y;
	}

	public boolean isDiagonalTo(Position pos)
	{
		return Math.abs(x - pos.x) == Math.abs(y - pos.y);
	}

	public boolean isWithinBoard()
	{
		return x > -1 && x < 8 && y > -1 && y < 8;
	}

	@Override
	public Position clone()
	{
		return new Position(x, y);
	}

	@Override
	public String toString()
	{
		return "{" + x + ", " + y + "}";
	}
}

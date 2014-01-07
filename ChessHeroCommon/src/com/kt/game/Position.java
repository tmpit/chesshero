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

		// y is offset from character '1'
		int y = pos.charAt(1) - '1';

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

		return String.format("%c%c", (char)('a' + x), (char)('1' + y));
	}

	public static Position ZERO = new Position(0, 0);

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

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Position)
		{
			return equals((Position)obj);
		}

		return false;
	}

	@Override
	public int hashCode()
	{	// Unique enough for the purposes of the class
		return (x * 10) + y;
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

	// Returns a new object with swapped x and y absolute values retaining the sign of the old components
	// e.g. {-2, 1} becomes {-1, 2}, but {2, 1} becomes {1, 2}
	public Position swappedAbsolute()
	{
		if ((x ^ y) < 0)
		{	// x and y have different signs - reverse signs of the components of the new object
			return new Position(-y, -x);
		}

		return new Position(y, x);
	}

	public Position negated()
	{
		return new Position(-x, -y);
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

	public byte toData()
	{
		return (byte)((x << 4) | y);
	}
}

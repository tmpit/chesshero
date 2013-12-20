package com.kt.game;

/**
 * Created by Toshko on 12/20/13.
 */
public class Position
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

	@Override
	public String toString()
	{
		return "{" + x + ", " + y + "}";
	}
}

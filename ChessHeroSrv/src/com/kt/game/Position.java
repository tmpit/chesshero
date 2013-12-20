package com.kt.game;

/**
 * Created by Toshko on 12/20/13.
 */
public class Position
{
	// Position has to be in the format [horizontal position : vertical position] e.g. A1, H3, etc.
	// Returns null if the position is not valid
	public static Position positionFromBoardPosition(String pos)
	{
		if (pos.length() != 2)
		{
			return null;
		}

		pos = pos.toLowerCase();

		int x = pos.charAt(0) - 'a';

		if (x < 0 || x > 7)
		{	// Invalid position
			return null;
		}

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

		return String.format("%c%c", 'a' + x, Character.forDigit(y + 1, 10));
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

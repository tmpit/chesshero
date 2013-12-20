package com.kt.game;

/**
 * Created by Toshko on 12/20/13.
 */
public class Position
{
	private static char[] horizontal = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

	// Position has to be in the format [horizontal position : vertical position] e.g. A1, H3, etc.
	// Returns null if the position is not valid
	public static Position positionFromBoardPosition(String pos)
	{
		if (pos.length() != 2)
		{
			return null;
		}

		pos = pos.toLowerCase();

		char hor = pos.charAt(0);
		int x = -1;

		for (int i = 0; i < 8; i++)
		{
			if (horizontal[i] == hor)
			{
				x = i;
				break;
			}
		}

		if (-1 == x)
		{	// Invalid position
			return null;
		}

		int y = Character.getNumericValue(pos.charAt(1));

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

		return String.format("%c%c", horizontal[x], Character.forDigit(y, 10));
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
}

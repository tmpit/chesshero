package com.kt.game;

/**
 * The Position class represents a position on the chess board. It consists of a horizontal offset {@code x}
 * and vertical offset {@code y}
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class Position implements Cloneable
{
	// Create a Position object from a board position string e.g. A1, H3, C1, etc.
	// Returns null if the board position is not valid

	/**
	 * Attempts to parse the specified string as a chess board position and creates
	 * a {@code Position} object on success
	 * @param pos A {@code String} representing the chess board position
	 * @return A {@code Position} object or null if the specified string could not be parsed
	 */
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

	/**
	 * Attempts to parse the specified byte and returns a {@code Position} on success
	 * @param data The {@code byte} to parse
	 * @return A {@code Position} instance on success or null if the specified
	 * byte could not be parsed as a {@code Position} object
	 */
	public static Position positionFromData(byte data)
	{
		int x = data >>> 4; // Take the upper 4 bits
		int y = data & 0xF; // Take the lower 4 bits

		if (x < 0 || x > 7 || y < 0 || y > 7)
		{	// Invalid position
			return null;
		}

		return new Position(x, y);
	}

	/**
	 * Converts a {@code Position} object to a {@code String} in a chess board notation
	 * @param position A {@code Position} to convert
	 * @return A {@code String} on success or null if the {@code Position} is out of the chess board
	 */
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

	/**
	 * Initializes a newly created {@code Position} instance with horizontal and vertical offset
	 * @param x An {@code int}
	 * @param y An {@code int}
	 */
	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the horizontal offset of this {@code Position} instance
	 * @return An {@code int}
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Gets the vertical offset of this {@code Position} instance
	 * @return An {@code int}
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Checks this instance and the {@code Position} object specified for equality
	 * @param pos A {@code Position} to check against
	 * @return True if the specified {@code Position} represents the save horizontal and vertical
	 * offset as this instance
	 */
	public boolean equals(Position pos)
	{
		return (pos.x == x && pos.y == y);
	}

	/**
	 * Checks this instance and an {@code Object} instance for equality
	 * @param obj A {@code Object} to check against
	 * @return True if the {@code Object} is an instance if {@code Position} and it represents the
	 * same horizontal and vertical offset as this instance, false otherwise
	 */
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

	/**
	 * Creates a new {@code Position} instance as the result of summing this instance
	 * and {@code pos}
	 * @param pos A {@code Position}
	 * @return The resulting {@code Position}
	 */
	public Position plus(Position pos)
	{
		return new Position(x + pos.x, y + pos.y);
	}

	/**
	 * Creates a new {@code Position} instance as the result of subtracting {@code pos}
	 * from this instance
	 * @param pos A {@code Position}
	 * @return The resulting {@code Position}
	 */
	public Position minus(Position pos)
	{
		return new Position(x - pos.x, y - pos.y);
	}

	/**
	 * Adds the values of the specified {@code Position} to this instance
	 * @param pos A {@code Position}
	 */
	public void add(Position pos)
	{
		x += pos.x;
		y += pos.y;
	}

	/**
	 * Subtracts the values of the specified {@code Position} from this instance
	 * @param pos A {@code Position}
	 */
	public void subtract(Position pos)
	{
		x -= pos.x;
		y -= pos.y;
	}

	/**
	 * Creates a new {@code Position} instance with swapped {@code x} and {@code y} absolute values
	 * retaining the sign of the old components. E.g. {2, 1} becomes {1, 2} but {-2, 1} becomes {-1, 2}
	 * @return A {@code Position}
	 */
	public Position swappedAbsolute()
	{
		if ((x ^ y) < 0)
		{	// x and y have different signs - reverse signs of the components of the new object
			return new Position(-y, -x);
		}

		return new Position(y, x);
	}

	/**
	 * Creates a new {@code Position} instance with the negative horizontal and vertical values of
	 * this instance
	 * @return A {@code Position}
	 */
	public Position negated()
	{
		return new Position(-x, -y);
	}

	/**
	 * Checks whether the specified {@code Position} is horizontal or vertical to this instance
	 * @param pos A {@code Position}
	 * @return True if {@code pos} is horizontal or vertical to this instance, false if not
	 */
	public boolean isHorizontalOrVerticalTo(Position pos)
	{
		return x == pos.x || y == pos.y;
	}

	/**
	 * Checks whether the specified {@code Position} is diagonal to this instance
	 * @param pos A {@code Position}
	 * @return True if {@code pos} is diagonal to this instance, false if not
	 */
	public boolean isDiagonalTo(Position pos)
	{
		return Math.abs(x - pos.x) == Math.abs(y - pos.y);
	}

	/**
	 * Checks whether this instance is within an 8x8 chess board
	 * @return True if this instance is within an 8x8 chess board, false if not
	 */
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

	/**
	 * Serializes this instance to a {@code byte}
	 * @return A {@code byte}
	 */
	public byte toData()
	{
		return (byte)((x << 4) | y);
	}
}

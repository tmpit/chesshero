package com.kt.game;

/**
 * The Color class is used to represent either the color of a chess piece or a field on the chess board
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public enum Color
{
	NONE, WHITE, BLACK;
	public Color Opposite;

	static
	{
		NONE.Opposite = NONE;
		WHITE.Opposite = BLACK;
		BLACK.Opposite = WHITE;
	}

	@Override
	public String toString()
	{
		if (this == Color.WHITE)
		{
			return "white";
		}
		if (this == Color.BLACK)
		{
			return "black";
		}
		return null;
	}

	/**
	 * Parses a {@code Color} object from a {@code String}
	 * @param str A {@code String} object to parse from
	 * @return A {@code Color} instance
	 */
	public static Color fromString(String str)
	{
		str = str.toLowerCase();

		if (str.equals("white"))
		{
			return Color.WHITE;
		}
		if (str.equals("black"))
		{
			return Color.BLACK;
		}
		return Color.NONE;
	}
}
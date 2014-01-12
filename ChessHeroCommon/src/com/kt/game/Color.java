package com.kt.game;

/**
 * Created with IntelliJ IDEA.
 * User: kiro
 * Date: 11/30/13
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
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

	public static Color fromString(String str)
	{
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
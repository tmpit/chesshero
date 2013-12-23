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
}
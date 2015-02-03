package com.chesshero.client;

/**
 * Created by Toshko on 12/17/14.
 *
 * Enumeration describing pawn promotion choices
 */
public enum Promotion
{
	QUEEN,
	ROOK,
	BISHOP,
	KNIGHT;

	public String toString()
	{
		switch (this)
		{
			case QUEEN: 	return "q";
			case ROOK:		return "r";
			case BISHOP: 	return "b";
			case KNIGHT:	return "n";
			default:		return null;
		}
	}
}

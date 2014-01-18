package com.kt.game;

import com.kt.game.ChessPiece;

/**
 * The BoardField class represents a field in a chess board. A chess board is represented by a grid of
 * objects of this class. Each instance has a color, position and a chess piece (if a chess piece is positioned on this field)
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class BoardField
{
	private Position position;
	private Color color;

	private ChessPiece chessPiece;

	/**
	 * Initializes a newly created {@code BoardField} instance with a position and color
	 * @param pos An instance of {@code Position} representing the position of the field in the chess board
	 * @param color An instance of {@code Color} enum representing the color of the field
	 */
	public BoardField(Position pos, Color color)
	{
		this(pos, color, null);
	}

	/**
	 * Initializes a newly created {@code BoardField} instance with position, color and a chess piece
	 * @param pos An instance of {@code Position} representing the position of the field in the chess board
	 * @param color An instance of {@code Color} enum representing the color of the field
	 * @param chessPiece An instance of {@code ChessPiece} which is "standing" on this field
	 */
	public BoardField(Position pos, Color color, ChessPiece chessPiece)
	{
		this.position = pos;
		this.color = color;
		this.chessPiece = chessPiece;
	}

	/**
	 * Gets the chess piece that is "standing" on this field
	 * @return An instance of {@code ChessPiece} or null if there is no chess piece on this field
	 */
	public ChessPiece getChessPiece()
	{
		return chessPiece;
	}

	/**
	 * Sets the chess piece that is "standing" on this field
	 * @param aPiece An instance of {@code ChessPiece} or null if you wish to remove the current chess piece
	 */
	public void setChessPiece(ChessPiece aPiece)
	{
		this.chessPiece = aPiece;
	}

	@Override
	public String toString()
	{
		return String.format(" %s ", (null == chessPiece ? "-" : chessPiece.toString()));
	}
}

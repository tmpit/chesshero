package com.kt.game;

import com.kt.game.chesspieces.ChessPiece;

/**
 * Created by Toshko on 12/23/13.
 */
public class BoardField
{
	private Position position;
	private Color color;

	private ChessPiece chessPiece;

	public BoardField(Position pos, Color color)
	{
		this(pos, color, null);
	}

	public BoardField(Position pos, Color color, ChessPiece chessPiece)
	{
		this.position = pos;
		this.color = color;
		this.chessPiece = chessPiece;
	}

	public ChessPiece getChessPiece()
	{
		return chessPiece;
	}

	public void setChessPiece(ChessPiece aPiece)
	{
		this.chessPiece = aPiece;
	}
}

package com.kt.game;

/**
 * Created by Toshko on 12/17/14.
 */
public class ChessMoveResult
{
	public final int resultCode;
	public final boolean checkmate;
	public final Player winner;

	public ChessMoveResult(int resultCode)
	{
		this(resultCode, false, null);
	}

	public ChessMoveResult(int resultCode, boolean checkmate, Player winner)
	{
		this.resultCode = resultCode;
		this.checkmate = checkmate;
		this.winner = winner;
	}
}

package com.kt.game;

/**
 * Created by Toshko on 12/17/14.
 *
 * Instance describes the result of an executed chess move
 */
public class ChessMoveResult
{
	/**
	 * A result code from @{link com.kt.api.Result}
	 */
	public final int resultCode;

	/**
	 * If the result of the chess move is checkmate
	 */
	public final boolean checkmate;

	/**
	 * If @{code checkmate} is @{code true}, this is set to the @{code Player} of the winning player
	 */
	public final Player winner;

	/**
	 * Initialize a @{code ChessMoveResult} object only with a result code. This constructor sets @{code checkmate}
	 * to @{code false} and @{code winner} to @{code null}
	 * @param resultCode Must be a value from @{link com.kt.api.Result}
	 */
	public ChessMoveResult(int resultCode)
	{
		this(resultCode, false, null);
	}

	/**
	 * Designated initializer for @{code ChessMoveResult}
	 * @param resultCode Must be a value from @{link com.kt.api.Result}
	 * @param checkmate Specify if the executed move checkmated a player
	 * @param winner Optional. Must be set if @{code checkmate} is @{code true}
	 */
	public ChessMoveResult(int resultCode, boolean checkmate, Player winner)
	{
		this.resultCode = resultCode;
		this.checkmate = checkmate;
		this.winner = winner;
	}
}

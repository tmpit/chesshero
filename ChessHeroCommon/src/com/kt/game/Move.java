package com.kt.game;

/**
 * Created by Toshko on 12/30/14.
 *
 * A model class describing a chess move in the past
 */
public class Move
{
	/**
	 * The player that executed the move
	 */
	public final Player executor;

	/**
	 * The encoded chess move
	 */
	public final String code;

	/**
	 * Designated initializer for the class
	 * @param executor A @{code Player} instance representing the chess move's executor. Must not be @{code null}
	 * @param code A @{code String} instance representing the encoded chess move. Must not be @{code null}
	 */
	public Move(Player executor, String code)
	{
		this.executor = executor;
		this.code = code;
	}
}

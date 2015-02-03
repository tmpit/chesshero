package com.kt.game;

/**
 * Created by Toshko on 12/17/14.
 *
 * A chess move executor is an object that operates on a chess board. Each type of chess move executor
 * implements its own logic for handling the move
 */
public interface ChessMoveExecutor
{
	/**
	 * Executes a chess move specified by a @{code Player} that performs the move
	 * @param executor a @{code Player} instance - the player that performs the move. Must not be @{code null}
	 * @param move a @{code String} object describing the chess move in an unspecified encoding. Must not be @{code null}
	 * @return a @{code ChessMoveResult} object describing the result of the executed move
	 */
	public ChessMoveResult executeMove(Player executor, String move);
}

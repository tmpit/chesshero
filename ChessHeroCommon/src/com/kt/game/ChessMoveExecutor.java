package com.kt.game;

/**
 * Created by Toshko on 12/17/14.
 */
public interface ChessMoveExecutor
{
	public ChessMoveResult executeMove(Player executor, String move);
}

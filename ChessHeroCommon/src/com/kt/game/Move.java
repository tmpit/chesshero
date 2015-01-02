package com.kt.game;

/**
 * Created by Toshko on 12/30/14.
 */
public class Move
{
	public final Player executor;
	public final String code;

	public Move(Player executor, String code)
	{
		this.executor = executor;
		this.code = code;
	}
}

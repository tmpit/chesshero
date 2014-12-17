package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 */
public abstract class PushParser
{
	protected abstract void reset();

	public PushParser parse(HashMap<String, Object> message)
	{
		reset();

		return this;
	}
}

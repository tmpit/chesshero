package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/17/14.
 *
 * A base abstract class for a push message parser. An instance of this class is a reusable object
 */
public abstract class PushParser
{
	/**
	 * Resets the state of the parser, clearing any previous information associated with the previous parse request
	 */
	protected abstract void reset();

	/**
	 * Parses the provided message. {@code reset()} is called prior to parsing the message
	 * @param message The push message as sent by the server. Must not be {@code null}
	 * @return {@code this}
	 */
	public PushParser parse(HashMap<String, Object> message)
	{
		reset();

		return this;
	}
}

package com.chesshero.client.parsers;

import com.kt.api.Result;

import java.util.HashMap;

/**
 * Created by Toshko on 12/8/14.
 *
 * A base class for an object parsing responses from server requests. An instance of this class is a reusable object
 */
public class ResponseParser
{
	/**
	 * The evaluated outcome of the request based on the {@code result}.
	 * {@code true} if {@code result == com.kt.api.Result.OK}, {@code false} otherwise
	 */
	public boolean success;

	/**
	 * The result code as returned in the response.
	 * @see com.kt.api.Result
	 */
	public Integer result;

	/**
	 * Resets the state of the parser, clearing any information from the previous parse request
	 */
	protected void reset()
	{
		success = false;
		result = null;
	}

	/**
	 * Parses the provided response. {@code reset()} is called prior to parsing the response
	 * @param response The response as sent by the server. Must not be {@code null}
	 * @return {@code this}
	 */
	public ResponseParser parse(HashMap<String, Object> response)
	{
		reset();

		if (response != null)
		{
			result = (Integer)response.get("result");
		}

		success = result != null && result == Result.OK;

		return this;
	}
}

package com.chesshero.client.parsers;

import com.kt.api.Result;

import java.util.HashMap;

/**
 * Created by Toshko on 12/8/14.
 */
public class ResponseParser
{
	public boolean success;
	public Integer result;

	protected void reset()
	{
		success = false;
		result = null;
	}

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

package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/8/14.
 */
public class LoginResponseParser extends ResponseParser
{
	public Integer userID;
	public String userName;

	@Override
	protected void reset()
	{
		super.reset();

		userID = null;
		userName = null;
	}

	@Override
	public LoginResponseParser parse(HashMap<String, Object> response)
	{
		super.parse(response);

		if (success)
		{
			userName = (String)response.get("username");
			userID = (Integer)response.get("userid");
		}

		return this;
	}
}

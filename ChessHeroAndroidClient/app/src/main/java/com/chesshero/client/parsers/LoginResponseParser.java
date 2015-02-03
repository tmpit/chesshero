package com.chesshero.client.parsers;

import java.util.HashMap;

/**
 * Created by Toshko on 12/8/14.
 *
 * Instances of this class can parse the response returned from a login request as described in
 * @{link com.kt.api.Action}
 */
public class LoginResponseParser extends ResponseParser
{
	/**
	 * The user id of the logged in user
	 */
	public Integer userID;

	/**
	 * The user name of the logged in user
	 */
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

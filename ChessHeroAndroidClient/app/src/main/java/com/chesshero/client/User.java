package com.chesshero.client;

/**
 * Created by Toshko on 12/7/14.
 */
public class User
{
	public final Integer userID;
	public final String userName;

	public User(Integer userID, String userName)
	{
		this.userID = userID;
		this.userName = userName;
	}

	public String toString()
	{
		return "<User :: " + "userid=" + userID + ", username=" + userName + ">";
	}
}

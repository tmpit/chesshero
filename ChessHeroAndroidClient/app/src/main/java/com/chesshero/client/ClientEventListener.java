package com.chesshero.client;

/**
 * Created by Toshko on 12/8/14.
 */
public interface ClientEventListener
{
	public void clientDidCompleteLogin(Integer result, User user);
}

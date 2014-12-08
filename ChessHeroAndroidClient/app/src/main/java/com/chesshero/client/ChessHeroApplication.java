package com.chesshero.client;

import android.app.Application;

/**
 * Created by Toshko on 12/8/14.
 */
public class ChessHeroApplication extends Application
{
	private Client sharedClient = null;

	public Client getClient()
	{
		if (null == sharedClient)
		{
			sharedClient = new Client(this);
		}

		return sharedClient;
	}
}

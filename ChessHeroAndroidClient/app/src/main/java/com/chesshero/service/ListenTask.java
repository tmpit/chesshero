package com.chesshero.service;

import android.os.Handler;
import com.kt.utils.SLog;
import java.util.Map;

/**
 * Created by Toshko on 12/6/14.
 */
public abstract class ListenTask extends Task
{
	private CHESCOSocket socket;

	ListenTask(CHESCOSocket socket)
	{
		this.socket = socket;
	}

	@Override
	public boolean execute()
	{
		Handler callbackHandler = getCallbackHandler();

		try
		{
			while (true)
			{
				final Map message = socket.read(0);

				if (callbackHandler != null)
				{
					callbackHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							onMessage(message);
						}
					});
				}
				else
				{
					onMessage(message);
				}
			}
		}
		catch (Throwable e)
		{
			SLog.write("[ListenTask] ~ exception thrown: " + e);
		}

		return true;
	}

	public abstract void onMessage(Map message);
}

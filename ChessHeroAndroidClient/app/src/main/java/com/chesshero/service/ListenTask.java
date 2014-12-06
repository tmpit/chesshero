package com.chesshero.service;

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
	public void cancel()
	{
		super.cancel();

		try
		{
			socket.getSocket().close();
		}
		catch (Throwable e)
		{
			SLog.write("[ListenTask] ~ exception thrown on closing socket: " + e);
		}
	}

	@Override
	public boolean execute()
	{
		while (true)
		{
			try
			{
				onMessage(socket.read(0));
			}
			catch (Throwable e)
			{
				SLog.write("[ListenTask] ~ exception thrown: " + e);
				break;
			}
		}

		return true;
	}

	public abstract void onMessage(Map message);
}

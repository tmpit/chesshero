package com.chesshero.service;

import android.os.Handler;
import com.kt.utils.SLog;
import java.util.Map;

/**
 * Created by Toshko on 12/6/14.
 *
 * This task listens on a socket until interrupted and passes received messages along to a subclass.
 */
public abstract class ListenTask extends Task
{
	private CHESCOSocket socket;

	/**
	 * Designated initializer for the class
	 * @param socket The socket this task will listen on. Must not be @{code null}
	 */
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

	/**
	 * Invoked every time this task reads a message from its socket. If a @{code Handler} object is provided through
	 * the @{code setCallbackHandler()} method, this method will be dispatched on that handler. Otherwise, it will be invoked
	 * on the thread this task runs on
	 * @param message The message read from the socket
	 */
	public abstract void onMessage(Map message);
}

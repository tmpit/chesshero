package com.chesshero.service;

import com.kt.utils.SLog;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Toshko on 12/6/14.
 *
 * This task writes a message to a socket
 */
public abstract class SendTask extends Task
{
	private CHESCOSocket socket;
	private Map message;
	private int timeout;

	/**
	 * Designated initializer for the class
	 * @param socket The socket to write the message to. Must not be @{code null}
	 * @param message The message to write. Must not be @{code null}
	 * @param timeout The write timeout measured in milliseconds. Pass 0 for no timeout
	 */
	SendTask(CHESCOSocket socket, Map message, int timeout)
	{
		this.socket = socket;
		this.message = message;
		this.timeout = timeout;
	}

	@Override
	public boolean execute()
	{
		try
		{
			socket.write(message, timeout);
		}
		catch (IOException e)
		{
			SLog.write("[SendTask] ~ exception thrown: " + e);
			return false;
		}

		return true;
	}
}

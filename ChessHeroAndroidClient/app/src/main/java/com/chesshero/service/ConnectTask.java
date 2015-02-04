package com.chesshero.service;

import com.kt.utils.SLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Toshko on 12/2/14.
 *
 * This task opens up a socket connection to a server
 */
public abstract class ConnectTask extends Task
{
	private CHESCOSocket socket;
	private String address;
	private int port;
	private int timeout;

	/**
	 * Designated initializer for the class
	 * @param address The address the socket should connect to. Must not be {@code null}
	 * @param port The port the socket should connect to
	 * @param timeout The connection timeout measured in milliseconds. Pass 0 to disable timeout
	 */
	ConnectTask(String address, int port, int timeout)
	{
		this.address = address;
		this.port = port;
		this.timeout = timeout;
	}

	/**
	 * Gets the created {@code CHESCOSocket} instance. Will exist only if the task completes successfully
	 * @return A {@code CHESCOSocket} instance if created
	 */
	public synchronized CHESCOSocket getSocket()
	{
		return socket;
	}

	private synchronized void setSocket(CHESCOSocket socket)
	{
		this.socket = socket;
	}

	@Override
	public boolean execute()
	{
		Socket sock = new Socket();

		try
		{
			sock.connect(new InetSocketAddress(address, port), timeout);
			sock.setKeepAlive(true);
			setSocket(new CHESCOSocket(sock));
		}
		catch (IOException e)
		{
			SLog.write("[ConnectTask] ~ exception thrown: " + e);
			return false;
		}

		return true;
	}
}

package com.chesshero.service;

import com.kt.utils.SLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Toshko on 12/2/14.
 */
public abstract class ConnectTask extends Task
{
	private CHESCOSocket socket;
	private String address;
	private int port;
	private int timeout;

	ConnectTask(String address, int port, int timeout)
	{
		this.address = address;
		this.port = port;
		this.timeout = timeout;
	}

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

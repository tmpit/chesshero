package com.chesshero.service;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import com.kt.Config;
import com.kt.chesco.*;
import com.kt.utils.SLog;

/**
 * Created by Toshko on 11/30/14.
 */
public class CHESCOSocket
{
	private Socket sock = null;
	private boolean connected = false;
	private CHESCOWriter writer;
	private CHESCOReader reader;

	private void log(String text)
	{
		if (Config.DEBUG)
		{
			SLog.write("[CHESCOSocket] : " + text);
		}
	}

	private void cleanUp()
	{
		sock = null;
		writer = null;
		reader = null;
	}

	public boolean isConnected()
	{
		return connected;
	}

	public void connect(String address, int port, int connectionTimeout) throws IOException
	{
		if (connected)
		{
			log("attempting to connect while connected. It's OK, I've got your back, sister.");
			return;
		}

		sock = new Socket();
		sock.connect(new InetSocketAddress(address, port), connectionTimeout);
		sock.setKeepAlive(true);

		writer = new CHESCOWriter(sock.getOutputStream());
		reader = new CHESCOReader(sock.getInputStream());

		connected = true;
	}

	public void disconnect()
	{
		if (!connected)
		{
			log("attempting to disconnect while disconnected. It's OK, I've got your back, sister.");
			return;
		}

		try
		{
			sock.close();
		}
		catch (IOException e)
		{
			log("exception on disconnect: " + e);
		}
		finally
		{
			connected = false;
			cleanUp();
		}
	}

	public void write(Map map, int timeout) throws IOException
	{
		if (!connected)
		{
			log("attempting to write to a disconnected socket. Epic fail...");
		}

		try
		{
			sock.setSoTimeout(timeout);
			writer.write(map);
		}
		catch (IOException e)
		{
			connected = false;
			cleanUp();
			throw e;
		}
	}

	public Map read(int timeout) throws IOException
	{
		if (!connected)
		{
			log("attempting to read from a disconnected socket. Epic fail...");
		}

		try
		{
			sock.setSoTimeout(timeout);
			return (Map)reader.read();
		}
		catch (IOException e)
		{
			connected = false;
			cleanUp();
			throw e;
		}
	}
}

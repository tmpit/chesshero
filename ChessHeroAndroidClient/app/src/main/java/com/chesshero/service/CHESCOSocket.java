package com.chesshero.service;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import com.kt.chesco.*;
import com.kt.utils.SLog;

/**
 * Created by Toshko on 11/30/14.
 */
public class CHESCOSocket
{
	private Socket sock = null;
	private CHESCOWriter writer;
	private CHESCOReader reader;

	CHESCOSocket(Socket socket) throws IOException
	{
		sock = socket;
		writer = new CHESCOWriter(sock.getOutputStream());
		reader = new CHESCOReader(sock.getInputStream());
	}

	public Socket getSocket()
	{
		return sock;
	}

	public void write(Map map, int timeout) throws IOException
	{
		sock.setSoTimeout(timeout);
		writer.write(map);
	}

	public Map read(int timeout) throws IOException
	{
		sock.setSoTimeout(timeout);
		return (Map)reader.read();
	}
}

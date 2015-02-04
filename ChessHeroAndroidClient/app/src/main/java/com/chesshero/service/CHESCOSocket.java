package com.chesshero.service;


import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import com.kt.chesco.*;

/**
 * Created by Toshko on 11/30/14.
 *
 * A wrapper around a {@code Socket} object working with {@code CHESCOWriter} and {@code CHESCOReader} objects to
 * to directly read and write Java objects to the socket
 */
public class CHESCOSocket
{
	private Socket sock = null;
	private CHESCOWriter writer;
	private CHESCOReader reader;

	/**
	 * Designated initializer for this class
	 * @param socket The socket this instance will operate on. Must not be {@code null}
	 * @throws IOException
	 */
	CHESCOSocket(Socket socket) throws IOException
	{
		sock = socket;
		writer = new CHESCOWriter(sock.getOutputStream());
		reader = new CHESCOReader(sock.getInputStream());
	}

	/**
	 * Gets the socket this instance operates on
	 * @return The socket this instance operates on
	 */
	public Socket getSocket()
	{
		return sock;
	}

	/**
	 * Writes the serialized {@code Map} to the socket's output stream with a timeout
	 * @param map The map to write to the socket's output stream. Must not be {@code null}
	 * @param timeout The write timeout measured in milliseconds. Pass 0 to disable timeout
	 * @throws IOException
	 */
	public void write(Map map, int timeout) throws IOException
	{
		sock.setSoTimeout(timeout);
		writer.write(map);
	}

	/**
	 * Reads a {@code Map} object from the socket's input stream with a timeout
	 * @param timeout The read timeout measured in milliseconds. Pass 0 to disable timeout
	 * @return The parsed {@code Map} object
	 * @throws IOException
	 */
	public Map read(int timeout) throws IOException
	{
		sock.setSoTimeout(timeout);
		return (Map)reader.read();
	}
}

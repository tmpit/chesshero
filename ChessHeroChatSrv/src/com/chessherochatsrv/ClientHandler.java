/*
ToDo:
1. User establishes connection
2. Set initial timeout for the socket and start reading
3. If the read times out, close connection
4. If something is read, but it is not recognised as a valid message to the server, close connection
5. If the server recognises the message, check for auth token
6. If auth token is not provided, close connection
7. If auth token is provided, fetch gid and uid from database for the specified token
8. If no row exists with that token, close connection (maybe send back an error message before that, up to you)
9. If the gid is already used by a pair of players, close connection
10. If the gid is used by one player and that player’s uid is the same as the uid of the player connecting now, close connection
11. If the gid is used by one player and that player’s uid is not the same as the uid of the player connection now, pair the two players
12. If the gid is not used by anyone, place the player in a waiting state while his opponent connects. Until then, ignore any chat message sent by the player
13. If the connection still exists, set the socket timeout to 0, meaning a read would never timeout
 */

package com.chessherochatsrv;

import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientHandler extends Thread {

	private static String DB_URL = "jdbc:mysql://localhost:3306/";
	private static String DB_NAME = "srv_db";
	private static String DB_USER = "root";
	private static String DB_PASS = "";

	protected Socket socket;
	protected Vector threadPool;

	private Connection conn = null;
	private boolean isOpen = false;

	private boolean isAuthenticated = false;
	private boolean isValidToken = false;
	private String token;
	private int gid;
	private int uid;

	public ClientHandler(Socket socket, Vector threadPool) {

		this.socket = socket;
		this.threadPool = threadPool;
		threadPool.add(this);
	}

	private void connectToDB() throws SQLException {

		if (isOpen) {
			return;
		}

		String connector = "com.mysql.jdbc.Driver";

		try {
			Class.forName(connector).newInstance();
			conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER,
					DB_PASS);
			isOpen = true;
		} catch (IllegalAccessException e) {
			throw new SQLException("Connect raised illegal access exception");
		} catch (InstantiationException e) {
			throw new SQLException("Connect raised instantiation exception");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Connect raised class not found exception");
		}
	}

	private void checkFromDB(String token) {

		try {
			connectToDB();
			PreparedStatement stmt = null;
			ResultSet set = null;

			stmt = conn
					.prepareStatement("SELECT gid,uid FROM chat_auth WHERE token = ?");
			stmt.setString(1, token);
			set = stmt.executeQuery();

			if (set.next()) {
				this.gid = set.getInt(1);
				this.uid = set.getInt(2);
				isValidToken = true;
				isAuthenticated = true;
				System.out.println("gid=" + this.gid + " uid=" + this.uid);
				disconnectFromDB();
			} else {
				disconnectFromDB();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void disconnectFromDB() {

		if (null == conn) {
			return;
		}

		try {
			conn.close();
			isOpen = false;
		} catch (SQLException ignore) {
		} finally {
			conn = null;
		}
	}

	public void run() {

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()));

			while (true) {

				String inString = in.readLine();
				
				if (!isAuthenticated) {
					// If there is some incoming data, remove the timeout
					if (inString.trim().length() > 0) {
						socket.setSoTimeout(0);
					}

					// Check if the token is valid
					checkFromDB(inString);

					if (!isValidToken) {
						out.println("Not a valid token!");
						out.flush();
						break;
					} else {
						isAuthenticated = true;
						out.println("Ready");
						out.flush();
					}
				} else {
					// Start chatting
					out.println("");
					out.flush();
				}
				
				if (inString.trim().equals("q")) {
					break;
				}
				
				System.out.println("Client sent: " + inString);
			}
			socket.close();
		} catch (IOException e) {
			System.out.println("Client disconnected!");
		} finally {
			threadPool.remove(this);
		}
	}
}
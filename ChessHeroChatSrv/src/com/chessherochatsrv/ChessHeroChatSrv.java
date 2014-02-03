package com.chessherochatsrv;

import java.net.*;
import java.util.*;

public class ChessHeroChatSrv {
	static final int PORT = 333;
	static ServerSocket socket;
	static Vector<ClientHandler> threadPool = new Vector<ClientHandler>();

	public static void main(String[] args) {
		try {
			socket = new ServerSocket(PORT);
			System.out.println("Server is started!");
			
			while (true) {
				Socket incoming = socket.accept();
				new ClientHandler(incoming, threadPool).start();
				System.out.println("New client connected!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

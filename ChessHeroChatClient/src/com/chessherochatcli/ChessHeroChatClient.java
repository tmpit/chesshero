package com.chessherochatcli;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class ChessHeroChatClient extends Frame {

	// Communication elements
	private Socket socket;
	private BufferedReader in;
	private PrintWriter ou;
	final int PORT_NUMBER = 333;

	// GUI elements
	private TextField textSend = new TextField(20);
	private TextArea textArea = new TextArea(5, 20);

	private Button buttonConnect = new Button("Connect");
	private Button buttonSend = new Button("Send");
	private Button buttonDisconnect = new Button("Disconnect");
	private Button buttonQuit = new Button("Quit");

	private Panel leftPanel = new Panel();
	private Panel rightPanel = new Panel();

	private Label empty = new Label("");

	ChessHeroChatClient() {
		setTitle("TChatClient");
		setLocationRelativeTo(null);
		setSize(500, 500);
		setResizable(false);
		this.setBackground(Color.lightGray);

		leftPanel.setLayout(new BorderLayout());
		rightPanel.setLayout(new GridLayout(6, 1));

		leftPanel.add(textSend, BorderLayout.NORTH);
		leftPanel.add(textArea, BorderLayout.CENTER);

		rightPanel.add(buttonConnect);
		rightPanel.add(buttonSend);
		rightPanel.add(buttonDisconnect);
		rightPanel.add(empty);
		rightPanel.add(buttonQuit);

		add(leftPanel, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);

		buttonSend.setEnabled(false);
		buttonDisconnect.setEnabled(false);

		pack();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});

		buttonConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});

		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(in, ou);
			}
		});

		buttonDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect(in, ou);
			}
		});

		buttonQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
	}

	private void connect() {
		try {
			socket = new Socket(textSend.getText(), PORT_NUMBER);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			ou = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()));

			textArea.append(in.readLine() + "\n");
			textArea.append(in.readLine() + "\n");
			
			buttonConnect.setEnabled(false);
			buttonSend.setEnabled(true);
			buttonDisconnect.setEnabled(true);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	private void send(BufferedReader in, PrintWriter ou) {
		String inLine;
		try {
			ou.println(textSend.getText());
			ou.flush();
			textSend.setText("");
			inLine = in.readLine();
			textArea.appendText("Server: " + inLine + "\n");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	private void disconnect(BufferedReader in, PrintWriter ou) {
		ou.println("Bye!");
		ou.flush();
		try {
			in.close();
			ou.close();
			
			buttonConnect.setEnabled(true);
			buttonSend.setEnabled(false);
			buttonDisconnect.setEnabled(false);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	private void quit() {
		System.exit(0);
	}

	public static void main(String[] args) {
		ChessHeroChatClient chessHeroChatClient = new ChessHeroChatClient();
	}
}
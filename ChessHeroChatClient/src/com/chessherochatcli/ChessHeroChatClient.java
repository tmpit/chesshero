package com.chessherochatcli;

import java.io.*;
import java.net.*;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class ChessHeroChatClient extends JFrame {
	// Communication elements
	private Socket chatSocket;
	private BufferedReader chatIn;
	private PrintWriter chatOut;
	final String CHAT_SERVER = "localhost";
	final int CHAT_SERVER_PORT = 1033;

	// GUI elements
	private JTextField textSend = new JTextField(20);
	private JTextArea textArea = new JTextArea(5, 20);

	private JScrollPane scroll = new JScrollPane(textArea);

	private JButton buttonConnect = new JButton("Connect");
	private JButton buttonSend = new JButton("Send");
	private JButton buttonDisconnect = new JButton("Disconnect");
	private JButton buttonQuit = new JButton("Quit");

	private JPanel leftPanel = new JPanel();
	private JPanel rightPanel = new JPanel();

	private JLabel empty = new JLabel("");

	// User's nickname
	private String nickName = "";

	ChessHeroChatClient() {
		setTitle("ChessHero Chat Client");
		setLocationRelativeTo(null);
		setSize(500, 500);
		setResizable(false);

		leftPanel.setLayout(new BorderLayout());
		rightPanel.setLayout(new GridLayout(6, 1));

		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		leftPanel.add(textSend, BorderLayout.NORTH);
		leftPanel.add(scroll, BorderLayout.EAST);

		rightPanel.add(buttonConnect);
		rightPanel.add(buttonSend);
		rightPanel.add(buttonDisconnect);
		rightPanel.add(empty);
		rightPanel.add(buttonQuit);

		add(leftPanel, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);

		buttonSend.setEnabled(false);
		buttonDisconnect.setEnabled(false);

		textArea.setEditable(false);

		pack();
		setVisible(true);

		buttonConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});

		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(chatOut);
			}
		});

		buttonDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect(chatIn, chatOut);
			}
		});

		buttonQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});

		textSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(chatOut);
			}
		});

		scroll.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						e.getAdjustable().setValue(
								e.getAdjustable().getMaximum());
					}
				});
	}

	/**
	 * Connects to the server and asks for a nickname. When the connection is
	 * made, a connection notification is sent to the server. After that a swing
	 * worker infinitely waits for incoming messages.
	 */
	private void connect() {
		try {
			chatSocket = new Socket(CHAT_SERVER, CHAT_SERVER_PORT);

			if (chatSocket.isConnected()) {
				do {
					nickName = JOptionPane.showInputDialog(null,
							"Enter your nickname:");
				} while (nickName.trim().equals(""));
			}

			chatIn = new BufferedReader(new InputStreamReader(
					chatSocket.getInputStream()));
			chatOut = new PrintWriter(new OutputStreamWriter(
					chatSocket.getOutputStream()));

			chatOut.println(nickName + " has connected.");
			chatOut.flush();

			buttonConnect.setEnabled(false);
			buttonDisconnect.setEnabled(true);
			buttonSend.setEnabled(true);

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					while (true) {
						String message = chatIn.readLine();
						textArea.append(message + "\n");
					}
				}
			};

			worker.execute();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to "
					+ CHAT_SERVER + " on port " + CHAT_SERVER_PORT, "Error", 2);
		}
	}

	/**
	 * Sends a message to the server and clears the textSend field
	 */
	private void send(PrintWriter out) {
		out.println(nickName + ": " + textSend.getText());
		out.flush();
		textSend.setText("");
	}

	/**
	 * Disconnects from the server, but before that it sends a disconnect
	 * notification
	 */
	private void disconnect(BufferedReader in, PrintWriter out) {
		try {
			out.println(nickName + " has disconnected.");
			out.flush();
			in.close();
			out.close();
			chatSocket.close();

			buttonConnect.setEnabled(true);
			buttonSend.setEnabled(false);
			buttonDisconnect.setEnabled(false);

		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	/**
	 * Quits, but before that calls disconnect() in order to properly close the
	 * connection and notify all other chat clients
	 */
	private void quit() {
		disconnect(chatIn, chatOut);
		System.exit(0);
	}

	public static void main(String[] args) {
		new ChessHeroChatClient();
	}
}
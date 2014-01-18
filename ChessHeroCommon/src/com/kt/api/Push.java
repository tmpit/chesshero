package com.kt.api;

/**
 * <pre>
 * The Push class groups event codes for push messages. A push message is considered any message sent
 * from the server that is not a response to a request. Each push message must have at least two
 * parameters: [push:BOOL] which is always true and flags the message as a push message and
 * [event:INT] which signifies the event that has occurred.
 *
 * About the notation used refer to the com.kt.api.Action class
 * </pre>
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 *
 * @see com.kt.api.Action
 */
public class Push
{
	/**
	 * <pre>
	 * Sent to you when another player joins the game you are in. When you receive this event, it also means that the game has started
	 * Parameters:
	 * - [opponentname:STR] - The username of the opponent that has joined the game
	 * - [opponentid:INT] - The user id of the opponent that has joined the game
	 * </pre>
	 */
	public static final int GAME_JOIN = 1;

	/**
	 * <pre>
	 * Sent in the event of a game ending
	 * The game can end in four ways:
	 * - by sudden death
	 * - by checkmate
	 * - one of the players exits the game
	 * - one of the players disconnects from the server
	 * Whoever exits the game (or disconnects, which the server interprets in the same way) is considered defeated.
	 * In the first and second cases, this event will be sent to both players
	 * In the third and fourth cases, this event will be sent to the player that is still in the game
	 * Parameters:
	 * - [winner:INT] - The user id of the winner or null if the game is draw
	 * - [suddendeath:BOOL] - Optional. Always true. Present when the winner's opponent runs out of time
	 * - [checkmate:BOOL] - Optional. Always true. Present when the winner has checkmated their opponent
	 * - [exit:BOOL] - Optional. Always true. Sent when the winner's opponent exits the game
	 * - [disconnect:BOOL] - Optional. Always true. Sent when the winner's opponent disconnects from the server
	 * </pre>
	 */
	public static final int GAME_END = 2;

	/**
	 * <pre>
	 * You receive this event when your opponent makes a move
	 * Parameters:
	 * - [move:STR] - The move as per the Pure coordinate notation: http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
	 * - [attackers:ARR] - Optional. Present only when your king is in check. Each element in the array is a [STR]
	 * representing the position of a chess piece attacking your king
	 * </pre>
	 */
	public static final int GAME_MOVE = 3;

	/**
	 * <pre>
	 * You receive this event when your opponent sends a request to save the game. Since the game is closed after it is saved, you need to confirm
	 * that you want to save the game. You respond to your opponent's request by sending a save request in which you can either accept or decline.
	 * After you receive this request, the server will no longer accept any other requests from you except a save game request and will return an
	 * appropriate error code if you try to do anything else
	 * </pre>
	 */
	public static final int GAME_SAVE = 4;
}

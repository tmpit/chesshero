package com.kt.api;

/**
 * Created by Toshko on 12/20/13.
 */
public class Push
{
	// About the notation used:
	// On the left side of the colon is the key for the parameter.
	// On the right side of the colon is the type of the parameter.

	// Each message from the server that is not a response to a request should have at least two parameters:
	// [push:BOOL] which is always true and [event:INT] which signifies the event that has occurred.
	// The following are all event codes sent by the server and their additional parameters:

	// Game joined
	// Description: This event is sent to you when another player joins the game you are in.
	// When you receive this event, it also means that the game has started
	// Parameters:
	// - [opponentname:STR] - the username of the opponent
	// - [opponentid:INT] - the user id of the opponent
	public static final int GAME_JOIN = 1;

	// Game ended
	// Description: This event is sent to the two players in the event of a game ending.
	// The game can end in four ways:
	// - the game reaches its natural end
	// - the game is saved
	// - one of the players exits the game
	// - one of the players disconnects from the server
	// Whoever exits the game (or disconnects, which the server interprets in the same way) is considered defeated.
	// In the first and second cases, this event will be sent to both players
	// In the third and fourth cases, this event will be sent to the player that is still in the game
	// Parameters:
	// - [winner:INT] - optional, the user id of the winner, if there is no such key, the game has been closed (e.g. after a save), if winner is null, the game is draw
	// - [opponentexited:BOOL] - optional, sent when the other player exits the game, always true, might be present only if there is a winner
	// - [opponentdisconnected:BOOL] - optional, sent when the other player disconnects from the server, always true, might be present only if there is a winner
	public static final int GAME_END = 2;

	// Game move
	// Description: You receive this event when your opponent makes a move
	// Parameters:
	// - [move:STR] - the move as per the Pure coordinate notation: http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
	// - [attackers:ARR] - optional, present only when your king is in check.
	// Each element in the array is a [STR] representing the position of a chess piece attacking your king
	public static final int GAME_MOVE = 3;

	// Game save
	// Description: You receive this event when your opponent sends a request to save the game. Since the game is closed after it is saved, you need to confirm
	// that you want to do that. You respond to your opponent's request by sending a save request in which you can either accept or decline.
	// After you receive this request, the server will no longer accept any other requests from you except a save game request and will return an appropriate error code
	// if you try to do anything else.
	public static final int GAME_SAVE = 4;
}

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

	// Game started
	// Description: This event is sent to the creator of a game when another player joins the game
	// Parameters:
	// - [opponentname:STR] - the username of the opponent
	// - [opponentid:INT] - the user id of the opponent
	public static final int GAME_START = 1;

	// Game ended
	// Description: This event is sent to the two players in the event of a game ending.
	// The game can end in three ways:
	// - the game reaches its natural end
	// - one of the players exits the game
	// - one of the players disconnects from the server
	// Whoever exits the game (one way or another) before its natural end is considered defeated.
	// In the first case, this event will be sent to both players
	// In the second and third cases, this event will be sent to the player that is still in the game
	// Parameters:
	// - [winner:INT] - optional, the user id of the winner, if there is no winner the game is draw
	// - [opponentexited:BOOL] - optional, sent when the other player exits the game, always true, might be present only if winner is present
	// - [opponentdisconnected:BOOL] - optional, sent when the other player disconnects from the server, always true, might be present only if winner is present
	public static final int GAME_END = 2;

	// Game move
	// Description: You receive this event when your opponent makes a move
	// Parameters:
	// - [move:STR] - the move as per the Pure coordinate notation: http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
	// - [attackers:ARR] - optional, present only when your king is in check.
	// Each element in the array is a [STR] representing the position of a chess piece attacking your king
	public static final int GAME_MOVE = 3;

	// Game save
	// Description: You receive this event when your opponent has saved the game
	public static final int GAME_SAVE = 4;
}

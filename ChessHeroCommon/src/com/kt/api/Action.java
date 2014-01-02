package com.kt.api;

/**
 * Created by Toshko on 12/8/13.
 */
public class Action
{
    // About the notation used:
    // On the left side of the colon is the key for the parameter.
    // On the right side of the colon is the type of the parameter.

    // Each request to the server should always have at least one parameter [action:INT].
    // Each response from the server should have at least one parameter [result:INT].
    // The following are all action codes recognized by the server, their additional parameters required for each action,
    // and all additional parameters returned if the request has succeeded:

    // Login
    // Description: Authenticate an existing user
    // Parameters:
	// - [username:STR] - the username
	// - [password:STR] - the password
    // Returns:
	// - [username:STR] - the username
	// - [userid:INT] - the user's id
    public static final int LOGIN = 1;

    // Register
    // Description: Register a new user
    // Parameters:
	// - [username:STR] - the username
	// - [password:STR] - the password
    // Returns:
	// - [username:STR] - the username
	// - [userid:INT] - the user's id
    public static final int REGISTER = 2;

    // Create game
    // Description: Create a new game that would be available to all players
    // Parameters:
	// - [gamename:STR] - the name of the game
	// - [color:STR] - optional, the color of the player, either "black" or "white", default value is "white"
    // Returns:
	// - [gameid:INT] - the id of the game
	// - [chattoken:STR] - the token needed to chat with the other player
    public static final int CREATE_GAME = 3;

    // Cancel game
    // Description: Cancel your created game. Only works for a game the current user has created and only if the game hasn't started yet
    // Parameters:
	// - [gameid:INT] - the game id of the game to cancel
    public static final int CANCEL_GAME = 4;

    // Fetch games
    // Description: Fetch all games waiting for a second player to join
    // Parameters:
	// - [offset:INT] - optional, the offset within the games list, default value is 0
	// - [limit:INT] - optional, the maximum number of entries after offset, default value is 100, maximum value is 1000
    // Returns:
	// - [games:ARR] - each element in the array has:
	// 		- [gameid:INT] - the id of the game
	// 	 	- [gamename:STR] - the name of the game
	// 	 	- [playercolor:STR] - the color the creator of the game has picked, either "white" or "black"
    public static final int FETCH_GAMES = 5;

    // Join game
    // Description: Join a game waiting for a second player to join and start playing
    // Parameters:
	// - [gameid:INT] - the id of the game to join
    // Returns:
	// - [opponentname:STR] - the username of the opposing player
	// - [opponentid:INT] - the user id of the opposing player
	// - [chattoken:STR] - the token needed to chat with the other player
    public static final int JOIN_GAME = 6;

    // Exit game
	// Description: Exit the current game. Whoever exits the game before its natural end is considered defeated
    // Parameters:
	// - [gameid:INT] - the id of the game to exit
	// Returns:
	// - [winner:INT] - the user id of the winner, this is always the opponent
    public static final int EXIT_GAME = 7;

	// Execute move
	// Description: Move a chess piece
	// Parameters:
	// - [move:STR] - the move as per the Pure coordinate notation: http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
	public static final int MOVE = 8;
}

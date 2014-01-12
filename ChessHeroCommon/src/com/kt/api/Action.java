package com.kt.api;

/**
 * Created by Toshko on 12/8/13.
 */
public class Action
{
    // About the notation used:
	// A typical parameter: [name:STR]
    // On the left side of the colon is the key for the parameter - in this example the key is "name"
    // On the right side of the colon is the type of the parameter as per CHESCO's supported types - in this example the type is STR which stands for string

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
	// - [type:STR] - optional, it can be either one of these (without the quotes):
	//		- 'pending' - fetch all pending games
	//		- 'saved' - fetch all saved games in which the current user is one of the players
	// If not present, 'pending' is the default
	// - [offset:INT] - optional, the offset within the games list, default value is 0
	// - [limit:INT] - optional, the maximum number of entries after offset, default value is 100, maximum value is 1000
    // Returns:
	// - [games:ARR] - each element in the array has:
	// 		- [gameid:INT] - the id of the game
	// 	 	- [gamename:STR] - the name of the game
	//		- [userid:INT] - the id of the opponent
	//		- [username:STR] - the name of the opponent
	// 	 	- [usercolor:STR] - the color the opponent has picked, either "white" or "black"
    public static final int FETCH_GAMES = 5;

    // Join game
    // Description: Join a game waiting for a second player to join and start playing
    // Parameters:
	// - [gameid:INT] - the id of the game to join
    // Returns:
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

	// Save game
	// Description: Save and leave the game. After a game is saved, it can then be resumed only by the same two players. A game save in Chess Hero represents
	// the state of a specific game before that game was closed. The state can only be used to resume a specific game and cannot be used to start a separate
	// independent instance of the game.
	// Since a game is closed once saved, your opponent will need to confirm that they are willing to perform this action as well.
	// After you send a save game request, the game enters a paused state and a push is sent to your opponent to notify them that they have to respond to the request.
	// During the paused state, the server will not accept any request from your opponent other than a save game request to accept or decline.
	// If they decline the request, the game will continue normally. If they accept it, the game will be saved, after which it will be closed.
	// Furthermore, this is a blocking request, meaning that you do not receive a response until the save game request is resolved. This applies only to the
	// player that initiates the save game routine. Any requests sent during the block will be processed after the save game request is resolved.
	// Parameters:
	// - [gameid:INT] - the id of the game to save
	// - [save:BOOL] - optional, true if you want to save the game, false if not. If not present, default value is true
	// Obviously sending false is a no-op when trying to initiate the save game routine. However, when responding to your opponent's request, this is how
	// you can decline it
	// Returns:
	// - [saved:BOOL] - true if the game was saved, false if not
	public static final int SAVE_GAME = 9;

	// Delete saved game
	// Description: Delete a game that was previously saved
	// Parameters:
	// - [gameid:INT] - the id of the game to delete
	public static final int DELETE_SAVED_GAME = 10;

	// Resume game
	// Description: Enter a previously saved game. The game resumes after both of the players enter it, in which moment the game save is deleted as it serves no
	// purpose from this moment on
	// Parameters:
	// - [gameid:INT] - the id of the game to resume
	// Returns:
	// - [chattoken:STR] - the token needed to chat with the other player
	public static final int RESUME_GAME = 11;
}

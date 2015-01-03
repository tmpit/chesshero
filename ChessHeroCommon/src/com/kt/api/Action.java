package com.kt.api;

/**
 * <pre>
 * The Action class groups all action codes the game server recognizes
 *
 * About the notation used:
 * A typical parameter: [name:STR]
 * On the left side of the colon is the key for the parameter - in this example the key is "name"
 * On the right side of the colon is the type of the parameter as per CHESCO's supported types - in this example the type is STR which stands for string
 *
 * Each request to the server must always have at least one parameter [action:INT] which holds the action code
 * Each response from the server must have at least one parameter [result:INT] which holds the result code
 *
 * Each action has a Parameters section which lists all additional parameters expected by the server alongside the action code parameter and a Returns
 * section listing all additional parameters (alongside the result code) the server will return if the request succeeds
 * </pre>
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 * @see com.kt.api.Result
 * @see com.kt.chesco.CHESCO
 */
public class Action
{
	/**
	 * <pre>
	 * Authenticate an existing user
	 * Parameters:
	 * - [username:STR] - The username of the user. It must be between 3 and 50 characters long (leading and trailing spaces are trimmed)
	 * - [password:STR] - The password of the user. It must be between 3 and 50 characters long
	 * Returns:
	 * - [username:STR] - The username of the user
	 * - [userid:INT] - The user id of the user
	 * </pre>
	 */
    public static final int LOGIN = 1;

	/**
	 * <pre>
	 * Register a new user. After a user registers they are considered logged in
	 * - [username:STR] - The username of the user. It must be between 3 and 50 characters long (leading and trailing spaces are trimmed)
	 * - [password:STR] - The password of the user. It must be between 3 and 50 characters long
	 * Returns:
	 * - [username:STR] - The username of the user
	 * - [userid:INT] - The user id of the user
	 * </pre>
	 */
    public static final int REGISTER = 2;

	/**
	 * <pre>
	 * Create a new game that would be available to all players
	 * Parameters:
	 * - [gamename:STR] - The name of the game. It must be between 3 and 256 characters long (leading and trailing spaces are trimmed)
	 * - [color:STR] - Optional. The color of the player - either "black" or "white", default value is "white" if not present
	 * - [timeout:INT] - Optional. The time (in minutes) each player should have to finish the game (time control type used is "sudden death").
	 * The timeout can be between 3 and 30 minutes for timed chess matches or 0 for timeless. If not present, the default is 0
	 * Returns:
	 * - [gameid:INT] - The if of the game which
	 * - [gamename:STR] - The name of the game
	 * - [color:STR] - The color of the player
	 * - [timeout:INT] - The game's timeout value. 0 means no timeout
	 * - [chattoken:STR] - The chat token needed to authenticate with the chat server
	 * </pre>
	 */
    public static final int CREATE_GAME = 3;

	/**
	 * <pre>
	 * Cancel your created game. Only works for a game the user has created and only if the game hasn't started yet
	 * Parameters:
	 * - [gameid:INT] - The game id of the game to cancel
	 * </pre>
	 */
    public static final int CANCEL_GAME = 4;

	/**
	 * <pre>
     * Fetch all the games that can be joined
     * Parameters:
	 * - [type:STR] - Optional. It can be either one of (without the quotes):
	 *		- "pending" - Fetch all pending games (a pending game is a game with only one player waiting for an opponent to join)
	 *		- "saved" - Fetch all saved games in which the user is one of the players
	 * If not present, "pending" is the default
	 * - [offset:INT] - Optional. The offset within the games list, default value is 0
	 * - [limit:INT] - Optional. The maximum number of entries after offset. Default value is 100. Maximum value is 1000
     * Returns:
	 * - [games:ARR] - Each element in the array has:
	 * 		- [gameid:INT] - The id of the game
	 * 	 	- [gamename:STR] - The name of the game
	 *		- [timeout:INT] - The game's timeout in minutes
	 *		- [userid:INT] - The user id of the opponent
	 *		- [username:STR] - The username of the opponent
	 * 	 	- [usercolor:STR] - The color the opponent has picked, either "white" or "black"
	 * 	 </pre>
	 */
    public static final int FETCH_GAMES = 5;

	/**
	 * <pre>
	 * Join a pending game and start playing
	 * Parameters:
	 * - [gameid:INT] - The id of the game to join
	 * Returns:
	 * - [chattoken:STR] - The chat token needed to authenticate with the chat server
	 * </pre>
	 */
    public static final int JOIN_GAME = 6;

	/**
	 * <pre>
     * Exit the current game. Whoever exits the game before its natural end is considered defeated
     * Parameters:
	 * - [gameid:INT] - The id of the game to exit
	 * Returns:
	 * - [winner:INT] - The user id of the winner. This is always the opponent
	 * </pre>
	 */
    public static final int EXIT_GAME = 7;

	/**
	 * <pre>
	 * Execute a game move
	 * Parameters:
	 * - [move:STR] - The move as per the Pure coordinate notation described at http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
	 * Returns:
	 * - [playertime:INT] - Optional. Present only if playing a timed game. This is the time in milliseconds the player executing the
	 * move has spent in the game. The time is calculated after the move is executed.
	 * </pre>
	 */
	public static final int MOVE = 8;

	/**
	 * <pre>
	 * Save and leave the game. After a game is saved, it can then be resumed only by the same two players. A game save in Chess Hero represents
	 * the state of a specific game before that game was closed. The state can only be used to resume a specific game and cannot be used to start a separate
	 * independent instance of the game.
	 * Since a game is closed once saved, your opponent will need to confirm that they are willing to perform this action as well.
	 * After you send a save game request, the game enters a paused state and a push is sent to your opponent to notify them that they have to respond to the request.
	 * During the paused state, the server will not accept any request from your opponent other than a save game request to accept or decline.
	 * If they decline the request, the game will continue normally. If they accept it, the game will be saved, after which it will be closed.
	 * Furthermore, this is a blocking request, meaning that you do not receive a response until the save game request is resolved. This is apparent only to the
	 * player that initiates the save game routine. Any requests sent during the block will be processed after the save game request is resolved
	 * Parameters:
	 * - [gameid:INT] - The id of the game to save
	 * - [save:BOOL] - Optional. True if you want to save the game, false if not. If not present, the default value is true.
	 * Obviously sending false is a no-op when trying to initiate the save game routine. However, when responding to your opponent's request, this is how
	 * you can decline it
	 * Returns:
	 * - [saved:BOOL] - True if the game was saved and closed, false if not
	 * </pre>
	 */
	public static final int SAVE_GAME = 9;

	/**
	 * <pre>
	 * Delete a game that was previously saved. You can only delete saves for games that you have played in
	 * Parameters:
	 * - [gameid:INT] - The id of the game to delete
	 * </pre>
	 */
	public static final int DELETE_SAVED_GAME = 10;

	/**
	 * <pre>
	 * Enter a previously saved game. The game resumes after both players enter it. After a game is resumed, the save gets deleted.
	 * Parameters:
	 * - [gameid:INT] - The id of the game to resume
	 * Returns:
	 * - [game:STR] - The game data encoded as a UTF-8 string
	 * - [chattoken:STR] - The chat token needed to authenticate with the chat server
	 * - [next:BOOL] - True if you are the next to make a move, false if not
	 * - [started:BOOL] - True if the game has started - the game starts when both players enter the game or in other words when you are the second player to enter it
	 * </pre>
	 */
	public static final int RESUME_GAME = 11;
}

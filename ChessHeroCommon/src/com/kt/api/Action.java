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
    // Each message from the server that is not a response to a request should have parameter [push:BOOL] which is always true.
    // The following are all action codes recognized by the server, their additional parameters required for each action,
    // and all additional parameters returned if the request has succeeded:

    // Login
    // Description: Authenticate an existing user
    // Parameters: [username:STR], [password:STR]
    // Returns: [username:STR], [userid:INT]
    public static final int LOGIN = 1;

    // Register
    // Description: Register a new user
    // Parameters: [username:STR], [password:STR]
    // Returns: [username:STR], [userid:INT]
    public static final int REGISTER = 2;

    // Create game
    // Description: Create a new game that would be available to all players
    // Parameters: [gamename:STR], [color:STR] - either 'black' or 'white', optional, default value is 'white' if not specified
    // Returns: [gameid:INT], [chattoken:STR]
    // Additional: If the user successfully creates a game, they should expect a push message
    // to be sent when another player joins the game. The message will contain the following parameters:
    // [opponentname:STR], [opponentid:INT]
    public static final int CREATE_GAME = 3;

    // Cancel game
    // Description: Cancel your created game. Only works for a game the current user has created and only if the game hasn't started yet
    // Parameters: [gameid:INT]
    public static final int CANCEL_GAME = 4;

    // Fetch games
    // Description: Fetch all games waiting for a second player to join
    // Parameters: [offset:INT] - optional, [limit:INT] - optional. Default offset is 0, default limit is 100
    // Returns: [games:ARR]
    // - Each element in games has: [gameid:INT], [gamename:STR], [playercolor:STR] (the color of the creator of the game)
    public static final int FETCH_GAMES = 5;

    // Join game
    // Description: Join a game waiting for a second player to join and start playing
    // Parameters: [gameid:INT]
    // Returns: [opponentname:STR], [opponentid:INT], [chattoken:STR]
    public static final int JOIN_GAME = 6;

    // Exit game
    // Parameters: [gameid:INT]
    // Description: Exit the current game. Whoever exits the game before its natural end is considered defeated
    public static final int EXIT_GAME = 7;
}

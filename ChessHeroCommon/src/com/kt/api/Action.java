package com.kt.api;

/**
 * Created by Toshko on 12/8/13.
 */
public class Action
{
    // About the notation used:
    // On the left side of the colon is the name of the parameter.
    // On the right side of the colon is the type of the parameter.

    // Each request to the server should always have at least one parameter [action:INT].
    // Each response from the server should have at least one parameter [result:INT].
    // Each message from the server that is not a response to a request should have parameter [push:BOOL] which is always true.
    // The following are all action codes recognized by the server, their additional parameters required for each action,
    // and all additional parameters returned if the request has succeeded:

    // Login
    // Parameters: [username:STR], [password:STR]
    // Returns: [username:STR], [userid:INT]
    public static final int LOGIN = 1;

    // Register
    // Parameters: [username:STR], [password:STR]
    // Returns: [username:STR], [userid:INT]
    public static final int REGISTER = 2;

    // Create a game
    // Parameters: [gamename:STR]
    // Returns: [gameid:INT]
    public static final int CREATE_GAME = 3;

    // Cancel a pending game
    // Parameters: [gameid:INT]
    public static final int CANCEL_GAME = 4;

    // Fetch games waiting for a player
    // Parameters: [offset:INT], [limit:INT]
    // Returns: [games:ARR]
    // - Each element in games has: [gameid:INT], [gamename:STR]
    public static final int FETCH_GAMES = 5;

    // Join a game
    // Parameters: [gameid:INT]
    // Returns: [chattoken:STR]
    public static final int JOIN_GAME = 6;
}

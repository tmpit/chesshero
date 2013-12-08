package com.kt.api;

/**
 * Created by Toshko on 12/8/13.
 */
public class Action
{
    // About the notation used:
    // On the left side of the arrow is the actual name of the parameter that should be used.
    // On the right side of the arrow is the type of value the server will expect.

    // Each request to the server should always have at least one parameter 'action' => [INT].
    // Each response from the server should have at least one parameter 'result' => [INT].
    // Each message from the server that is not a response to a request should have parameter 'push' => [BOOL] which is always true.
    // The following are all action codes recognized by the server, their additional parameters required for each action,
    // and all additional parameters returned if the request has succeeded:

    // Login
    // Parameters: 'username' => [STR], 'password' => [STR]
    // Returns: 'username' => [STR], 'userid' => [INT]
    public static final int LOGIN = 1;

    // Register
    // Parameters: 'username' => [STR], 'password' => [STR]
    // Returns: 'username' => [STR], 'userid' => [INT]
    public static final int REGISTER = 2;

    // Create a game
    // Parameters: 'gamename' => [STR]
    // Returns: 'gameid' => [INT]
    public static final int CREATE_GAME = 3;

    // Cancel a pending game
    // Parameters: 'gameid' => [INT]
    public static final int CANCEL_GAME = 4;
}

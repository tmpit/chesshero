package com.kt.api;

/**
 * Created by Toshko on 12/8/13.
 */
public class Action
{
    // About the notation:
    // On the left side of the arrow is the actual name of the parameter that should be used.
    // On the right side of the arrow is the type of value the server will expect

    // Login
    // Parameters: username => [STR], password => [STR]
    public static final int LOGIN = 1;

    // Register
    // Parameters: username => [STR], password => [STR]
    public static final int REGISTER = 2;

    // Create a game
    // Parameters: gamename => [STR]
    public static final int CREATE_GAME = 3;

    // Cancel a pending game
    // Parameters: gameid => [INT]
    public static final int CANCEL_GAME = 4;
}

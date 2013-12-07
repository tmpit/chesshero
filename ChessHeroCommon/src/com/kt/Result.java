package com.kt;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/17/13
 * Time: 1:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Result
{
    public static final int OK = 0;

    public static final int AUTH_REQUIRED = 100;        // Any request / The attempted action requires you to be logged in
    public static final int INVALID_TYPE = 101;         // Any request / Invalid message type
    public static final int INVALID_NAME = 102;         // Registration / The username is invalid (e.g. too short/long etc.)
    public static final int INVALID_PASS = 103;         // Registration / The password is invalid (e.g. too short/long etc.)
    public static final int INVALID_CREDENTIALS = 104;  // Login / The username/password combination is incorrect or the user does not exist
    public static final int USER_EXISTS = 105;          // Registration / A user with that name already exists
    public static final int BAD_USER = 106;             // Registration / The server doesn't like you
    public static final int ALREADY_LOGGEDIN = 107;     // Registration, Login / You have already logged in
    public static final int ALREADY_PLAYING = 108;      // Create game, Join game / You are already playing or waiting for player to join
    public static final int NOT_PLAYING = 109;          // Cancel game / You cannot cancel a game because you are not in a game
    public static final int CANCEL_NA = 110;            // Cancel game / Cancel is not applicable (e.g. you are already paired with a player)
    public static final int INVALID_GAME_ID = 111;      // Cancel game / Invalid game id provided

    public static final int INVALID_MESSAGE = 400;      // Any request / Invalid message format has been sent

    public static final int INTERNAL_ERROR = 500;       // Any request / ...
}

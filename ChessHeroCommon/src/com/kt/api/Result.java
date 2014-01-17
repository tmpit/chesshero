package com.kt.api;

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


    public static final int AUTH_REQUIRED = 100;        		// Any request / The attempted action requires you to be logged in
    public static final int INVALID_NAME = 101;         		// Registration / The username is invalid (e.g. too short/long etc.)
    public static final int INVALID_PASS = 102;         		// Registration / The password is invalid (e.g. too short/long etc.)
    public static final int INVALID_CREDENTIALS = 103;  		// Login / The username/password combination is incorrect or the user does not exist
    public static final int USER_EXISTS = 104;          		// Registration / A user with that name already exists
    public static final int BAD_USER = 105;             		// Registration / The server doesn't like you
    public static final int ALREADY_LOGGEDIN = 106;     		// Registration, Login / You have already logged in
    public static final int ALREADY_PLAYING = 107;      		// Create game, Join game / You are already playing or waiting for player to join
    public static final int INVALID_GAME_NAME = 108;    		// Create game / The game name is invalid (e.g. too short/long etc)
    public static final int NOT_PLAYING = 109;          		// Cancel game, Exit game / You cannot cancel/exit a game because you are not in a game
    public static final int CANCEL_NA = 110;            		// Cancel game / Cancel is not applicable (e.g. you are already paired with a player)
    public static final int INVALID_GAME_ID = 111;      		// Cancel game, Join game, Save game / Invalid game id provided
    public static final int GAME_OCCUPIED = 112;        		// Join game / Game cannot be joined because two players are already playing it
    public static final int DUPLICATE_PLAYER = 113;     		// Join game / Game cannot be joined because the same user has created the game
    public static final int EXIT_NA = 114;              		// Exit game / Exit is not applicable (e.g. game has not started yet)
	public static final int MOVE_NA = 115;						// Move / Move is not applicable (e.g. game has not started yet)
	public static final int SAVE_NA = 116;						// Save game / Save game is not applicable (e.g. game has not started yet)
	public static final int INVALID_TIMEOUT = 117;				// Create game / The timeout duration specified is invalid, check minimum and maximum values


	public static final int INVALID_MOVE_FORMAT = 200;			// Move / Invalid notation used - refer to Pure coordinate notation at http://chessprogramming.wikispaces.com/Algebraic+Chess+Notation
	public static final int NOT_YOUR_TURN = 201;				// Move / it is not your turn to make a move
	public static final int NO_CHESSPIECE = 202;				// Move / no chess piece specified
	public static final int NOT_YOUR_CHESSPIECE = 203;			// Move / attempting to move a chess piece that is not your own
	public static final int INVALID_MOVE = 204;					// Move / the chess piece you are trying to move cannot move to the specified position
	public static final int WRONG_MOVE = 205;					// Move / the king is in check and this move would not save him or the king would be in check if this move is executed
	public static final int MISSING_PROMOTION = 206;			// Move / you are moving a pawn to its highest rank but you have not specified promotion


	public static final int ACTION_DISABLED = 300;				// Any request / The action is temporarily disabled for you. Currently this can only happen when
																// your opponent has prompted you to save the game


    public static final int INVALID_REQUEST_FORMAT = 400;		// Any request / The request couldn't be parsed
	public static final int WRONG_REQUEST_FORMAT = 401;			// Any request / The server only recognizes requests wrapped in MAP format
    public static final int MISSING_PARAMETERS = 402;   		// Any request / Parameters are missing
    public static final int UNRECOGNIZED_ACTION = 403;  		// Any request / Unrecognized action code has been sent
    public static final int INVALID_PARAM = 404;        		// Any request / Wrong parameter type (e.g. server is expecting STR, client sends INT)


    public static final int INTERNAL_ERROR = 500;       		// Any request / ...
}

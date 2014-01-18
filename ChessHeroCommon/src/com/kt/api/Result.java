package com.kt.api;

/**
 * @author Todor Pitekov
 * @author Kiril Tabakov
 *
 * The Result class groups all result codes the game server can return
 */
public class Result
{
	/**
	 * The request has succeeded
	 */
    public static final int OK = 0;


	/**
	 * The attempted action requires you to be logged in. Can be returned on any request
	 */
    public static final int AUTH_REQUIRED = 100;

	/**
	 * The username provided is invalid (e.g. too short/long etc.). Can be returned on: registration
	 * @see com.kt.api.Action
	 */
    public static final int INVALID_NAME = 101;

	/**
	 * The password provided is invalid (e.t. too short/long etc>). Can be returned on: registration
	 * @see com.kt.api.Action
 	 */
    public static final int INVALID_PASS = 102;

	/**
	 * The username/password combination is incorrect or the user does not exist. Can be returned on: login
	 */
    public static final int INVALID_CREDENTIALS = 103;

	/**
	 * A user with the username provided already exists. Can be returned on: registration
	 */
    public static final int USER_EXISTS = 104;

	/**
	 * The game server doesn't like you. Can be returned on: registration
	 */
    public static final int BAD_USER = 105;

	/**
	 * You have already logged in and you are attempting to login as another user or register. Can be returned on: login, registration
	 */
    public static final int ALREADY_LOGGEDIN = 106;

	/**
	 * You are already in a game. Can be returned on: create game, join game, resume game
	 */
    public static final int ALREADY_PLAYING = 107;

	/**
	 * The game name provided is invalid (e.g. too short/long etc.). Can be returned on: create game
	 * @see com.kt.api.Action
	 */
    public static final int INVALID_GAME_NAME = 108;

	/**
	 * You are not in a game. Can be returned on: cancel game, exit game, move, save game
	 */
    public static final int NOT_PLAYING = 109;

	/**
	 * Game cancel is not applicable (e.g. you are already paired with a player and the game has started). Can be returned on: cancel game
	 */
    public static final int CANCEL_NA = 110;

	/**
	 * The game id provided is invalid. Can be returned on: cancel game, join game, exit game, save game, delete saved game, resume game
	 */
    public static final int INVALID_GAME_ID = 111;

	/**
	 * Game cannot be entered because it has already started. Can be returned on: join game, resume game
	 */
    public static final int GAME_OCCUPIED = 112;

	/**
	 * Game cannot be entered because the player in the game is the same user but on a different connection. Can be returned on: join game, resume game
	 */
    public static final int DUPLICATE_PLAYER = 113;

	/**
	 * Exit game is not applicable (e.g. game has not started yet etc.). Can be returned on: exit game
	 */
    public static final int EXIT_NA = 114;

	/**
	 * Move is not applicable (e.g. game has not started yet etc.). Can be returned on: move
	 */
	public static final int MOVE_NA = 115;

	/**
	 * Save game is not applicable (e.g. game has not started yet etc.). Can be returned on: save game
	 */
	public static final int SAVE_NA = 116;

	/**
	 * The timeout duration specified is invalid. Can be returned on: create game
	 * @see com.kt.api.Action
	 */
	public static final int INVALID_TIMEOUT = 117;


	/**
	 * Invalid notation used. Can be returned on: move
	 * @see com.kt.api.Action
	 */
	public static final int INVALID_MOVE_FORMAT = 200;

	/**
	 * It is not your turn to make a move. Can be returned on: move
	 */
	public static final int NOT_YOUR_TURN = 201;

	/**
	 * There is no chess piece at the specified starting position. Can be returned on: move
	 */
	public static final int NO_CHESSPIECE = 202;

	/**
	 * Attempting to move a chess piece that is not yours. Can be returned on: move
	 */
	public static final int NOT_YOUR_CHESSPIECE = 203;

	/**
	 * The chess piece you are attempting to move cannot move to the specified position. Can be returned on: move
	 */
	public static final int INVALID_MOVE = 204;

	/**
	 * The king is in check and this move would not save him or the king would be in check if this move is executed. Can be returned on: move
	 */
	public static final int WRONG_MOVE = 205;

	/**
	 * You are moving a pawn to its highest rank but you have not specified promotion. Can be returned on: move
	 */
	public static final int MISSING_PROMOTION = 206;


	/**
	 * The action is temporarily disabled for you. Can be returned on any request other than a save game request only when
	 * your opponent has prompted you to save the game
	 * @see com.kt.api.Action
	 */
	public static final int ACTION_DISABLED = 300;


	/**
	 * The request couldn't be parsed. Make sure the message format conforms to CHESCO. Can be returned on any request
	 * @see com.kt.chesco.CHESCO
	 */
    public static final int INVALID_REQUEST_FORMAT = 400;

	/**
	 * The server only recognizes requests wrapped in a MAP format. Can be returned on any request
	 * @see com.kt.chesco.CHESCO
	 */
	public static final int WRONG_REQUEST_FORMAT = 401;

	/**
	 * A parameter is missing. Can be returned on any request
	 * @see com.kt.api.Action
	 */
    public static final int MISSING_PARAMETERS = 402;

	/**
	 * An unrecognized action code has been sent. Can be returned on any request
	 * @see com.kt.api.Action
	 */
    public static final int UNRECOGNIZED_ACTION = 403;

	/**
	 * Wrong parameter type (e.g. server is expecting STR, client sends INT). Can be returned on any request
	 * @see com.kt.api.Action
	 */
    public static final int INVALID_PARAM = 404;


	/**
	 * Blame Todor Pitekov for not doing a better job writing the server. Can be returned on any request
	 */
    public static final int INTERNAL_ERROR = 500;
}

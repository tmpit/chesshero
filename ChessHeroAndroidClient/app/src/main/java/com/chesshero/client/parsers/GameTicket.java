package com.chesshero.client.parsers;

import com.kt.game.Color;

/**
 * Created by Toshko on 12/10/14.
 *
 * Instances describe a pending game. A pending game is a game waiting for players to join. Class is used by
 * {@code FetchGamesResponseParser} and {@code Client} classes
 */
public class GameTicket
{
	/**
	 * The id of the game. Needed to join the game
	 */
	public final Integer gameID;

	/**
	 * The name of the game as specified by the game's creator
	 */
	public final String gameName;

	/**
	 * The timeout of the game measured in minutes. Can be {@code null} if the game has no timeout
	 */
	public final Integer timeout;

	/**
	 * The user id of the player in the game
	 */
	public final Integer opponentID;

	/**
	 * The username of the player in the game
	 */
	public final String opponentName;

	/**
	 * The color of the player in the game
	 */
	public final Color opponentColor;

	public GameTicket(Integer gameID, String gameName, Integer timeout, Integer opponentID, String opponentName, Color opponentColor)
	{
		this.gameID = gameID;
		this.gameName = gameName;
		this.timeout = timeout;
		this.opponentID = opponentID;
		this.opponentName = opponentName;
		this.opponentColor = opponentColor;
	}
}

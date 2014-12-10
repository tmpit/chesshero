package com.chesshero.client.parsers;

import com.kt.game.Color;

/**
 * Created by Toshko on 12/10/14.
 */
public class GameTicket
{
	public final Integer gameID;
	public final String gameName;
	public final Integer timeout;

	public final Integer opponentID;
	public final String opponentName;
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

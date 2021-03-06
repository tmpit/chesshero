package com.kt.game;

/**
 * The GameClockEventListener interface is used by the game clock to signal listeners of game-clock-related events
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public interface GameClockEventListener
{
	/**
	 * Invoked on the thread of the game clock when a player has run out of time
	 * @param player The {@code Player} that has run out of time
	 */
	public void playerDidTimeout(Player player);
}

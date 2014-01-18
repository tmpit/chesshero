package com.kt.game;

import com.kt.utils.SLog;

import java.util.ArrayList;

/**
 * @author Todor Pitekov
 * @author Kiril Tabakov
 *
 * The GameClock's job is to keep track of the time each player in a game has played and to signal listeners
 * when a player has run out of time
 */
public class GameClock extends Thread
{
	private Game game;
	private long startTimeMillis = 0;
	private ArrayList<GameClockEventListener> listeners = new ArrayList<GameClockEventListener>(2);

	/**
	 * Initializes a newly created {@code GameClock} instance with a {@code Game} instance which players to
	 * keep track of
	 * @param game A {@code Game}
	 */
	public GameClock(Game game)
	{
		this.game = game;
	}

	/**
	 * Adds a game clock event listener
	 * @param listener An object implementing the {@code GameClockEventListener} interface
	 */
	public void addEventListener(GameClockEventListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Removes a game clock event listener
	 * @param listener An object implementing the {@code GameClockEventListener} interface
	 */
	public void removeEventListener(GameClockEventListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * Gets the unix timestamp of the server time the game has started (in milliseconds)
	 * @return A {@code long}
	 */
	public long getStartTimeMillis()
	{
		return startTimeMillis;
	}

	@Override
	public void run()
	{
		startTimeMillis = System.currentTimeMillis() - (game.turn.millisPlayed + game.turn.getOpponent().millisPlayed);

		long timeout = game.getTimeout() * 60 * 1000l;
		long runningMillis = 0;
		long idleMillis;
		long inactiveMillis = 0;
		Player player;
		boolean didTimeout;

		while (!this.isInterrupted())
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// The clock will get interrupted when the game ends
				return;
			}

			synchronized (game)
			{
				short state = game.getState();

				if (Game.STATE_PAUSED == state)
				{	// Far from accurate but good enough
					inactiveMillis = (System.currentTimeMillis() - startTimeMillis) - runningMillis;
					continue;
				}
				if (state != Game.STATE_ACTIVE)
				{
					return;
				}

				player = game.turn;
				runningMillis = System.currentTimeMillis() - startTimeMillis;
				idleMillis = runningMillis - (player.millisPlayed + player.getOpponent().millisPlayed + inactiveMillis);
				didTimeout = player.millisPlayed + idleMillis > timeout;
			}

			if (didTimeout)
			{
				for (GameClockEventListener listener : listeners)
				{
					listener.playerDidTimeout(player);
				}

				return;
			}
		}
	}
}

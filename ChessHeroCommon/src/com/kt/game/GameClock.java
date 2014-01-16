package com.kt.game;

import java.util.ArrayList;

/**
 * Created by Toshko on 1/15/14.
 */
public class GameClock extends Thread
{
	private Game game;
	private long startTimeMillis = 0;
	private ArrayList<GameClockEventListener> listeners = new ArrayList<GameClockEventListener>(2);

	public GameClock(Game game)
	{
		this.game = game;
	}

	public void addEventListener(GameClockEventListener listener)
	{
		listeners.add(listener);
	}

	public void removeEventListener(GameClockEventListener listener)
	{
		listeners.remove(listener);
	}

	public long getStartTimeMillis()
	{
		return startTimeMillis;
	}

	@Override
	public void run()
	{
		startTimeMillis = System.currentTimeMillis();

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

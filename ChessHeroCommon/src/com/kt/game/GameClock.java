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
		long runningMillis;
		long idleMillis;
		Player player;
		boolean didTimeout;

		while (!this.isInterrupted())
		{
			synchronized (game)
			{
				if (Game.STATE_FINISHED == game.getState())
				{	// The game has finished while waiting to acquire the lock
					return;
				}

				player = game.turn;
				runningMillis = System.currentTimeMillis() - startTimeMillis;
				idleMillis = runningMillis - (player.millisPlayed + player.getOpponent().millisPlayed);
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

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// The clock will get interrupted when the game ends
				return;
			}
		}
	}
}

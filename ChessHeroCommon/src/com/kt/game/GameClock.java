package com.kt.game;

import java.util.ArrayList;

/**
 * Created by Toshko on 1/15/14.
 */
public class GameClock extends Thread
{
	private Game game;
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

	@Override
	public void run()
	{
		long timeout = game.getTimeout() * 60 * 1000l;
		long startTime = System.currentTimeMillis();
		Player player;
		boolean didTimeout;

		while (!this.isInterrupted())
		{
			synchronized (game)
			{
				player = game.turn;
				didTimeout = game.turn.lastMoveTimestampMillis - startTime > timeout;
			}

			if (didTimeout)
			{
				for (GameClockEventListener listener : listeners)
				{
					listener.playerDidTimeout(player);
				}
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

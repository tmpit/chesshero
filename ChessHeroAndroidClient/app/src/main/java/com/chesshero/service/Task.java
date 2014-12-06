package com.chesshero.service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Toshko on 12/2/14.
 */
public abstract class Task implements Runnable
{
	private static final int STATE_INIT = 0;
	private static final int STATE_COMPLETED = 1;
	private static final int STATE_CANCELLED = 2;

	private AtomicInteger state = new AtomicInteger();

	private void complete()
	{
		state.compareAndSet(STATE_INIT, STATE_COMPLETED);
	}

	public void cancel()
	{
		state.compareAndSet(STATE_INIT, STATE_CANCELLED);
	}

	public boolean isCancelled()
	{
		return state.get() == STATE_CANCELLED;
	}

	@Override
	public void run()
	{
		boolean result = false;

		if (!isCancelled())
		{
			result = execute();
			complete();
		}

		onFinish(result && !isCancelled());
	}

	public abstract boolean execute();

	public abstract void onFinish(boolean success);
}

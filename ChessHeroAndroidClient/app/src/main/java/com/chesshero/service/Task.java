package com.chesshero.service;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Toshko on 12/2/14.
 */
public abstract class Task implements Runnable
{
	private AtomicBoolean completed = new AtomicBoolean(false);
	private AtomicBoolean cancelled = new AtomicBoolean(false);
	private Handler callbackHandler = null;

	public void setCallbackHandler(Handler handler)
	{
		callbackHandler = handler;
	}

	public Handler getCallbackHandler()
	{
		return callbackHandler;
	}

	public void cancel()
	{
		cancelled.set(true);
	}

	private void complete()
	{
		completed.set(true);
	}

	public boolean isCancelled()
	{
		return cancelled.get();
	}

	public boolean isCompleted()
	{
		return completed.get();
	}

	@Override
	public void run()
	{
		if (!isCancelled() && execute())
		{
			complete();
		}

		if (callbackHandler != null)
		{
			callbackHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					onFinish();
				}
			});
		}
		else
		{
			onFinish();
		}
	}

	public abstract boolean execute();

	public abstract void onFinish();
}

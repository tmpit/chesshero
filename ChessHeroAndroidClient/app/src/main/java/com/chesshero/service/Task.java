package com.chesshero.service;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Toshko on 12/2/14.
 *
 * An abstract class for a cancellable @{code Runnable} similar to an @{code AsyncTask}
 * in the sense that it can invoke its completion method on a different thread than the one it executes its task on
 */
public abstract class Task implements Runnable
{
	private AtomicBoolean completed = new AtomicBoolean(false);
	private AtomicBoolean cancelled = new AtomicBoolean(false);
	private Handler callbackHandler = null;

	/**
	 * Sets the handler the @{code onFinish()} method will be invoked on
	 * @param handler The handler the @{code onFinish()} method will be invoked on. If @{code null} provided,
	 *                the @{code onFinish()} method will be invoked on the thread this @{code Runnable} runs on
	 */
	public void setCallbackHandler(Handler handler)
	{
		callbackHandler = handler;
	}

	/**
	 * Gets the handler the @{code onFinish()} method will be invoked on
	 * @return The handler the @{code onFinish()} method will be invoked on
	 */
	public Handler getCallbackHandler()
	{
		return callbackHandler;
	}

	/**
	 * Cancels this task. Note that cancelling does not interrupt or exit this runnable's thread. It is up to the
	 * task's implementation to check if it is cancelled and stop working
	 */
	public void cancel()
	{
		cancelled.set(true);
	}

	private void complete()
	{
		completed.set(true);
	}

	/**
	 * Call to check if this task has been cancelled. A task is cancelled by calling the @{code cancel()} method
	 * @return @{code true} if this task is cancelled, @{code false} otherwise
	 */
	public boolean isCancelled()
	{
		return cancelled.get();
	}

	/**
	 * Call to check if this task has completed. A task is considered completed if it successfully runs its
	 * @{code execute()} method. Note that a task can be both cancelled and completed at the same time. This
	 * can happen, for example, if the task's implementation of its @{code execute()} method ignores its
	 * cancellation status and finishes successfully
	 * @return @{code true} if this task has completed, @{code false} otherwise
	 */
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

	/**
	 * Subclasses should place work inside this method. It is invoked only if the task is not cancelled
	 * before it is run on a thread
	 * @return @{code true} if the work was done successfully, @{code false} otherwise. If @{code true},
	 * the task will raise its @{code completed} flag
	 */
	public abstract boolean execute();

	/**
	 * Method is invoked after @{code execute()} or, if the @{code execute()} method is not called, it is
	 * called immediately after it runs on a thread. Subclasses should check task result in this method.
	 * This method is optionally called on a thread different than the one the @{code execute()} method runs on.
	 * If a @{code Handler} is provided in the @{code setCallbackHandler()} method, this method will be dispatched on
	 * the handler
	 */
	public abstract void onFinish();
}

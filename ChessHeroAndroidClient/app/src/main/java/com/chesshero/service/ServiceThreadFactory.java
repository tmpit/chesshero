package com.chesshero.service;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Toshko on 12/2/14.
 */
public class ServiceThreadFactory implements ThreadFactory
{
	@Override
	public Thread newThread(Runnable runnable)
	{
		Thread thread = new Thread(runnable);
		thread.setPriority(Thread.NORM_PRIORITY - 2);
		return thread;
	}
}

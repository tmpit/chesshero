package com.chesshero.event;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Toshko on 12/9/14.
 */
public class EventCenter
{
	private static EventCenter singleton = null;

	public synchronized static EventCenter getSingleton()
	{
		if (null == singleton)
		{
			singleton = new EventCenter();
		}

		return singleton;
	}

	private HashMap<String, HashSet<EventCenterObserver>> observersPerEvent = new HashMap<String, HashSet<EventCenterObserver>>();

	public void addObserver(EventCenterObserver observer, String eventName)
	{
		HashSet<EventCenterObserver> set = null;

		synchronized (observersPerEvent)
		{
			set = observersPerEvent.get(eventName);
		}

		if (null == set)
		{
			set = new HashSet<EventCenterObserver>();

			synchronized (observersPerEvent)
			{
				observersPerEvent.put(eventName, set);
			}
		}

		synchronized (set)
		{
			set.add(observer);
		}
	}

	public void removeObserver(EventCenterObserver observer, String eventName)
	{
		HashSet<EventCenterObserver> set = null;

		synchronized (observersPerEvent)
		{
			set = observersPerEvent.get(eventName);
		}

		if (null == set)
		{
			return;
		}

		synchronized (set)
		{
			set.remove(observer);

			if (set.isEmpty())
			{
				synchronized (observersPerEvent)
				{
					observersPerEvent.remove(eventName);
				}
			}
		}
	}

	public void postEvent(String eventName, Object userData)
	{
		HashSet<EventCenterObserver> set = null;

		synchronized(observersPerEvent)
		{
			set = observersPerEvent.get(eventName);
		}

		if (null == set)
		{
			return;
		}

		synchronized (set)
		{
			for (EventCenterObserver observer : set)
			{
				observer.eventCenterDidPostEvent(eventName, userData);
			}
		}
	}
}

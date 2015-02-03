package com.chesshero.event;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Toshko on 12/9/14.
 *
 * A very basic class implementing a one-to-many Observer pattern.
 * Observers register for events specified by a name and once an event with that name is posted, the @{code EventCenter}
 * invokes a callback on the observer object
 */
public class EventCenter
{
	private static EventCenter singleton = null;

	/**
	 * Gets the singleton instance of the @{code EventCenter}. The instance is lazy loaded on demand
	 * @return An instance of @{code EventCenter}
	 */
	public synchronized static EventCenter getSingleton()
	{
		if (null == singleton)
		{
			singleton = new EventCenter();
		}

		return singleton;
	}

	private HashMap<String, HashSet<EventCenterObserver>> observersPerEvent = new HashMap<String, HashSet<EventCenterObserver>>();

	/**
	 * Adds an observer to the event center for an event specified by a name
	 * @param observer An object implementing @{code EventCenterObserver} interface. Must not be @{code null}
	 * @param eventName The name of the event. Must not be @{code null}
	 */
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

	/**
	 * Removes an observer from the event center for an event specified by a name. The observer will still be notified
	 * for other events it has subscribed for
	 * @param observer An object implementing @{code EventCenterObserver} interface. Must not be @{code null}
	 * @param eventName The name of the event. Must not be @{code null}
	 */
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

	/**
	 * Posts an event described by a name
	 * @param eventName The name of the event. Must not be @{code null}
	 */
	public void postEvent(String eventName)
	{
		postEvent(eventName, null);
	}

	/**
	 * Posts an event described by a name and an object to be passed to the observers
	 * @param eventName The name of the event. Must not be @{code null}
	 * @param userData An object that will be passed to all observers of this event. Can be @{code null}
	 */
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

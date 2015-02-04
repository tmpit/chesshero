package com.chesshero.event;

/**
 * Created by Toshko on 12/9/14.
 *
 * Interface that must be implemented in order to observe events posted by an {@code EventCenter}.
 * @see com.chesshero.event.EventCenter
 */
public interface EventCenterObserver
{
	/**
	 * Invoked when an event was posted which this observer has subscribed for
	 * @param eventName The name of the event
	 * @param userData A custom object passed as part of the event. May be {@code null}
	 */
	public void eventCenterDidPostEvent(String eventName, Object userData);
}

package com.chesshero.event;

/**
 * Created by Toshko on 12/9/14.
 */
public interface EventCenterObserver
{
	public void eventCenterDidPostEvent(String eventName, Object userData);
}

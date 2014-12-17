package com.chesshero.service;

import java.util.HashMap;

/**
 * Created by Toshko on 11/26/14.
 */
public class ServiceRequest
{
	private final int action;
	private final HashMap<String, Object> parameters = new HashMap<String, Object>();
	private final boolean timeout;

	public ServiceRequest(int action)
	{
		this(action, true);
	}

	public ServiceRequest(int action, boolean timeout)
	{
		this.action = action;
		this.timeout = timeout;
		parameters.put("action", action);
	}

	public int getAction()
	{
		return action;
	}

	public boolean canTimeout()
	{
		return timeout;
	}

	protected HashMap<String, Object> getParameters()
	{
		return parameters;
	}

	public void addParameter(String name, String value)
	{
		parameters.put(name, value);
	}

	public void addParameter(String name, int value)
	{
		parameters.put(name, value);
	}

	public void addParameter(String name, boolean value)
	{
		parameters.put(name, value);
	}

	@Override
	public String toString()
	{
		return "<Request :: parameters = " + parameters.toString() + ">";
	}
}

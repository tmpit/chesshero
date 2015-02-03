package com.chesshero.service;

import java.util.HashMap;

/**
 * Created by Toshko on 11/26/14.
 *
 * The class describes a generic server request and acts as a builder for a raw request that can be written to a @{code CHESCOSocket}
 * @see com.kt.api.Action
 */
public class ServiceRequest
{
	private final int action;
	private final HashMap<String, Object> parameters = new HashMap<String, Object>();
	private final boolean timeout;

	/**
	 * Initialize a request with a specific action. The request can timeout
	 * @param action An action code as per @{code com.kt.api.Action}
	 */
	public ServiceRequest(int action)
	{
		this(action, true);
	}

	/**
	 * Designated initializer for the class
	 * @param action An action code as per @{code com.kt.api.Action}
	 * @param timeout Pass @{code true} if this request should be able to timeout, @{code false} otherwise
	 */
	public ServiceRequest(int action, boolean timeout)
	{
		this.action = action;
		this.timeout = timeout;
		parameters.put("action", action);
	}

	/**
	 * Gets the action associated with this request
	 * @return The action associated with this request
	 */
	public int getAction()
	{
		return action;
	}

	/**
	 * Call to check if this request can timeout
	 * @return @{code true} if this request can timeout, @{code false} otherwise
	 */
	public boolean canTimeout()
	{
		return timeout;
	}

	/**
	 * Gets all of the raw parameters for the request
	 * @return A @{code HashMap} containing all the request parameters
	 */
	protected HashMap<String, Object> getParameters()
	{
		return parameters;
	}

	/**
	 * Adds a @{code String} parameter to the request
	 * @param name The parameter name. Must not be @{code null}
	 * @param value The parameter value. Must not be @{code null}
	 */
	public void addParameter(String name, String value)
	{
		parameters.put(name, value);
	}

	/**
	 * Adds an integer parameter to the request
	 * @param name The parameter name. Must not be @{code null}
	 * @param value The parameter value
	 */
	public void addParameter(String name, int value)
	{
		parameters.put(name, value);
	}

	/**
	 * Adds a boolean parameter to the request
	 * @param name The parameter name. Must not be @{code null}
	 * @param value The parameter value
	 */
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

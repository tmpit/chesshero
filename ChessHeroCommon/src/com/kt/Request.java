package com.kt;

import java.util.HashMap;

/**
 * Created by Toshko on 12/8/13.
 */
public class Request
{
    private int action;
    private HashMap<String, Object> parameters = new HashMap<String, Object>();

    public Request(int action)
    {
        this.action = action;
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
}

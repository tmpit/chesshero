package com.chesshero.client;

import com.chesshero.service.ServiceRequest;
import com.kt.api.Action;

/**
 * Created by Toshko on 12/8/14.
 */
public class ServiceRequestFactory
{
	public static ServiceRequest createLoginRequest(String userName, String password)
	{
		ServiceRequest request = new ServiceRequest(Action.LOGIN);
		request.addParameter("username", userName);
		request.addParameter("password", password);
		return request;
	}
}

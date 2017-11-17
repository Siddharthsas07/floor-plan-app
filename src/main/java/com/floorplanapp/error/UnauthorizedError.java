package com.floorplanapp.error;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UnauthorizedError 
{
	private String message;

	public UnauthorizedError() {}
	
	public UnauthorizedError(String message) 
	{
		super();
		this.message = message;
	}

	public String getError() 
	{
		return message;
	}

	public void setError(String message) 
	{
		this.message = message;
	}
	
	
}

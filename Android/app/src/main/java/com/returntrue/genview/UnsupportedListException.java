package com.returntrue.genview;
/**
 * 
 * @author JosephWang
 */
public class UnsupportedListException extends Exception 
{
	private static final long serialVersionUID = 1L;
	private String mistake;

	public UnsupportedListException() 
	{
		super();
	    mistake = "Only support List, Map, and basic data type";
	}
	
	public UnsupportedListException(String err)
	{
		super(err);     // call super class constructor
	    mistake = err;  // save message
	}
	
	public String getError() 
	{
		return mistake;
	}
}


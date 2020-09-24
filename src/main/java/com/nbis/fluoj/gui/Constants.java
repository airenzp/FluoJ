package com.nbis.fluoj.gui;

public class Constants {
	
	public static String delete_last_group_msg = "There it must be at least one group defined.";

	public static String getAlreadyExistsFieldMsg(String field, String value)
	{
		return field + " " + value 	+ " already exists";
	}
	
	public static String getEmptyFieldMsg(String field)
	{
		return "Must specify " + field;
	}
	
	public static String getReferencedFieldMsg(String field, String value)
	{
	    return String.format("Must remove references to %s %s before.", field, value);
	}

	public static String getIllegalValueMsg(String field, String value, String reason)
	{
		return String.format("Illegal value %s on %s. %s.", value, field, reason);
	}
}

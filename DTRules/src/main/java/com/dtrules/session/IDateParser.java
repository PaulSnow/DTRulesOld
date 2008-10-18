package com.dtrules.session;

import java.util.Date;

public interface IDateParser {
	/**
	 * Convert a string to a date
	 * @param s
	 * @return
	 */
	public abstract Date getDate( String s);

}
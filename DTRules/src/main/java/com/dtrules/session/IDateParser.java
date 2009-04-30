package com.dtrules.session;

import java.util.Date;

public interface IDateParser {
	/**
	 * Convert a string to a date.  On failure to do so, a null is returned.
	 * @param s
	 * @return
	 */
	public abstract Date getDate( String s);

}
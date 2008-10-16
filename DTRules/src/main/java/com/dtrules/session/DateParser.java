package com.dtrules.session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser implements IDateParser {

	    public  Pattern [] patterns ={
	        Pattern.compile("[01]\\d/[0123]\\d/\\d\\d\\d\\d"),
	        Pattern.compile("[01]?\\d-[0123]?\\d-\\d\\d\\d\\d"),
	        Pattern.compile("\\d\\d\\d\\d/[01]?\\d/[0123]?\\d"),
	        Pattern.compile("\\d\\d\\d\\d-[01]?\\d-[0123]?\\d"),
	    };
	    
	    public  SimpleDateFormat [] formats ={
	            new SimpleDateFormat("MM/dd/yyyy"),
	            new SimpleDateFormat("MM-dd-yyyy"),
	            new SimpleDateFormat("yyyy/MM/dd"),
	            new SimpleDateFormat("yyyy-MM-dd"),
	        };
	    
	    /* (non-Javadoc)
		 * @see com.dtrules.session.IDateParser#getDate(com.dtrules.session.IRSession, java.lang.String)
		 */
	    public  Date getDate( String s){
	    	s = s.trim();
	        for(int i=0;i<patterns.length;i++){
	            Matcher matcher = patterns[i].matcher(s);
	            if(matcher.matches()){
	                try {
	                    Date d = formats[i].parse(s);
	                    return d;
	                } catch (ParseException e) { } // Didn't work? just try again
	            }
	        }
	        return null;   
	    }
}

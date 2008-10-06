/*  
 * $Id$   
 *  
 * Copyright 2004-2007 MTBJ, Inc.  
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");  
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at  
 *   
 *      http://www.apache.org/licenses/LICENSE-2.0  
 *   
 * Unless required by applicable law or agreed to in writing, software  
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
 * See the License for the specific language governing permissions and  
 * limitations under the License.  
 */  
  
package com.dtrules.interpreter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dtrules.infrastructure.RulesException;
import com.dtrules.session.DTState;

public class RTime extends ARObject {
    final Date time;
    public int type() {
        return iTime;
    }
    
    @Override
    public double doubleValue() throws RulesException {
        return time.getTime();
    }

    @Override
    public long longValue() throws RulesException {
        return  time.getTime();
    }

    @Override
    public RDouble rDoubleValue() throws RulesException {
        return  RDouble.getRDoubleValue(time.getTime());
    }


    @Override
    public RInteger rIntegerValue() throws RulesException {
        return  RInteger.getRIntegerValue(time.getTime());
    }

    @Override
    public RString rStringValue() {
        return RString.newRString(stringValue());
    }



    private RTime(Date t){
        time = t;
    }
    public static Pattern [] patterns ={
        Pattern.compile("[01]\\d/[0123]\\d/\\d\\d\\d\\d"),
        Pattern.compile("[01]\\d-[0123]\\d-\\d\\d\\d\\d"),
        Pattern.compile("\\d\\d\\d\\d/[01]\\d/[0123]\\d"),
        Pattern.compile("\\d\\d\\d\\d-[01]\\d-[0123]\\d"),
    };
    
    public static SimpleDateFormat [] formats ={
            new SimpleDateFormat("MM/dd/yyyy"),
            new SimpleDateFormat("MM-dd-yyyy"),
            new SimpleDateFormat("yyyy/MM/dd"),
            new SimpleDateFormat("yyyy-MM-dd"),
        };
    
    public static Date getDate(String s){
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
    
    /**
     * Returns a Null if no valid RDate can be parsed from
     * the string representation
     * @param s
     * @return
     */
    public static RTime getRDate(String s) throws RulesException {
        Date d = getDate(s);
        if(d==null){
            throw new RulesException("Bad Date Format","getRDate","Could not parse: '"+s+"' as a Date or Time value");
        }
        return new RTime(d);
    }
    
    public static RTime getRTime(Date t){
        return new RTime(t);
    }

    @Override
    public String toString(){
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        return f.format(time);
    }
    
    public String stringValue() {
        return time.toString();
    }

    public int getYear(DTState state) throws RulesException{
        if(time==null){
            throw new RulesException("Undefined", "getYear()", "No valid date available");
        }
        state.calendar.setTime(time);
        return state.calendar.get(Calendar.YEAR);
    }
    /**
     * Returns self.
     */
    @Override
    public RTime rTimeValue() throws RulesException {
        return this;
    }

    /**
     * Returns the time's date object.
     */
    @Override
    public Date timeValue() throws RulesException {
        return time;
    }

    /**
     * returns 0 if both are equal. -1 if this object is less than the argument. 
     * 1 if this object is greater than the argument
     */
    @Override
    public int compare(IRObject irObject) throws RulesException {
    	return this.time.compareTo(irObject.timeValue());
    }

	@Override
	public boolean equals(IRObject o) throws RulesException {
		return time.equals(o.timeValue());
	}
    
    
}

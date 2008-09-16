/**
 * 
 */
package com.dtrules.example2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RInteger;
import com.dtrules.interpreter.RString;

/**
 * @author ps24876
 *
 */
public class Dumbtest {

    static public void tryDates(Date date1, Date date2){
        
        Calendar calendar = Calendar.getInstance();
        
        if(date1.after(date2)){
            Date hold = date1;
            date1 = date2;
            date2 = hold;
        }
        
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
        
        calendar.setTime(date1);
        int y1 = calendar.get(Calendar.YEAR);
        int m1 = calendar.get(Calendar.MONTH);
        int d1 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(date2);
        int y2 = calendar.get(Calendar.YEAR);
        int m2 = calendar.get(Calendar.MONTH);
        int d2 = calendar.get(Calendar.DAY_OF_MONTH);
        int yeardiff = y2-y1;
        if(m2<m1)yeardiff--;
        int monthdiff = m2-m1;
        if(d2<d1-1)monthdiff--;
        if(monthdiff < 0)monthdiff +=12;
        monthdiff += 12*yeardiff;  
        System.out.println(f.format(date1)+"-"+f.format(date2)+" -- "+RInteger.getRIntegerValue(monthdiff));
     }

static void TryRegularExpressions () {
     String   pattern = "[\\s]*,[\\s]*";
     String   v       = ",   this is,, a , test , of, this ting, \tha\r,one,\n,more,\r\ntime.   ";
     String[] results;
     results = v.trim().split(pattern);
     for(String s : results){
         System.out.println("'"+s+"'");
     }

 }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        tryDates(new Date("12/5/2006"),new Date("1/3/2008"));
        tryDates(new Date("1/5/2007"),new Date("2/3/2008"));
        tryDates(new Date("1/5/2007"),new Date("3/3/2008"));
        tryDates(new Date("1/3/2007"),new Date("1/1/2008"));
        tryDates(new Date("1/5/2007"),new Date("3/5/2008"));
    }

}

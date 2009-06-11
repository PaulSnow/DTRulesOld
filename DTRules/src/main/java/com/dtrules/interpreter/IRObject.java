/** 
 * Copyright 2004-2009 DTRules.com, Inc.
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
 **/
  
package com.dtrules.interpreter;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.dtrules.entity.IREntity;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.mapping.XMLNode;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;

@SuppressWarnings({"unchecked"})
public interface IRObject {

    public IRObject clone(IRSession s) throws RulesException;
    
	//  *************** NOTE !!!!!!
	//  You can't put static methods on an interface. So the String to integer conversion
	//  for types is a static method on the RSession class.
 	
	final String rBoolean       = "boolean",
	             rString        = "string",
	             rInteger       = "integer",
	             rFloat         = "float",
	             rEntity        = "entity",
	             rName          = "name",
	             rArray         = "array",
	             rDecisiontable = "decisiontable",
	             rNull          = "null",
	             rMark          = "mark",
	             rOperator      = "operator",
                 rTime          = "time",
                 rTable         = "table",
                 rXmlValue      = "xmlvalue";
	
	final String types[] = { rBoolean, rString, rInteger, rFloat,
			                 rEntity,  rName,   rArray,   rDecisiontable,  
			                 rNull,    rMark,   rOperator, rTime,
                             rTable, rXmlValue};
	
	final int    iBoolean       = 0,
	             iString        = 1,
	             iInteger       = 2,
	             iDouble        = 3,
	             iEntity        = 4,
	             iName          = 5,
	             iArray         = 6,
	             iDecisiontable = 7,
	             iNull          = 8,
	             iMark          = 9,
	             iOperator      = 10,
                 iTime          = 11,
                 iTable         = 12,
                 iXmlValue      = 13;
 	
	
	void execute(DTState state) throws RulesException;
	
	public IRObject getExecutable();
    
    public IRObject getNonExecutable();
    
    public boolean equals(IRObject o) throws RulesException;
    
    public boolean isExecutable();
    
    /**
     * By default, the toString() method for most Objects should provide
     * a representation of the object that can be used as the postFix value.
     * The stringValue should provide simply the data for the object.  Thus
     * If I want to append the a String and a Date to create a new string,
     * I should append the results from their stringValue() implementation.
     * <br><br>
     * If I needed the postfix (and thus the quotes and maybe other ) then
     * call postFix().  But if the postFix is going to match either stringValue()
     * or toString, it is going to match toString().
     * <br><br>
     * @return
     */
    public String   stringValue();
    public RString  rStringValue();
    
    public String postFix();
    
    public int type();
	
    public IRObject rclone();
    
    public XMLNode              xmlTagValue()   throws RulesException;
    public long                 longValue ()    throws RulesException;
    public int                  intValue ()     throws RulesException;
    public RInteger             rIntegerValue() throws RulesException;
    public double               doubleValue ()  throws RulesException;
    public RDouble              rDoubleValue()  throws RulesException;
    public ArrayList<IRObject>  arrayValue ()   throws RulesException;
    public RArray               rArrayValue()   throws RulesException;
    public boolean              booleanValue () throws RulesException;
    public RBoolean             rBooleanValue() throws RulesException;
    public HashMap              hashMapValue () throws RulesException;
    public IRObject             rXmlValue ()    throws RulesException; // Because it can return an RNull
    public IREntity             rEntityValue () throws RulesException;
    public RName                rNameValue ()   throws RulesException;
    public RTime                rTimeValue ()   throws RulesException;
    public Date                 timeValue ()    throws RulesException;
    public RTable               rTableValue()   throws RulesException;
    public HashMap              tableValue()    throws RulesException;
    public int                  compare(IRObject irObject) throws RulesException;
}

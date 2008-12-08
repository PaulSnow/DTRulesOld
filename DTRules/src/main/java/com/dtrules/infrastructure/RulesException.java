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
  
package com.dtrules.infrastructure;

import javax.rules.RuleException;

public class RulesException extends RuleException {
    String errortype;
	String location;
    String message;
    String decisionTable="";
    String postfix      = null;    
    String filename     = null;
    String section      = null;
    int    number;
    
    public void setPostfix(String s){
        if(postfix == null){
            postfix = s;
        }
    }
   
    public String getPostfix(){
        return postfix;
    }
    
    /**
     * Get the DecisionTable under execution at the time the error
     * occurred.  Returns a null if unknown.
     * @return
     */
    public String getDecisionTable() {
		return decisionTable;
	}
    
    /**
     * Set the decisionTable under execution when the error occurred.
     * @param decisionTable
     */
    public void addDecisionTable(String decisionTable, String filename) {
        if(filename==null)filename="";
    	if(this.decisionTable.length()==0){
    		this.decisionTable = decisionTable+" \n";
    	}else {
            this.decisionTable = this.decisionTable+"     called by: "+decisionTable+"   \t("+filename+")\n";
    	}
    	if(this.filename == null) this.filename = filename;
	}
    
    public RulesException(String type, String _location, String _message ){
		super("Location :"+_location+" type: "+type+" error: "+_message);
		location  = _location;
        errortype = type;
        message   = _message;
	}
    
	static final long serialVersionUID = 0;

    /**
     * Provide my view of a Rules Exception;
     */
    public String toString() {
       
        return 
          (decisionTable!="" ?  "\r\nDecision Table: "+decisionTable:"\r\n") +
                                  "File name:      "+filename+"\r\n"+
          ((section!= null)  ?   ("Section:        "+section + " " + number +"\r\n"):"")+  
          ((postfix!= null)  ?   ("Postfix:        "+postfix+"\r\n"):"")+
        	                      "Location:       '"+location+"'\r\n" +
        	                      "Type:           '"+errortype+"'\r\n" +
        	                      "Error:          '"+message+"'\r\n" ;
    }

    /**
     * @return the section
     */
    public String getSection() {
        return section;
    }

    /**
     * @param section the section to set
     */
    public void setSection(String section, int number) {
        if(this.section == null){
           this.section = section;
           this.number = number;
        }   
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }
    
    /**
     * This method allows us to add information to the message we have
     * about the context in which an error is thrown.
     * @param s
     */
    public void addToMessage(String s){
        message = message + "\r\n" + s;
    }
    
}

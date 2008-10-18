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
  
package com.dtrules.session;

import java.io.IOException;
import java.util.HashMap;

import com.dtrules.entity.REntity;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RName;
import com.dtrules.xmlparser.IGenericXMLParser;

@SuppressWarnings({"unchecked"})
public class EDDLoader implements IGenericXMLParser {

	final IRSession        session;
	final EntityFactory    ef;
	final String           filename;
    boolean                succeeded =  true;
	String                 errorMsgs =  "";
	int                    version   =  1;
	
	EDDLoader(String _filename, IRSession session, EntityFactory ef){
		this.session = session;
		this.ef = ef;
        filename = _filename;
	}
	
	/**
	 * If this string has a non-zero length, the EDD did not load
	 * properly.  The caller is responsible for checking this.  Otherwise
	 * the loader can only report a single error.
	 * 
	 * @return
	 */
	
	
	public String getErrorMsgs() {
        return errorMsgs;
    }




    public void beginTag(String[] tagstk, int tagstkptr, String tag,
		HashMap attribs) throws IOException, Exception {
        
        if(tag.equals("entity_data_dictionary") ){
            
            try{
                version = Integer.parseInt((String) attribs.get("version"));
            }catch(NullPointerException e){}   // Ignore any errors
            catch(Exception e){} 
        
        }else if(version == 2){
            beginTag2(tagstk,tagstkptr,tag,attribs);
        }    
	}

	public void endTag(String[] tagstk, 
			           int      tagstkptr, 
			           String   tag, 
			           String   body, 
			           HashMap  attribs) throws Exception, IOException {
	                 // 
	    
	    if(version==2){
	        
	        endTag2(tagstk,tagstkptr,tag,body,attribs);
	    
	    }else if(tag.equals("entity")){
		    
		  String entityname = (String) attribs.get("entityname");
		  String attribute  = (String) attribs.get("attribute");
		  String type       = (String) attribs.get("type");
		  String subtype    = (String) attribs.get("subtype");
		  String access     = (String) attribs.get("access");
		  String defaultv   = (String) attribs.get("default");
		  
		  boolean  writeable = true; 	// We need to convert access to a boolean
		  boolean  readable  = true;    // Make an assumption of r/w
		  int      itype     = -1;      // We need to convert the type to an int.
		  IRObject defaultO  = null;    // We need to convert the default into a Rules Engine Object.
		  
		  //First deal with the access thing... Compute the boolean we need.
		  if(access==null){				// Check to see if we need to handle TIERS EDD files.
			  access = (String) attribs.get("cdd_i_c_flag");
			  if(access!=null){
				  writeable = access.toLowerCase().indexOf("i")<0;
			  }
		  }else{						// Nope?  Then handle the more rational DTRules EDD files
			  writeable = access.toLowerCase().indexOf("w")>=0;
			  readable  = access.toLowerCase().indexOf("r")>=0;
			  if(!writeable && !readable){
			      errorMsgs +="\nThe attribute "+attribute+" has to be either readable or writable\r\n";
			      succeeded=false;
			  }
		  }

		  // Now the type.  An easy thing.
          try {
			itype = RSession.typeStr2Int(type,entityname,attribute);
		  } catch (RulesException e1) {
			errorMsgs+= e1.getMessage()+"\n";
			succeeded = false;
		  }
		  
		  // Now deal with the default specified.  
		  if (defaultv==null){			// Do we need to handle TIERS EDD files?
			  defaultv = (String) attribs.get("cdd_default_value");
		  }
          		  
          defaultO = EntityFactory.computeDefaultValue(session, ef, defaultv, itype) ;
          
		  RName  entityRName = RName.getRName(entityname.trim(),false);
		  RName  attributeRName = RName.getRName(attribute.trim(),false);
		  REntity entity = ef.findcreateRefEntity(false,entityRName);
          int    intType = -1;
          try {
			intType = RSession.typeStr2Int(type,entityname,attribute);
		  } catch (RulesException e) { 
			errorMsgs += "Bad Type: '"+type+"' encountered on entity: '"+entityname+"' attribute: '"+attribute+"' \n";
			succeeded = false;
		  }
		  String errstr  = entity.addAttribute(attributeRName,
		                                       defaultv, 
		                                       defaultO,
		                                       writeable,
		                                       readable,
		                                       intType,
		                                       subtype);
		  if(errstr!=null){
		      succeeded = false;
		      errorMsgs += errstr;
		  }
        }
	}

	public boolean error(String v) throws Exception {
		return true;
	}

	
	/** Support for the New EDD format **/
	
	String entityname;
	String entitycomment;
	String entityaccess;
    public void beginTag2(String[] tagstk, int tagstkptr, String tag,
            HashMap attribs) throws IOException, Exception {
        if(tag.equals("entity")){
            entityname      = (String) attribs.get("name");
            entitycomment   = (String) attribs.get("comment");
            entityaccess    = (String) attribs.get("access");
        }
    }

    public void endTag2(String[] tagstk, 
                           int      tagstkptr, 
                           String   tag, 
                           String   body, 
                           HashMap  attribs) throws Exception, IOException {
        if(!tag.equals("field")) return;
        
        
        String default_value  = (String) attribs.get("default_value");
        String attrib_name    = (String) attribs.get("name");
       // String attrib_comment = (String) attribs.get("comment");
        String access         = (String) attribs.get("access");
        String subtype        = (String) attribs.get("subtype");
	    String type           = (String) attribs.get("type");
	    
	    boolean writeable = access.toLowerCase().indexOf("w")>=0;
        boolean readable  = access.toLowerCase().indexOf("r")>=0;
        if(!writeable && !readable){
            errorMsgs +="\nThe attribute "+attrib_name+" has to be either readable or writable\r\n";
            succeeded=false;
        }
        
    int itype=-1;

    // Now the type.  An easy thing.
    try {
      itype = RSession.typeStr2Int(type,entityname,attrib_name);
    } catch (RulesException e1) {
      errorMsgs+= e1.getMessage()+"\n";
      succeeded = false;
    }
                
    IRObject defaultO = EntityFactory.computeDefaultValue(session, ef, default_value, itype) ;
    
    RName  entityRName = RName.getRName(entityname.trim(),false);
    RName  attributeRName = RName.getRName(attrib_name.trim(),false);
    REntity entity = ef.findcreateRefEntity(false,entityRName);
    int    intType = -1;
    try {
      intType = RSession.typeStr2Int(type,entityname,attrib_name);
    } catch (RulesException e) { 
      errorMsgs += "Bad Type: '"+type+"' encountered on entity: '"+entityname+"' attribute: '"+attrib_name+"' \n";
      succeeded = false;
    }
    String errstr  = entity.addAttribute(attributeRName,
                                         default_value, 
                                         defaultO,
                                         writeable,
                                         readable,
                                         intType,
                                         subtype);
    if(errstr!=null){
        succeeded = false;
        errorMsgs += errstr;
    }
    }
	
	
	
	
	
	
	
	
	
	
}

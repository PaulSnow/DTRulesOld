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
  
package com.dtrules.interpreter.operators;

import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.RNull;
import com.dtrules.interpreter.RString;
import com.dtrules.mapping.XMLNode;
import com.dtrules.session.DTState;

@SuppressWarnings("unchecked")
public class RXmlValueOps {
	    static {
	    	new SetXmlAttribute();
	    	new GetXmlAttribute();
	    }
	 
	    /**
	     * SetXmlAttribute ( XmlValue Attribute Value --> )
	     * Overwrites the attribute in the XML node.  If the object provided
	     * doesn't actually have an XmlValue, this becomes a no op.
	     * @author Paul Snow
	     *
	     */
		static class SetXmlAttribute extends ROperator {
			SetXmlAttribute(){super("setxmlattribute");}

			@Override
            public void execute(DTState state) throws RulesException {
				String    value     = state.datapop().stringValue();
				String    attribute = state.datapop().stringValue();
				XMLNode   xmlNode   = state.datapop().xmlTagValue();
				if(xmlNode != null){
				    state.traceInfo("SetXmlAttribute","tag='"+xmlNode.getTag()+"' attribute='"+attribute+"' value='"+value+"'");
				    xmlNode.getAttribs().put(attribute, value);
				}
			}
		}
		/**
         * GetXmlAttribute ( XmlValue Attribute --> Value )
         * Get the value of the given attribute from this XmlValue.
         * If the attribute is not defined, a null is returned.
         * @author Paul Snow
         *
         */
        static class GetXmlAttribute extends ROperator {
            GetXmlAttribute(){super("getxmlattribute");}

            @Override
            public void execute(DTState state) throws RulesException {
                String    attribute = state.datapop().stringValue();
                XMLNode   xmlNode    = state.datapop().xmlTagValue();
                
                String    value     = (String) xmlNode.getAttribs().get(attribute);
                if(value != null ){
                   state.datapush(RString.newRString(value)); 
                   state.traceInfo("GetXmlAttribute","tag='"+xmlNode.getTag()+"' attribute='"+attribute+"' value='"+value+"'");
                }else{
                   state.datapush(RNull.getRNull()); 
                   state.traceInfo("GetXmlAttribute","tag='"+xmlNode.getTag()+"' attribute='"+attribute+"' null='true'");
                }
            }
        }
		
        
}
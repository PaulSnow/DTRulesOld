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
 

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.dtrules.entity.IREntity;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.mapping.XMLNode;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;

public abstract class ARObject implements IRObject {

    /**
     * No point in implementing this here.  Every Object that has
     * an array representation needs to implement it themselves.
     */
    public RArray rArrayValue() throws RulesException {
       throw new RulesException("Conversion Error","ARObject","No Array Value value exists for "+this.stringValue());
    }

    public RBoolean rBooleanValue() throws RulesException {
        return RBoolean.getRBoolean(booleanValue());
    }

    public RDouble rDoubleValue() throws RulesException {
        return RDouble.getRDoubleValue(doubleValue());
    }

    public RTime rTimeValue() throws RulesException {
        return RTime.getRTime(timeValue());
    }

    public Date timeValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Time value exists for: "+this);
    }

	public void execute(DTState state) throws RulesException {
		state.datapush(this);
	}

    public IRObject getExecutable(){
    	return this;
    }
    
    public IRObject getNonExecutable() {
    	return this;
    }

	public boolean equals(IRObject o) throws RulesException {
		return o==this;
	}

	public boolean isExecutable() {
		return false;
	}

	public String postFix() {
		return toString();
	}
	
    public RString rStringValue() {
        return RString.newRString(stringValue());
    }
    
	public IRObject rclone() {
		return (IRObject) this;
	}

	/** Conversion Methods.  Default is to throw a RulesException **/
	
    public int intValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Integer value exists for "+this);
    }

	public ArrayList<IRObject> arrayValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Array value exists for "+this);
	}

	public boolean booleanValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Boolean value exists for "+this);
	}

	public double doubleValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No double value exists for "+this);
	}

	public IREntity rEntityValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Entity value exists for "+this);
	}
	@SuppressWarnings({"unchecked"})
	public HashMap hashMapValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Integer value exists for "+this);
	}

	public long longValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Long value exists for "+this);
	}

	public RName rNameValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Integer value exists for "+this);
	}

	public RInteger rIntegerValue() throws RulesException {
        throw new RulesException("Undefined","Conversion Error","No Integer value exists for "+this);
	}

	public int compare(IRObject irObject) throws RulesException {
        throw new RulesException("Undefined","No Supported","Compared Not suppoted for this object"+this);
	}

    /**
     * By default, objects clone themselves by simply returnning themselves.
     * This is because the clone of a number or boolean etc. is itself.
     */
    public IRObject clone(IRSession s) throws RulesException {
        return this;
    }

    /* (non-Javadoc)
     * @see com.dtrules.interpreter.IRObject#rTableValue()
     */
    public RTable rTableValue() throws RulesException {
        throw new RulesException("Undefined","Not Supported","No Table value exists for "+this);
    }

    /* (non-Javadoc)
     * @see com.dtrules.interpreter.IRObject#tableValue()
     */
    @SuppressWarnings({"unchecked"})
    public HashMap tableValue() throws RulesException {
        throw new RulesException("Undefined","Not Supported","No Table value exists for "+this);
    }

    /* (non-Javadoc)
     * @see com.dtrules.interpreter.IRObject#rXmlValue()
     */
    public IRObject rXmlValue() throws RulesException {
        return RNull.getRNull();
    }
    
    /**
     * If no XMLNode exists for this object, a null is returned.
     */
    public XMLNode xmlTagValue() throws RulesException {
        return null;
    }    
    
}

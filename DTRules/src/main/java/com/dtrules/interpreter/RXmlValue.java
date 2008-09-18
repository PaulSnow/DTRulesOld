/**
 * 
 */
package com.dtrules.interpreter;

import java.util.ArrayList;
import java.util.Date;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.mapping.XMLNode;
import com.dtrules.session.DTState;

/**
 * @author paul snow
 * 
 * An XmlValue object represents a node in the XML input stream that
 * supplied data to the Rules Engine.  It provides a way to update
 * and modify that XML based on rules defined in Decision Tables.
 *
 */
public class RXmlValue extends ARObject {
    XMLNode tag;
    DTState state;
    int     id;
    
    public RXmlValue(DTState state, XMLNode tag){
        this.tag = tag;
        id = state.getSession().getUniqueID();
    }
    
    /**
     * Sets the value of an Attribute on the tag for this RXmlValue.
     *  
     * @param attribute
     * @param value
     */
    public void setAttribute(String attribute, String value){
       tag.getAttribs().put(attribute, value);
    }
    
    /**
     * Gets the value of an Attribute on the tag for this RXmlValue.
     * Returns a null if the Attribute isn't defined on this tag. 
     * @param attribute
     * @param value
     */
    public String getAttribute(String attribute){
       if(!tag.getAttribs().containsKey(attribute)){
           return null;
       }
       return tag.getAttribs().get(attribute).toString();
    }
    
    /**
     * The following are all the accessors that are suppored
     * for working with RXmlValue objects
     */
    
    /**
     * The string value of an XMLTag is its body value
     */
    public String stringValue() {
        return tag.getBody().toString();
    }
    public int type() {
        return iXmlValue;
    }

    public ArrayList<IRObject> arrayValue() throws RulesException {
        ArrayList<IRObject> a = new ArrayList<IRObject>();
        if(tag.getTags().size()>0){
           for(XMLNode t : tag.getTags()){
               a.add(new RXmlValue(state,t));
           }
        }
        return a;
    }

    @Override
    public boolean booleanValue() throws RulesException {
        return RBoolean.booleanValue(tag.getBody().toString());
    }

    @Override
    public double doubleValue() throws RulesException {
        return RDouble.getDoubleValue(tag.getBody().toString());
    }

    @Override
    public boolean equals(IRObject o) throws RulesException {
        return rStringValue().equals(o);
    }

    @Override
    public int intValue() throws RulesException {
        return (int)RInteger.getIntegerValue(tag.getBody().toString());
    }

   
    @Override
    public long longValue() throws RulesException {
        return RInteger.getIntegerValue(tag.getBody().toString());
          }

    @Override
    public RBoolean rBooleanValue() throws RulesException {
        return RBoolean.getRBoolean(booleanValue());
    }

    @Override
    public RDouble rDoubleValue() throws RulesException {
        return RDouble.getRDoubleValue(doubleValue());
    }

    @Override
    public RInteger rIntegerValue() throws RulesException {
        return RInteger.getRIntegerValue(longValue());
    }

    @Override
    public RName rNameValue() throws RulesException {
        return RName.getRName(stringValue(),false);
    }

    public RString rStringValue() {
        return RString.newRString(stringValue());
    }

    public RTime rTimeValue() throws RulesException {
        return RTime.getRTime(timeValue());
    }

    public Date timeValue() throws RulesException {
        return RTime.getDate(tag.getBody().toString());
    }

    /* (non-Javadoc)
     * @see com.dtrules.interpreter.ARObject#rXmlValue()
     */
    @Override
    public RXmlValue rXmlValue() throws RulesException {
        return this;
    }

    /* (non-Javadoc)
     * @see com.dtrules.interpreter.ARObject#xmlTagValue()
     */
    @Override
    public XMLNode xmlTagValue() throws RulesException {
        return tag;
    }

    
    
}

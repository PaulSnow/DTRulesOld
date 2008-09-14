/**
 * 
 */
package com.dtrules.mapping;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This object represents an XML tag, but I avoid serializaiton under
 * the assumption that any conversion to a string would have been 
 * undone by a conversion from a string back to the object anyway. I
 * also use the same structure for both tagged data and a tag holding
 * tags.
 * 
 * @author Paul Snow
 * Sep 24, 2007
 *
 */
public class XMLTag {
    String                  tag;
    HashMap<String, Object> attribs = new HashMap<String,Object>();
    ArrayList<XMLTag>       tags    = new ArrayList<XMLTag>();
    Object                  body    = null;
    XMLTag                  parent;
    
    public XMLTag(String tag, XMLTag parent){
        this.tag    = tag;
        this.parent = parent;
    }
    
    public String toString(){
        String r = "<"+tag;
        for(String key : attribs.keySet()){
            r +=" "+key +"='"+attribs.get(key).toString()+"'";
        }
        if(body != null){
            String b = body.toString();
            if(b.length()>20){
                b = b.substring(0,18)+"...";
            }
            r +=">"+b+"</"+tag+">";
        }else if( tags.size()==0 ){
           r += "/>";
        }else{
           r += ">";
        }
        return r;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return the tags
     */
    public ArrayList<XMLTag> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(ArrayList<XMLTag> tags) {
        this.tags = tags;
    }

    /**
     * @return the body
     */
    public Object getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(Object body) {
        this.body = body;
    }

    /**
     * @return the parent
     */
    public XMLTag getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(XMLTag parent) {
        this.parent = parent;
    }

    /**
     * @return the attribs
     */
    public HashMap<String, Object> getAttribs() {
        return attribs;
    }
    
    
}
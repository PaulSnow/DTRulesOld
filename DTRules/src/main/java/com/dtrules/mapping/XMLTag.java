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
public class XMLTag implements XMLNode {
    String                  tag;
    HashMap<String, Object> attribs = new HashMap<String,Object>();
    ArrayList<XMLNode>      tags    = new ArrayList<XMLNode>();
    Object                  body    = null;
    XMLNode                 parent;
    
    public XMLTag(String tag, XMLNode parent){
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
    
    /* (non-Javadoc)
     * @see com.dtrules.mapping.XMLNode#addChild(com.dtrules.mapping.XMLNode)
     */
    @Override
    public void addChild(XMLNode node) {
       tags.add(node);
    }
    
    /* (non-Javadoc)
     * @see com.dtrules.mapping.XMLNode#remove(com.dtrules.mapping.XMLNode)
     */
    @Override
    public void remove(XMLNode node){
        tags.remove(node);
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
    public ArrayList<XMLNode> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(ArrayList<XMLNode> tags) {
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
    public XMLNode getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(XMLTag parent) {
        if(this.parent != null){
            this.parent.remove(this);
        }
        this.parent = parent;
    }

    /**
     * @return the attribs
     */
    public HashMap<String, Object> getAttribs() {
        return attribs;
    }
    
    public Type type(){ return Type.TAG; }
}
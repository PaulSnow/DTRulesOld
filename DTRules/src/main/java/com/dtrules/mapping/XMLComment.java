/**
 * 
 */
package com.dtrules.mapping;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Paul Snow
 *
 */
public class XMLComment implements XMLNode {
    static HashMap<String,Object> attribs = new HashMap<String,Object>();
    static ArrayList<XMLNode> tags = new ArrayList<XMLNode>();
    Object body;
    XMLNode parent;
    
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
    
    public HashMap<String, Object> getAttribs() {
        return attribs;
    }

    /* (non-Javadoc)
     * @see com.dtrules.mapping.XMLNode#getBody()
     */
    public Object getBody() {
        return body;
    }

    /* (non-Javadoc)
     * @see com.dtrules.mapping.XMLNode#getParent()
     */
    public XMLNode getParent() {
        return parent;
    }
        /* (non-Javadoc)
     * @see com.dtrules.mapping.XMLNode#getTag()
     */
    public String getTag() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.dtrules.mapping.XMLNode#getTags()
     */
    public ArrayList<XMLNode> getTags() {
       return tags;
    }

    /* (non-Javadoc)
     * @see com.dtrules.mapping.XMLNode#setBody(java.lang.Object)
     */
    public void setBody(Object body) {
        this.body=body;
    }

    /* (non-Javadoc)
     * @see com.dtrules.mapping.XMLNode#setParent(com.dtrules.mapping.XMLTag)
     */
    public void setParent(XMLTag parent) {
        this.parent = parent;
    }

    /**
     *  A comment cannot have a tag.
     */
    public void setTag(String tag) {
        
    }

    /**
     *  A comment cannot have tags
     * @see com.dtrules.mapping.XMLNode#setTags(java.util.ArrayList)
     */
    public void setTags(ArrayList<XMLNode> tags) {
       
    }

    public String toString(){
        return "<!--"+body+"-->";
    }
    
    public Type type(){ return Type.COMMENT; }
    
}

/**
 * 
 */
package com.dtrules.mapping;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ps24876
 *
 */
public interface XMLNode {

    public enum Type { TAG, COMMENT, HEADER } 
        
    public Type type ();
    
    public abstract String toString();

    /**
     * Adds the given XMLNode to the children of this tag
     */
    public abstract void addChild(XMLNode node);
    
    /**
     * Removes any reference to the given node from
     * this XML Node 
     */
    public abstract void remove(XMLNode node);
    /**
     * @return the tag
     */
    public abstract String getTag();

    /**
     * @param tag the tag to set
     */
    public abstract void setTag(String tag);

    /**
     * @return the tags
     */
    public abstract ArrayList<XMLNode> getTags();

    /**
     * @param tags the tags to set
     */
    public abstract void setTags(ArrayList<XMLNode> tags);

    /**
     * @return the body
     */
    public abstract Object getBody();

    /**
     * @param body the body to set
     */
    public abstract void setBody(Object body);

    /**
     * @return the parent
     */
    public abstract XMLNode getParent();

    /**
     * @param parent the parent to set
     */
    public abstract void setParent(XMLTag parent);

    /**
     * @return the attribs
     */
    public abstract HashMap<String, Object> getAttribs();

}
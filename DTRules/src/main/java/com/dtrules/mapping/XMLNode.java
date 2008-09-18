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

    public abstract String toString();

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
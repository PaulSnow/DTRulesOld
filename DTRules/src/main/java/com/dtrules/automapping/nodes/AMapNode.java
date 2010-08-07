/**
 * 
 */
package com.dtrules.automapping.nodes;

import java.util.ArrayList;
import java.util.List;

import com.dtrules.automapping.AutoDataMap;
import com.dtrules.automapping.LabelMap;
import com.dtrules.automapping.access.IAttribute;
import com.dtrules.xmlparser.XMLPrinter;

/**
 * @author ps24876
 *
 */
public abstract class AMapNode implements IMapNode {

    private IMapNode        parent;
    private IAttribute      attribute;
    private List<IMapNode>  children = new ArrayList<IMapNode>();
    private boolean         invalid;        // Set to true if this node is invalid
    
    public List<IMapNode> getChildren(){
        return children;
    }
    
    public void addChild(IMapNode child){
        children.add(child);
    }
    
    AMapNode(IAttribute attribute, IMapNode parent){
        this.attribute = attribute;
        this.parent    = parent;
        if(parent != null){
            parent.addChild(this);
        }
    }
    
    @Override
    public IMapNode getParent() {
        return parent;
    }

    @Override
    public void setParent(IMapNode parent) {
        this.parent = parent;
        if(parent != null){
            //parent.addChild(this);
        }
    }

    /**
     * @return the attribute
     */
    public IAttribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(IAttribute attribute) {
        this.attribute = attribute;
    }

    
    /**
     * @return the invalid
     */
    public boolean isInvalid() {
        return invalid;
    }

    /**
     * @param invalid the invalid to set
     */
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    /**
     * Back updates the value from the Rules Engine into
     * the source objects.  
     */
    @Override
    public void update(AutoDataMap autoDataMap) {
        List<IMapNode> children = getChildren();
        for(IMapNode child : children){
            child.update(autoDataMap);
        }
    }

}

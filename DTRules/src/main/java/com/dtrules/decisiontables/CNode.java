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
  
package com.dtrules.decisiontables;

import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.IRObject;
import com.dtrules.session.DTState;
/**
 * The Condition Node evaluates conditions within a Decision
 * table.
 * @author Paul Snow
 *
 */
public class CNode implements DTNode {

    final int            column;                  //Column that created this node
    final int            conditionNumber;         //NOPMD 
    final IRObject       condition;
    final RDecisionTable dtable;                  // Pointer back to the Decision Table for debug purposes

    DTNode iftrue  = null;
    DTNode iffalse = null;
    
    /**
     * Clone this CNode and all the CNodes referenced by this CNode
     */
    public CNode cloneDTNode (){
        CNode newCNode = new CNode(dtable,column,conditionNumber,condition);
        newCNode.iftrue  = iftrue  == null ? null : iftrue.cloneDTNode(); 
        newCNode.iffalse = iffalse == null ? null : iffalse.cloneDTNode();
        return newCNode;
    }
    
    public int getRow(){ return conditionNumber; }
    /**
     * Two CNodes are equal if their true paths and their
     * false paths are the same.
     * And those CommonANodes have to be equal.
     */
	public boolean equalsNode(DTNode node) {
        if(node.getClass().equals(CNode.class)){
            CNode cnode = (CNode)node;
            if(cnode.conditionNumber != conditionNumber){
                return false;
            }
            return(cnode.iffalse.equalsNode(iffalse) && 
               cnode.iftrue.equalsNode(iftrue));
        }else{
            ANode me  = getCommonANode();       // Get this CNode's commonANode.
            if(me==null)return false;           // If none exists, it can't be equal!
            ANode him = getCommonANode();       // Get the other DTNode's commonANode
            if(him==null)return false;          // If none exists, it can't be equal!
            return me.equalsNode(him);          // Return true if this node matches that node!
        }    
    }
	
    /**
     * To have a CommonANode, every path through the CNode (i.e. both
     * the true path and the false path) has to have the same commonANode.
     * So both iftrue and iffalse have to have a commonANode, and those have
     * to match.
     */
    public ANode getCommonANode() {
        ANode trueSide = iftrue.getCommonANode();           // Does the true side have a CommonANode
        if(trueSide==null)return null;                      // Nope? false!
        ANode falseSide = iffalse.getCommonANode();         // Does the false side have a CommonANode?
        if(falseSide==null)return null;                     // Nope? false!
        if(trueSide.equalsNode(falseSide))return trueSide;  // If they match, I just have to return one of them!
        return null;                                        // If they don't match, I have to return false!
    }

  
	
	CNode(RDecisionTable dt, int column, int conditionNumber, IRObject condition){
        this.column          = column;
        this.conditionNumber = conditionNumber; 
        this.condition       = condition;
        this.dtable          = dt;
    }
	
	public int countColumns(){
	    int t = iftrue.countColumns();
	    int f = iffalse.countColumns();
	    return t+f;
	}
	    
	public void execute(DTState state) throws RulesException{
        boolean result;
        try {
            if(state.testState(DTState.TRACE)){
                state.traceTagBegin("Condition", "n",(conditionNumber+1)+"");
                    state.traceInfo("Formal", dtable.getConditions()[conditionNumber]);
                    state.traceInfo("Postfix", dtable.getConditionsPostfix()[conditionNumber]);
                    state.traceTagBegin("execute");
                    result = state.evaluateCondition(condition);
                    state.traceTagEnd();
                    state.traceInfo("result", "v",(result?"Y":"N"),null);
                state.traceTagEnd();
            }else{
                result = state.evaluateCondition(condition);
            }
            
    		if(result){
    			iftrue.execute(state);
    		}else{
    			iffalse.execute(state);
    		}
    		
        } catch (RulesException e) {
            state.traceTagBegin("Condition", "n", (conditionNumber+1)+"");
            state.traceInfo("Condition_Text", dtable.conditions[conditionNumber]);
            e.setSection("Condition",conditionNumber+1);
            e.setFormal(dtable.getConditions()[conditionNumber]);
            throw e;
        }
	}

	public Coordinate validate() {
		if(iftrue  == null || iffalse == null){
           return new Coordinate(conditionNumber,column); 
        }   
	    Coordinate result = iffalse.validate();
        if(result!=null){
            return result;
        }
        
        return iftrue.validate();
	}

	public String toString(){
		return "Condition Number "+(conditionNumber+1);
	}
}

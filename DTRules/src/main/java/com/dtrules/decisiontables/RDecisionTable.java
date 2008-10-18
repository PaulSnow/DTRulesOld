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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dtrules.decisiontables.DTNode.Coordinate;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.ARObject;
import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RName;
import com.dtrules.interpreter.RNull;
import com.dtrules.interpreter.RString;
import com.dtrules.session.DTState;
import com.dtrules.session.EntityFactory;
import com.dtrules.session.ICompilerError;
import com.dtrules.session.IRSession;
import com.dtrules.session.RuleSet;
import com.dtrules.xmlparser.GenericXMLParser;

/**
 * Decision Tables are the classes that hold the Rules for a set of Policy 
 * implemented using DTRules.  There are three types: <br><br>
 * 
 * BALANCED -- These decision tables expect all branches to be defined in the condition table <br>
 * ALL      -- Evaluates all the columns, then executes all the actions, in the order they
 *             are specified, for all columns whose conditions are met.<br>
 * FIRST    -- Effectively evaluates each column, and executes only the first Column whose
 *             conditions are met.<br>
 * @author paul snow
 * Mar 1, 2007
 *
 */
public class RDecisionTable extends ARObject {
    
    private final  RName    dtname;             // The decision table's name.
    
    private        String   filename = null;    // Filename of Excel file where the table is defined,
                                                //   if this decision table is defined in Excel.
    
    enum UnbalancedType { FIRST, ALL };         // Unbalanced Table Types.
    public static enum Type { 
        BALANCED { void build(RDecisionTable dt) {dt.buildBalanced(); }},
        FIRST    { void build(RDecisionTable dt) {dt.buildUnbalanced(UnbalancedType.FIRST); }},
        ALL      { void build(RDecisionTable dt) {dt.buildUnbalanced(UnbalancedType.ALL);   }};
        abstract void build(RDecisionTable dt);
    }    
  
    public Type  type = Type.BALANCED;          // By default, all decision tables are balanced.
    
    public static  final int MAXCOL = 16;       // The Maximum number of columns in a decision table.
    
                         int maxcol = 1;        // The number of columns in this decision table.
                             
    private final IRSession session;        	// We need to compile within a session, so we know how to parse dates (among other things)
	
    private final RuleSet ruleset;			    // A decision table must belong to a particular ruleset
    
    public  final Map<RName,String> fields = new HashMap<RName, String>(); // Holds meta information about this decision table.
	
	private boolean  compiled=false;            // The decision table isn't compiled
												//   until fully constructed.  And
												//   it won't be if compilation fails.
    String [][] conditiontable;
	String   [] conditions;                     // The conditions in formal.  The formal langauge is compiled to get the postfix
	String   [] conditionsPostfix;              // The conditions in postfix. derived from the formal
	String   [] conditionsComment;              // A comment per condition.
	IRObject [] rconditions;					// Each compiled condition
	
    String [][] actiontable;
	String   [] actions;
	String   [] actionsComment;
	String   [] actionsPostfix;
	IRObject [] ractions;						// Each compiled action
	
	
	String   [] initialActions;                 // A list of actions to be executed each time the 
	                                            // decision table is executed before the conditions
	                                            // are evaluated.
	IRObject [] rinitialActions;
	String   [] initialActionsPostfix;          // Compiled Initial Actions
    String   [] initialActionsComment;          // Comment for Initial Actions
	String   [] contexts;                       // Contexts in which to execute this table.
	String   [] contextsPostfix;                // The Postfix for each context statement.
	String      contextsrc;                     // For Tracing...
	IRObject    rcontext;                       //  lists of entities.  It is best if this is done within the table than
	                                            //  by the calling table.
	
	List<ICompilerError> errorlist = new ArrayList<ICompilerError>();
	DTNode decisiontree=null;

    private int numberOfRealColumns = 0;        // Number of real columns (as unbalanced tables can have
    // far more columns than they appear to have).

    public int getNumberOfRealColumns() {
        if(decisiontree==null)return 0;
        return decisiontree.countColumns();
    }

    /**
     * Check for errors in the decision table.  Returns the column
     * and row of a problem if one is found.  If nothing is wrong,
     * a null is returned.
     * @return
     */
    public Coordinate validate(){
       if(decisiontree==null){
           if(actions !=null && actions.length==0)return null;
            return new Coordinate(0,0);
       }
       return decisiontree.validate();
    }
    
    BalanceTable balanceTable = null;           // Helper class to build a balanced or optimized version
                                                //   of this decision table.
    
    public boolean isCompiled(){return compiled;}
    
    
    
	public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public IRObject clone(IRSession s) throws RulesException {
        RDecisionTable dt = new RDecisionTable(s, dtname.stringValue());
        dt.numberOfRealColumns      = numberOfRealColumns;
        dt.conditiontable           = conditiontable.clone();
        dt.conditions               = conditions.clone();
        dt.conditionsPostfix        = conditionsPostfix.clone();
        dt.conditionsComment        = conditionsComment.clone();
        dt.rconditions              = rconditions.clone();
        dt.actiontable              = actiontable.clone();
        dt.actions                  = actions.clone();
        dt.actionsComment           = actionsComment.clone();
        dt.actionsPostfix           = actionsPostfix.clone();
        dt.ractions                 = ractions.clone();
        dt.rinitialActions          = rinitialActions.clone();
        dt.initialActions           = initialActions.clone();
        dt.initialActionsComment    = initialActionsComment.clone();
        dt.initialActionsPostfix    = initialActionsPostfix.clone();
        dt.contexts                 = contexts.clone();
        dt.contextsPostfix          = contextsPostfix.clone();
        dt.contextsrc               = contextsrc;
        dt.rcontext                 = rcontext.clone(s);
        return dt;
    }
    /**
     * Changes the type of the given decision table.  The table is rebuilt. 
     * @param type
     * @return Returns a list of errors which occurred when the type was changed.
	 */
	public void setType(Type type) {
       this.type = type;   
    }
	/**
	 * This routine compiles the Context statements for the 
	 * decision table into a single executable array.  
	 * It must embed into this array a call to executeTable 
	 * (which avoids this context building for the table).
	 */
	private void buildContexts(){
	    // Nothing to do if no extra contexts are specfied.
	   if(contextsPostfix==null || contextsPostfix.length==0) return;
       
	   // This is the call to executeTable against this decisiontable
	   // that we are going to embed into our executable array.
	   contextsrc = "/"+getName().stringValue()+" executeTable ";
       
	   boolean keep = false;
       for(int i=contextsPostfix.length-1;i>=0;i--){
           if(contextsPostfix[i]!=null){
               contextsrc = "{ "+contextsrc+" } "+contextsPostfix[i];
               keep = true;
           }    
       }
       if(keep == true){
           try {
              rcontext = RString.compile(session, contextsrc, true);
           } catch (RulesException e) {
              errorlist.add(
                    new CompilerError (
                            ICompilerError.Type.CONTEXT,
                            "Formal Compiler Error: "+e,
                            contextsrc,0));
           }
       }  	    
	}
		
    /**
     * Build this decision table according to its type.
     *
     */
	public void build(){
       errorlist.clear();
       decisiontree = null;
       buildContexts();
       /** 
        * If a context or contexts are specified for this decision table,
        * compile the context formal into postfix.
        */
       type.build(this);
    }
        
    /**
     * Return the name of this decision table.
     * @return
     */
    public RName getName(){
        return dtname;
    }
    
    /**
     * Renames this decision table.
     * @param session
     * @param newname
     * @throws RulesException
     */
    public void rename(IRSession session, RName newname)throws RulesException{
        ruleset.getEntityFactory(session).deleteDecisionTable(dtname);
        ruleset.getEntityFactory(session).newDecisionTable(newname, session);
    }
    
    /**
     * Create a Decision Table 
     * @param tables
     * @param name
     * @throws RulesException
     */
    
	public RDecisionTable(IRSession session, String name) throws RulesException{
        this.session = session;
        ruleset      = session.getRuleSet();
		dtname       = RName.getRName(name,true);
		
        EntityFactory ef = ruleset.getEntityFactory(session);
        RDecisionTable dttable =ef.findDecisionTable(RName.getRName(name)); 
        if(dttable != null){
            new CompilerError(CompilerError.Type.TABLE,"Duplicate Decision Tables Found",0,0);
		}    
	}
	
	/**
	 * Compile each condition and action.  We mark the decision table as
	 * uncompiled if any error is detected.  However, we still attempt to 
	 * compile all conditions and all actions.
	 */
	public List<ICompilerError> compile(){
		compiled          = true;                  // Assume the compile will work.
		rconditions       = new IRObject[conditionsPostfix.length];
		ractions          = new IRObject[actionsPostfix.length];
		rinitialActions   = new IRObject[initialActionsPostfix.length];
		
		for(int i=0; i< initialActions.length; i++){
             try {
                 rinitialActions[i] = RString.compile(session, initialActionsPostfix[i],true);
             } catch (Exception e) {
                 errorlist.add(
                         new CompilerError(
                            ICompilerError.Type.INITIALACTION,
                            "Postfix Interpretation Error: "+e,
                            initialActionsPostfix[i],
                            i
                         )
                 );            
                 compiled = false;
                 rinitialActions[i]=RNull.getRNull();
             }
         }
		 
		for(int i=0;i<rconditions.length;i++){
			try {
				rconditions[i]= RString.compile(session, conditionsPostfix[i],true);
			} catch (RulesException e) {
                errorlist.add(
                   new CompilerError(
                      ICompilerError.Type.CONDITION,
                      "Postfix Interpretation Error: "+e,
                      conditionsPostfix[i],
                      i
                   )
                );
                compiled=false;
				rconditions[i]=RNull.getRNull();
			}
		}
		for(int i=0;i<ractions.length;i++){
			try {
				ractions[i]= RString.compile(session, actionsPostfix[i],true);
			} catch (RulesException e) {
                errorlist.add(
                        new CompilerError(
                           ICompilerError.Type.ACTION,
                           "Postfix Interpretation Error: "+e,
                           actionsPostfix[i],
                           i
                        )
                     );
                compiled=false;
				ractions[i]=RNull.getRNull();
			}
		}
        return errorlist;
	}
	
	public void execute(DTState state) throws RulesException {
	    RDecisionTable last = state.getCurrentTable();
	    state.setCurrentTable(this);
	    try {
			int estk     = state.edepth();
			int dstk     = state.ddepth();
			int cstk     = state.cdepth();

			state.pushframe();
			
			if(rcontext==null){
			    executeTable(state);
			}else{
			    if(state.testState(DTState.TRACE)){
			        state.traceTagBegin("context", "execute='"+contextsrc+"'");
			        try {
                        rcontext.execute(state);
                    } catch (RulesException e) {
                        e.setSection("Context", 0);
                        throw e;
                    }
			        state.traceTagEnd("context", null);
			    }else{
			        rcontext.execute(state);
			    }    
			}
			state.popframe();
			
			if(estk!= state.edepth() ||
			   dstk!= state.ddepth() ||
			   cstk!= state.cdepth() ){
			    throw new RulesException("Stacks Not balanced","DecisionTables", 
			    "Error while executing table: "+getName().stringValue() +"\n" +
			     (estk!= state.edepth() ? "Entity Stack before  "+estk+" after "+state.edepth()+"\n":"")+
			     (dstk!= state.ddepth() ? "Data Stack before    "+dstk+" after "+state.ddepth()+"\n":"")+
			     (cstk!= state.cdepth() ? "Control Stack before "+cstk+" after "+state.cdepth()+"\n":""));
			}
		} catch (RulesException e) {
			e.addDecisionTable(this.getName().stringValue(), this.getFilename());
			state.setCurrentTable(last);
			throw e;
		}
	    state.setCurrentTable(last);
	}
	
	/**
	 * A decision table is executed by simply executing the
	 * binary tree underneath the table.
	 */
	public void executeTable(DTState state) throws RulesException {
        if(compiled==false){
            throw new RulesException(
                "UncompiledDecisionTable",
                "RDecisionTable.execute",
                "Attempt to execute an uncompiled decision table: "+dtname.stringValue()
            );
        }
        
        boolean trace = state.testState(DTState.TRACE);
        int edepth    = state.edepth();  // Get the initial depth of the entity stack 
                                         //  so we can toss any extra entities added...
        if(trace){
            state.traceTagBegin("decisiontable","tID='"+state.tracePt()+"' name='"+dtname+"'");
            if(state.testState(DTState.VERBOSE)){
                state.traceTagBegin("entity_stack", null);
                for(int i=0;i<state.edepth();i++){
                    state.traceInfo("entity", "id='"+state.getes(i).getID()+"'", state.getes(i).stringValue());
                }
                state.traceTagEnd("entity_stack",null);
            }
            state.traceTagBegin("initialActions", null);
            for( int i=0; rinitialActions!=null && i<rinitialActions.length; i++){
                try{
                   rinitialActions[i].execute(state);
                }catch(RulesException e){
                    e.setSection("Initial Actions", i+1);
                    throw e;
                }
            }
            state.traceTagEnd("initialActions", null);
            if(decisiontree!=null)decisiontree.execute(state);
            state.traceTagEnd  ("decisiontable",null);
            
        }else{
            for( int i=0; rinitialActions!=null && i<rinitialActions.length; i++){
                state.setCurrentTableSection("InitialActions", i);
                try{
                    rinitialActions[i].execute(state);
                 }catch(RulesException e){
                     e.setSection("Initial Actions", i+1);
                     throw e;
                 }
            }
            if(decisiontree!=null)decisiontree.execute(state);
        }    
        while(state.edepth() > edepth)state.entitypop();     // Pop off extra entities. 
	}

	/**
	 * Builds (if necessary) the internal representation of the decision table,
	 * then validates that structure.
	 * @return true if the structure builds and is valid; false otherwise.
	 */
	public List<ICompilerError> getErrorList()  {
       if(decisiontree==null){
           errorlist.clear();
           build();
	   }
	   return errorlist;
	}	
	
    
    
    
	/**
	 * Builds the decision tree, which is a binary tree of "DTNode"'s which can be executed
     * directly.  This defines the execution of a Decision Table.
     * <br><br>
     * The way we build this binary tree is we walk down each column, tracing
	 * that column's path through the decision tree.  Once we are at the end of the column,
	 * we add on the actions.  This algorithm assumes that a decision table describes
	 * a complete decision tree, i.e. there is no set of posible condition states which 
     * are not explicitly handled by the decision table.
	 *
	 */
	void buildBalanced() {
		compile();
		if(conditiontable[0].length == 0 ||           // If we have no conditions, or
		   conditiontable[0][0].equals("*")){         // If *, we just execute all actions
		   decisiontree = ANode.newANode(this,0);	  //   checked in the first column             
		   return;
		}
        
		decisiontree = new CNode(this,0,0, rconditions[0]);                          // Allocate a root node.
       
        for(int col=0;col<maxcol;col++){						// For each column, we are going to run down the
																				//   column building that path through the tree.
			boolean laststep = conditiontable[0][col].equalsIgnoreCase("y");	// Set the test for the root condition.
			CNode   last     = (CNode) decisiontree;							// The last node will start as the root.					

			for(int i=1; i<conditiontable.length; i++){							// Now go down the rest of the conditions.
				String t = conditiontable[i][col];								// Get this conditions truth table entry.
				boolean yes = t.equalsIgnoreCase("y");
				boolean invalid = false;
				if(yes || t.equalsIgnoreCase("n")){			                    // If this condition should be considered...
					CNode here=null;
					try {
						if(laststep){
							here = (CNode) last.iftrue;
						}else{
							here = (CNode) last.iffalse;
						}
						if(here == null){									    // Missing a CNode?  Create it!
							here = new CNode(this,col,i,rconditions[i]);
							if(laststep){
								last.iftrue  = here;
							}else{
								last.iffalse = here;
							}
						}
					} catch (RuntimeException e) {
                        invalid = true;        
					}
					if(invalid || here.conditionNumber != i ){
                        errorlist.add(
                                new CompilerError(
                                   ICompilerError.Type.TABLE,
                                   "Condition Table Compile Error ",
                                   i,col
                                )
                        );
                        return;
					}
					last     = here;
					laststep = yes;
				}
            }    
			if(laststep){														// Once we have traced the column, add the actions.
				last.iftrue=ANode.newANode(this,col);	
			}else{
				last.iffalse=ANode.newANode(this,col);
			}
            
		}
        DTNode.Coordinate rowCol = decisiontree.validate();
        if(rowCol!=null){
            errorlist.add(
               new CompilerError(ICompilerError.Type.TABLE,"Condition Table isn't balanced.",rowCol.row,rowCol.col)
            );        
            compiled = false;
        }
	}
	
    boolean newline=true;
    
    private void printattrib(PrintStream p, String tag, String body){
        if(!newline){p.println();}
        p.print("<"); p.print(tag); p.print(">");
        p.print(body);
        p.print("</"); p.print(tag); p.print(">");
        newline = false;
    }
    
    private void openTag(PrintStream p,String tag){
        if(!newline){p.println();}
        p.print("<"); p.print(tag); p.print(">");
        newline=false;
    }
    
    /**
     * Write the XML representation of this decision table to the given outputstream.
     * @param o Output stream where the XML for this decision table will be written.
     */
    public void writeXML(PrintStream p){
        p.println("<decision_table>");
        newline = true;
        printattrib(p,"table_name",dtname.stringValue());
        Iterator<RName> ifields = fields.keySet().iterator();
        while(ifields.hasNext()){
            RName name = ifields.next();
            printattrib(p,name.stringValue(),fields.get(name));
        }
        openTag(p, "conditions");
        for(int i=0; i< conditions.length; i++){
            openTag(p, "condition_details");
            printattrib(p,"condition_number",(i+1)+"");
            printattrib(p,"condition_description",GenericXMLParser.encode(conditions[i]));
            printattrib(p,"condition_postfix",GenericXMLParser.encode(conditionsPostfix[i]));
            printattrib(p,"condition_comment",GenericXMLParser.encode(conditionsComment[i]));
            p.println();
            newline=true;
            for(int j=0; j<maxcol; j++){
               p.println("<condition_column column_number=\""+(j+1)+"\" column_value=\""+conditiontable[i][j]+"\" />");
            }
            p.println("</condition_details>");
        }
        p.println("</conditions>");
        openTag(p, "actions");
        for(int i=0; i< actions.length; i++){
            openTag(p, "action_details");
            printattrib(p,"action_number",(i+1)+"");
            printattrib(p,"action_description",GenericXMLParser.encode(actions[i]));
            printattrib(p,"action_postfix",GenericXMLParser.encode(actionsPostfix[i]));
            printattrib(p,"action_comment",GenericXMLParser.encode(actionsComment[i]));
            p.println();
            newline=true;
            for(int j=0; j<maxcol; j++){
               if(actiontable[i][j].length()>0){
                   p.println("<action_column column_number=\""+(j+1)+"\" column_value=\""+actiontable[i][j]+"\" />");
               }
            }
            p.println("</action_details>");
        }
        p.println("</actions>");
        p.println("</decision_table>");
    }
    
    
	/**
	 * All Decision Tables are executable.
	 */
	public boolean isExecutable() {
		return true;
	}
    
	/**
	 * The string value of the decision table is simply its name.
	 */
	public String stringValue() {
        String number = fields.get("ipad_id"); 
        if(number==null)number = "";
		return number+" "+dtname.stringValue();
	}
	
	/**
	 * The string value of the decision table is simply its name.
	 */
	public String toString() {
		return stringValue();
	}
    
	/**
     * Return the postFix value 
	 */
    public String postFix() {
        return dtname.stringValue();
    }

    /**
	 * The type is Decision Table.
	 */
	public int type() {
		return iDecisiontable;
	}

	/**
	 * @return the actions
	 */
	public String[] getActions() {
		return actions;
	}
	
	/**
	 * @return the actiontable
	 */
	public String[][] getActiontable() {
		return actiontable;
	}

	/**
	 * @return the conditions
	 */
	public String[] getConditions() {
		return conditions;
	}

	/**
	 * @return the conditiontable
	 */
	public String[][] getConditiontable() {
		return conditiontable;
	}
	
	public String getDecisionTableId(){
		return fields.get(RName.getRName("table_number"));
	}
	
	public void setDecisionTableId(String decisionTableId){
		fields.put(RName.getRName("table_number"),decisionTableId);
	}
	
	public String getPurpose(){
		return fields.get(RName.getRName("purpose"));
	}
	
	public void setPurpose(String purpose){
		fields.put(RName.getRName("purpose"),purpose);
	}
	
	public String getComments(){
		return fields.get(RName.getRName("comments"));
	}
	
	public void setComments(String comments){
		fields.put(RName.getRName("comments"),comments);
	}
	
	public String getReference(){
		return fields.get(RName.getRName("policy_reference"));
	}
	
	public void setReference(String reference){
		fields.put(RName.getRName("policy_reference"),reference);
	}

	/**
	 * @return the dtname
	 */
	public String getDtname() {
		return dtname.stringValue();
	}


	/**
	 * @return the ractions
	 */
	public IRObject[] getRactions() {
		return ractions;
}
	/**
	 * @param ractions the ractions to set
	 */
	public void setRactions(IRObject[] ractions) {
		this.ractions = ractions;
	}

	/**
	 * @return the rconditions
	 */
	public IRObject[] getRconditions() {
		return rconditions;
	}

	/**
	 * @param rconditions the rconditions to set
	 */
	public void setRconditions(IRObject[] rconditions) {
		this.rconditions = rconditions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(String[] actions) {
		this.actions = actions;
	}

	/**
	 * @param actiontable the actiontable to set
	 */
	public void setActiontable(String[][] actiontable) {
		this.actiontable = actiontable;
	}

	/**
	 * @param conditions the conditions to set
	 */
	public void setConditions(String[] conditions) {
		this.conditions = conditions;
	}

	/**
	 * @param conditiontable the conditiontable to set
	 */
	public void setConditiontable(String[][] conditiontable) {
		this.conditiontable = conditiontable;
	}


	/**
	 * @return the actionsComment
	 */
	public final String[] getActionsComment() {
		return actionsComment;
	}


	/**
	 * @param actionsComment the actionsComment to set
	 */
	public final void setActionsComment(String[] actionsComment) {
		this.actionsComment = actionsComment;
	}


	/**
	 * @return the actionsPostfix
	 */
	public final String[] getActionsPostfix() {
		return actionsPostfix;
	}


	/**
	 * @param actionsPostfix the actionsPostfix to set
	 */
	public final void setActionsPostfix(String[] actionsPostfix) {
		this.actionsPostfix = actionsPostfix;
	}


	/**
	 * @return the conditionsComment
	 */
	public final String[] getConditionsComment() {
		return conditionsComment;
	}


	/**
	 * @param conditionsComment the conditionsComment to set
	 */
	public final void setConditionsComment(String[] conditionsComment) {
		this.conditionsComment = conditionsComment;
	}


	/**
	 * @return the conditionsPostfix
	 */
	public final String[] getConditionsPostfix() {
		return conditionsPostfix;
	}


	/**
	 * @param conditionsPostfix the conditionsPostfix to set
	 */
	public final void setConditionsPostfix(String[] conditionsPostfix) {
		this.conditionsPostfix = conditionsPostfix;
	}

    /**
     * A little helpper function that inserts a new column in a table
     * of strings organized as String table [row][column];  Inserts blanks
     * in all new entries, so this works for both conditions and actions.
     * @param table     
     * @param col
     */
    private static void insert(String[][]table, int maxcol, final int col){
        for(int i=0; i<maxcol; i++){
            for(int j=15; j> col; j--){
                table[i][j]=table[i][j-1];
            }
            table[i][col]=" ";
        }   
    }
	/**
     * Insert a new column at the given column number (Zero based) 
     * @param col The zero based column number for the new column
     * @throws RulesException
	 */
    public void insert(int col) throws RulesException {
        if(maxcol>=16){
            throw new RulesException("TableTooBig","insert","Attempt to insert more than 16 columns in a Decision Table");
        }
        insert(conditiontable,maxcol,col);
        insert(actiontable,maxcol,col);
    }
    
    /**
     * Balances an unbalanced decision table.  The additional columns have
     * no actions added.  There are two approaches to balancing tables.  One
     * is to have executed all columns whose conditions are met.  The other is
     * to execute only the first column whose conditions are met.  This 
     * routine executes all columns whose conditions are met.
     */
    public void buildUnbalanced(UnbalancedType type) {
       
        compile(); 
        
       if( 
           conditiontable.length == 0 ||
    	   conditiontable[0].length == 0 ||           // If we have no conditions, or
           conditiontable[0][0].equals("*")){         // If *, we just execute all actions
    	   decisiontree = ANode.newANode(this,0);	  //   checked in the first column             
    	   return;
       }	
       
       if(conditions.length<1){  
           errorlist.add(
                   new CompilerError(
                           ICompilerError.Type.CONDITION,
                           "You have to have at least one condition in a decision table",
                           0,0)
           );        
       }
       if( conditiontable[0].length==0 || conditiontable[0][0].trim().equals("*"))return;
       /**
        * 
        */
       CNode top = new CNode(this,1,0,this.rconditions[0]);
       int defaultCol = -1;         // The Index of the Default Column
       int allCol     = -1;         // The Index of the "All" Column (executed on all conditions)
       for(int col=0;col<maxcol;col++){                             // Look at each column.
           boolean nonemptycolumn = false;
           for(int row=0; !nonemptycolumn && row<conditions.length; row++){
               String v      = conditiontable[row][col];                     // Get the value from the condition table
               nonemptycolumn = !v.equals("-") && !v.equals(" ");
           }
           if(nonemptycolumn){    
             try {
                processCol(type,top,0,col);                         // Process all other columns.
             } catch (Exception e) {
                /** Any error detected is recorded in the errorlist.  Nothing to do here **/                                            
             }
           }
       }
       ANode defaults;
       if(defaultCol >= 0){
           defaults = ANode.newANode(this,defaultCol);
       }else{    
           defaults = new ANode(this);
       }    
       addDefaults(top,defaults);                                   // Add defaults to all unmapped branches
       if(allCol >= 0) addAll(top, ANode.newANode(this,allCol));    // Add to all branches the All actions
       decisiontree = optimize(top);                                // Optimize the given tree.
    }     

    /**
     * Replace any untouched branches in the tree with a pointer
     * to the defaults for this table.  We only replace nulls.
     * @param node
     * @param defaults
     * @return
     */
    private DTNode addDefaults(DTNode node, ANode defaults){
        if(node == null ) return defaults; 
        if(node instanceof ANode)return node;
        CNode cnode = (CNode)node;
        cnode.iffalse = addDefaults(cnode.iffalse,defaults);
        cnode.iftrue  = addDefaults(cnode.iftrue, defaults);
        return node;
    }
         
    private void addAll(DTNode node, ANode all){
       if(node.getClass()==ANode.class){
           ((ANode)node).addNode(all);
       }else{
           addAll(  ((CNode)node).iffalse ,all);
           addAll(  ((CNode)node).iftrue  ,all);
       }    
    }
    
    /**
     * Replaces the given DTNode with the optimized DTNode.
     * @param node
     * @return
     */
    private DTNode optimize(DTNode node){
        ANode opt = node.getCommonANode();
        if(opt!=null){
            return opt;
        }
        CNode cnode = (CNode) node;
        cnode.iftrue  = optimize(cnode.iftrue);
        cnode.iffalse = optimize(cnode.iffalse);
        if(cnode.iftrue.equalsNode(cnode.iffalse)){
            return cnode.iftrue;
        }
        return cnode;
    }
    
    /**
     * Build a path through the decision tables for a particular column.
     * This routine throws an exception, but the calling routine just ignores it.
     * That way we don't flood the error list with lots of duplicate errors.
     * @param here
     * @param row
     * @param col
     * @return
     */
    private DTNode processCol(UnbalancedType code, DTNode here, int row, int col) throws Exception{
        if(row >= conditions.length){                                 // Ah, end of the column!
            ANode thisCol = ANode.newANode(this, col);                // Get a new ANode for the column
                                       
            if(here!=null && code == UnbalancedType.FIRST){           // If we execute only the First, we are done!
                thisCol = (ANode) here;
            }
            if(here!=null && code == UnbalancedType.ALL){             // If Some path lead here, fold the
                thisCol.addNode((ANode)here);           			  //    old stuff in with this column.
            }
            return thisCol;                                           // Return the mix!
        }

        String v      = conditiontable[row][col];                     // Get the value from the condition table
        boolean dcare = v.equals("-") || v.equals(" ");               // Standardize Don't cares.
        
        if(!v.equalsIgnoreCase("y") && !v.equalsIgnoreCase("n") && !dcare){
            errorlist.add(
                    new CompilerError (
                            ICompilerError.Type.CONTEXT,
                            "Bad value in Condition Table '"+v+"' at row "+(row+1)+" column "+(col+1),
                            v,0));
        }
        if((here==null || here.getRow()!= row ) && dcare){            // If we are a don't care, but not on a row
            return processCol(code,here,row+1,col);                   //   that matches us, we skip this row for now.
        }
        
        if(here==null){                                               // If this node is null, and I need
            here = new CNode(this,col,row,rconditions[row]);          //   a condition node, create it!
        }else if (here!=null && here.getRow()!= row ){                // If this is the wrong node, and I need 
            CNode t = new CNode(this,col,row,rconditions[row]);       //   a condition node, create a new one and insert it.
            t.iffalse = here;                                         // Put the node I have on the false tree
            t.iftrue  = here.cloneDTNode();                           //   and its clone on the true path.
            here = t;                                                 // Continue with the new node.  
        }
        
        if(v.equalsIgnoreCase("y") || dcare){                         // If 'y' or a don't care,
            DTNode next = ((CNode) here).iftrue;                      // Recurse on the True Branch.
            ((CNode) here).iftrue = processCol(code,next,row+1,col);
        }
        if (v.equalsIgnoreCase("n")|| dcare){                         // If 'n' or a don't care,  
            DTNode next = ((CNode) here).iffalse;                     // Recurse on the False branch.  Note that
            ((CNode) here).iffalse = processCol(code,next,row+1,col); // Don't care branches both ways.
        }
        return here;                                                  // Return the Condition node.
    }
    
    /**
     * In the case of an unbalanced decision table, this method returns a balanced
     * decision table using one of the two unbalanced rules:  FIRST (which executes only
     * the first column whose conditions are matched) and ALL (which executes all columns
     * whose conditions are matched).  If the decision table is balanced, this method returns
     * an "optimized" decision table where all possible additional "don't cares" are inserted.
     * 
     * @return
     */
    RDecisionTable balancedTable(IRSession session) throws RulesException{
        if(balanceTable==null)balanceTable = new BalanceTable(this);
        return balanceTable.balancedTable(session);
    }
    
    public BalanceTable getBalancedTable() throws RulesException {
        return new BalanceTable(this);
    }
    
    public Iterator<RDecisionTable> DecisionTablesCalled(){
        ArrayList<RDecisionTable> tables = new ArrayList<RDecisionTable>();
        ArrayList<RArray>         stack  = new ArrayList<RArray>();
        for(int i=0;i<ractions.length;i++){
           addTables(ractions[i],stack,tables);
        }
        return tables.iterator();
    }
    
    private void addTables(IRObject action,List<RArray> stack, List<RDecisionTable> tables){
        if(action==null)return;
        if(action.type()==iArray){
            RArray array = (RArray)action;
            if(stack.contains(array))return;    // We have already been here.
            stack.add(array);
            try {     // As this is an array, arrayValue() will not ever throw an exception
                @SuppressWarnings({"unchecked"})
                Iterator objects = array.arrayValue().iterator();
                while(objects.hasNext()){
                    addTables((IRObject) objects.next(),stack,tables);
                }
            } catch (RulesException e) { }
        }
        if(action.type()==iDecisiontable && !tables.contains(action)){
            tables.add((RDecisionTable)action);
        }
    }
    /**
     * Returns the list of Decision Tables called by this Decision Table
     * @return
     */    
    ArrayList<RDecisionTable> decisionTablesCalled(){
        ArrayList<RDecisionTable> calledTables = new ArrayList<RDecisionTable>();

        addlist(calledTables, rinitialActions);
        addlist(calledTables, rconditions);
        addlist(calledTables, ractions);
        
        return calledTables;
    }
    /**
     * We do a recursive search down each IRObject in these lists, looking for
     * references to Decision Tables.  We only add references to Decision Tables
     * to the list of called tables if the list of called tables doesn't yet have
     * that reference.
     * 
     * @param calledTables
     * @param list
     */
    private void addlist(ArrayList<RDecisionTable> calledTables, IRObject [] list){
        for(int i=0; i<list.length; i++){
            ArrayList<RDecisionTable> tables = new ArrayList<RDecisionTable>();
            ArrayList<RArray>         stack  = new ArrayList<RArray>();
            getTables(stack, tables, list[i]);
            for(RDecisionTable table : tables){
                if(!calledTables.contains(table))calledTables.add(table);
            }
        }
    }
    /**
     * Here we do a recursive search of all the constructs in an IROBject.  This
     * is because some IRObjects are arrays, so we search them as well.
     * @param obj
     * @return
     */
    private ArrayList<RDecisionTable> getTables(ArrayList<RArray>stack, ArrayList<RDecisionTable> tables, IRObject obj){
        
        if(obj instanceof RDecisionTable) tables.add((RDecisionTable) obj);
        if(obj instanceof RArray && !stack.contains(obj)){
            stack.add((RArray) obj);
            for(IRObject obj2 : (RArray) obj){
                getTables(stack,tables,obj2);
            }
        }
        return tables;
    }
}

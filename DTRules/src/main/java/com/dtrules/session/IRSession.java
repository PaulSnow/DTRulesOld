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
  
package com.dtrules.session;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleExecutionSetMetadata;

import com.dtrules.entity.IREntity;
import com.dtrules.entity.REntity;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.mapping.DataMap;
import com.dtrules.mapping.Mapping;
import com.dtrules.xmlparser.IXMLPrinter;
import com.dtrules.interpreter.IRObject;

public interface IRSession {
   
    /**
     * Retrieve a list of all the DataMaps allocated by this session.
     * These DataMaps may have been updated by the Rules Engine, and
     * can be translated to XML with these modifications.
     * @return
     */
    public ArrayList<DataMap> getRegisteredMaps();
    /**
     * Allocate a registered data map.  If you want to map Data Objects
     * into the Rules Engine, you need to use this call, providing the
     * mapping which provides information about these Data Objects.
     * @return
     */
    public DataMap getDataMap(Mapping map, String tag);
   
    /**
     * Returns the RulesDirectory used to create this session.
     * @return RulesDirectory
     */
    public abstract RulesDirectory getRulesDirectory();
    /**
     * Returns the RuleSet associated with this Session.
     * @return
     */
    public abstract RuleSet getRuleSet();
    
    /**
     * Creates a new uniqueID.  This ID is unique within the RSession, but
     * not across all RSessions.  Unique IDs are used to relate references 
     * between objects when writing out trace files, or to reconstruct a RSession
     * when reading in a trace file.
     *   
     * @return A unique integer.
     */
    public abstract int getUniqueID();

    public abstract RuleExecutionSetMetadata getRuleExecutionSetMetadata();

    public abstract void release() throws RemoteException,
            InvalidRuleSessionException;

    public abstract int getType() throws RemoteException,
            InvalidRuleSessionException;

    public abstract void execute(String s) throws RulesException;

    /**
     * Returns the Rules Engine State for this Session. 
     * @return
     */
    public abstract DTState getState();
    
    public abstract EntityFactory getEntityFactory() ;

    /**
     * Debugging aid that allows you to dump an Entity and its attributes.
     * @param e
     */
    public void dump(REntity e) throws RulesException;
    
    public void printEntity(IXMLPrinter rpt, String tag, IREntity e) throws Exception ;

    public void printEntityReport(IXMLPrinter rpt, DTState state, String iRObjname );
    
    public void printEntityReport(IXMLPrinter rpt, boolean verbose, DTState state, String name, IRObject object );
    
    public void printEntityReport(IXMLPrinter rpt, boolean verbose, DTState state, String iRObjname );
    
    public void printBalancedTables(PrintStream out)throws RulesException;
    
    /**
     * Get the default mapping
     * @return
     */
    public Mapping getMapping ();
    
    /**
     * Get a named mapping file
     * @param filename
     * @return
     */
    public Mapping getMapping (String filename);
}
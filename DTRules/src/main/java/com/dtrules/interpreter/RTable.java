/*  
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
package com.dtrules.interpreter;

import java.util.ArrayList;
import java.util.HashMap;

import com.dtrules.infrastructure.RulesException;
import com.dtrules.session.DTState;
import com.dtrules.session.EntityFactory;
import com.dtrules.session.RuleSet;

public class RTable extends ARObject {
    private final int     resultType;
    private final int     id;
    private       RName   tablename;
    private       RString description;
    
    private final HashMap<RName, IRObject> table = new HashMap<RName,IRObject>();
    
    /**
     * Get the description of this table
     * @return the description
     */
    public RString getDescription() {
        return description;
    }

    /**
     * Set the description of this table
     */
    public void setDescription(RString description){
        this.description = description;
    }
    
    /**
     * Return the Unique ID for this table.
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Return the type of result found in this table.
     * @return the resultType
     */
    public int getResultType() {
        return resultType;
    }

    /**
     * Return the HashMap for primary dimension of this table
     * @return the table
     */
    public HashMap<RName, IRObject> getTable() {
        return table;
    }

    /**
     * @return the tablename
     */
    public RName getTablename() {
        return tablename;
    }

    public boolean containsKey (RName key){
        return table.containsKey(key);
    }
    
    public boolean containsValue (IRObject value){
        return table.containsValue(value);
    }
    
    private RTable(EntityFactory ef, 
            RName  tablename, 
            String description, 
            int    resultType) throws RulesException {
        this.tablename   = tablename;
        this.resultType  = resultType;
        this.id          = ef.getUniqueID();
        this.description = RString.newRString(description);
    }
    /**
     * Factory method for creating an RTable
     * @param state
     * @param tablename
     * @param description
     * @param resultType
     * @return
     */
    static public RTable newRTable(EntityFactory ef, RName tablename, String description, int resultType) throws RulesException{
        return new RTable(ef,tablename,description,resultType);
    }
    /**
     * This routine assumes that the string defines an Array of the 
     * form:
     *     {
     *       { key1 value1 }
     *       { key2 value2 }
     *       ...
     *       { keyn valuen }
     *     }
     * This routine compiles the given string, then calls the
     * set routine that takes an array of key value pairs and sets
     * them into the RTable    
     *     
     * @param values
     */
    public void setValues(RuleSet rs, String values)throws RulesException{
        RArray array = RString.compile(rs, values, false).rArrayValue();
        setValues(array);
    }
    /**
     * This routine assumes that an Array of the 
     * form:
     *     {
     *       { key1 value1 }
     *       { key2 value2 }
     *       ...
     *       { keyn valuen }
     *     }
     * This routine takes an array of key value pairs and sets
     * them into the RTable    
     *     
     * @param values
     */
    public void setValues(RArray values) throws RulesException{
        for(IRObject irpair : values){
            RArray pair = irpair.rArrayValue();
            if(pair.size()!=2){
                throw new RulesException(
                        "Invalid_Table_Value",
                        "RTable.setValues",
                        "setValues expected an array of arrays giving pairs of values to assert into the Table");
            }
            RName key      = pair.get(0).rNameValue();
            IRObject value = pair.get(1);
            setValue(key, value);
        }
    }
    
    /**
     * Set a value with the given set of keys into the given table. 
     * @param keys
     * @param value
     * @throws RulesException
     */
    @SuppressWarnings("unchecked")
    public void setValue(DTState state, RName[]keys, IRObject value) throws RulesException{
        IRObject v = this;
        for(int i=0;i<keys.length-1; i++){
            if(v.type()!=iTable){
                throw new RulesException("OutOfBounds","RTable","Invalid Number of Keys used with Table "+this.stringValue());
            }
            RTable   table = v.rTableValue();
            IRObject next  =  table.getValue(keys[i]);
            if(next == null){
                next = newRTable(state.getSession().getEntityFactory(),this.tablename,this.description.stringValue(),this.resultType);
                table.setValue(keys[i], next);
            }
            v = (IRObject) next;
        }
        v.rTableValue().setValue(keys[keys.length-1], value);
    }
    
    public void setValue(RName key, IRObject value) throws RulesException{
        table.put(key, value);
    }
    
    public IRObject getValue(RName key) throws RulesException {
        IRObject v = this.table.get(key);
        if(v==null)return RNull.getRNull();
        return v;
    }
    
    public IRObject getValue(RName[]keys) throws RulesException {
        IRObject v = this;
        for(int i=0;i<keys.length; i++){
            if(v.type()!=iTable){
                throw new RulesException("OutOfBounds","RTable","Invalid Number of Keys used with Table "+this.stringValue());
            }
            RTable   table = v.rTableValue();
            IRObject next  =  table.getValue(keys[i]);
            if(next == null){
                return RNull.getRNull();
            }
            v = (IRObject) next;
        }
        return v;
    }
    
    public RArray getKeys (DTState state){
        ArrayList <RName> keys = new ArrayList<RName>(table.keySet());
        int id = state.getSession().getUniqueID();
        return new RArray(id, true, keys,false);
    }
    
    public String stringValue() {
        if(tablename!=null) return tablename.stringValue();
        return "";
    }

    public int type() {
        return iTable;
    }

    /* (non-Javadoc)
     * @see com.dtrules.interpreter.ARObject#rTableValue()
     */
    @Override
    public RTable rTableValue() throws RulesException {
        return this;
    }

    /* (non-Javadoc)
     * @see com.dtrules.interpreter.ARObject#tableValue()
     */
    @SuppressWarnings({"unchecked"})
    public HashMap tableValue() throws RulesException {
        return table;
    }
    
    
}

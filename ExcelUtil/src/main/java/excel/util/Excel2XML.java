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
package excel.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;

import com.dtrules.compiler.cup.Compiler;
import com.dtrules.compiler.decisiontables.DTCompiler;
import com.dtrules.decisiontables.RDecisionTable;
import com.dtrules.decisiontables.DTNode.Coordinate;
import com.dtrules.entity.IREntity;
import com.dtrules.interpreter.RName;
import com.dtrules.mapping.IMapGenerator;
import com.dtrules.mapping.MapGenerator;
import com.dtrules.mapping.MapGenerator2;
import com.dtrules.session.EntityFactory;
import com.dtrules.session.ICompilerError;
import com.dtrules.session.IRSession;
import com.dtrules.session.RSession;
import com.dtrules.session.RuleSet;
import com.dtrules.session.RulesDirectory;

public class Excel2XML {

    private RuleSet       ruleSet;
    private final String  UDTFilename = "Uncompiled_DecisionTables.xml";    
    String                path;
    String                rulesDirectoryXML;
    String                ruleset;
    DTCompiler            dtcompiler = null;
    
    /**
     * Returns the ruleSet being used by this compiler
     * @return
     */
    public RuleSet getRuleSet() {
        return ruleSet;
    }

    /**
     * We assume that the Rule Set provided gives us all the parameters needed
     * to open a set of Excel Files and convert them into XML files. 
     * @param propertyfile
     */
    public Excel2XML(RuleSet ruleSet) {
    	this.ruleSet = ruleSet;
    }

    /**
     * Opens the RulesDirectory for the caller.
     * @param rulesDirectoryXML
     * @param ruleset
     */
    public Excel2XML(String path, String rulesDirectoryXML, String ruleset){
        this.path               = path;
        this.rulesDirectoryXML  = rulesDirectoryXML;
        this.ruleset            = ruleset;
        reset();
    }

    /**
     * We reset the Rules Directory after we have modified the XML used to
     * define the Rules Directory.
     */
    public void reset (){
        RulesDirectory rd = new RulesDirectory(path,rulesDirectoryXML);
        this.ruleSet = rd.getRuleSet(ruleset);
    }
        
    /**
     * Return the Last DecisionTable Compiler used.  Null if none exists.
     *
     * @return Return the Last DecisionTable Compiler used.
     */
    public DTCompiler getDTCompiler(){
        return dtcompiler;
    }
    /**
     * Converts the RuleSet
     * @throws Exception
     */
    public void convertRuleset() throws Exception {        
        ImportRuleSets dt = new ImportRuleSets();
        dt.convertEDDs          (ruleSet, ruleSet.getSystemPath()+"/"+ruleSet.getExcel_edd(),     ruleSet.getFilepath()+ruleSet.getEDD_XMLName());
        dt.convertDecisionTables(ruleSet.getSystemPath()+"/"+ruleSet.getExcel_dtfolder(),ruleSet.getFilepath()+UDTFilename);
        copyFile(ruleSet.getFilepath()+UDTFilename,ruleSet.getFilepath()+ruleSet.getDT_XMLName());
        reset();
    }   

    /**
     * Copy a file
     * Throws a runtime exception if anything goes wrong. 
     */
    public static void copyFile(String file1, String file2) {
        FileChannel inChannel  = null; 
        FileChannel outChannel = null;
        try{
            inChannel   = new FileInputStream(file1).getChannel();
            outChannel  = new FileOutputStream(file2).getChannel();
            try {
                // Stupid fix for Windows --  64Mb - 1024 bytes to avoid a bug. 
                int blksize = (64 * 1024 * 1024)-1024;
                long size = inChannel.size();
                long position = 0;
                while (position < size) {
                   position += inChannel.transferTo(position, blksize, outChannel);
                }
                inChannel.close();
                outChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }catch(FileNotFoundException e) {
            throw new RuntimeException(e);
        }   
    }

    /**
     * Compiles the RuleSet
     * @param NumErrorsToReport How many errors to print
     * @param err The output stream which to print the errors
     */
    public void compile(int NumErrorsToReport, PrintStream err) {
        
        try {
            IRSession session = new RSession(ruleSet);        
            Compiler       compiler     = new Compiler(session);
                           dtcompiler   = new DTCompiler(compiler);
            
            InputStream    inDTStream   = new FileInputStream(ruleSet.getFilepath()+"/"+UDTFilename);
            OutputStream   outDTStream  = new FileOutputStream(ruleSet.getFilepath()+"/"+ruleSet.getDT_XMLName());
            
            dtcompiler.compile(inDTStream, outDTStream);
                        
            RulesDirectory  rd  = new RulesDirectory(path, rulesDirectoryXML);
            RuleSet         rs  = rd.getRuleSet(RName.getRName(ruleset));
            EntityFactory   ef  = rs.newSession().getEntityFactory();
            IREntity        dt  = ef.getDecisiontables();
            Iterator<RName> idt = ef.getDecisionTableRNameIterator();
            
            while(idt.hasNext()){
                RDecisionTable t = (RDecisionTable) dt.get(idt.next());
                List<ICompilerError> errs = t.compile();
                for (ICompilerError error : errs){
                    dtcompiler.logError(
                            t.getName().stringValue(),
                            t.getFilename(),
                            "validity check", 
                            error.getMessage(), 
                            0, 
                            "In the "+error.getErrorType().name()+" row "+error.getIndex()+"\r\n"+error.getSource());
                }
                Coordinate err_RowCol = t.validate();
                if(!t.isCompiled()  || err_RowCol!=null){
                    int column = 0;
                    int row    = 0;
                    if(err_RowCol!=null){
                        row    = err_RowCol.getRow();
                        column = err_RowCol.getCol();
                    }
                    dtcompiler.logError(
                            t.getName().stringValue(),
                            t.getFilename(),
                            "validity check", 
                            "Decision Table did not compile", 
                            0, 
                            "A problem may have been found on row "+row+" and column "+column);
                }
            }
            
            dtcompiler.printErrors(err, NumErrorsToReport);
            err.println("Total Errors Found: "+dtcompiler.getErrors().size());
            
        } catch (Exception e) {
            err.print(e);
        }
        
    }
    /**
     * Generates a default Mapping File.  This file is not complete, but 
     * requires adjustments to match XML tags if they differ from the 
     * Attribute names used in the EDD.  Also, the initial Entity Stack and
     * frequence of Entity types must be examined and updated.
     * 
     * @param mapping	Tag for the mapping file.  Attributes with this tag in the
     * 					EDD will be mapped in the generated mapping
     * @param filename	File name for the mapping file generated
     * @throws Exception
     */
    @Deprecated
    public void generateMap(String mapping, String filename) throws Exception {
        RuleSet        rs   = getRuleSet();
        IMapGenerator   mgen = new MapGenerator();           
        mgen.generateMapping(
                mapping,
                rs.getFilepath()+rs.getEDD_XMLName(), 
                rs.getWorkingdirectory()+"map.xml");
    }
    
    /**
     * Generates a default Mapping File.  This file is not complete, but 
     * requires adjustments to match XML tags if they differ from the 
     * Attribute names used in the EDD.  Also, the initial Entity Stack and
     * frequency of Entity types must be examined and updated.
     * 
     * @param mapping   Version of EDD to map.
     * @param mapping   Tag for the mapping file.  Attributes with this tag in the
     *                  EDD will be mapped in the generated mapping
     * @param filename  File name for the mapping file generated
     * @throws Exception
     */
    public void generateMap(int version, String mapping, String filename) throws Exception {
        RuleSet        rs   = getRuleSet();
        IMapGenerator   mgen;
        if(version == 1){
            mgen = new MapGenerator();
        }else{
            mgen = new MapGenerator2();
        }
        mgen.generateMapping(
                mapping,
                rs.getFilepath()+rs.getEDD_XMLName(), 
                rs.getWorkingdirectory()+"map.xml");
    }
}

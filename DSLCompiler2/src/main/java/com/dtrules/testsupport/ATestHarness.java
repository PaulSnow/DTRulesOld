/** 
 * Copyright 2004-2011 DTRules.com, Inc.
 * 
 * See http://DTRules.com for updates and documentation for the DTRules Rules Engine  
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
 **/

package com.dtrules.testsupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.io.FileInputStream;

import com.dtrules.automapping.AutoDataMap;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RName;
import com.dtrules.mapping.Mapping;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;
import com.dtrules.session.RuleSet;
import com.dtrules.session.RulesDirectory;
import com.dtrules.xmlparser.XMLPrinter;
import com.dtrules.xmlparser.XMLTree;
import com.dtrules.xmlparser.XMLTree.Node;
import com.dtrules.xmlparser.XMLTree.Node.MATCH;
import com.dtrules.mapping.DataMap;

public abstract class ATestHarness implements ITestHarness {
 
	
    protected DataMap     datamap     = null;
    protected AutoDataMap autoDataMap = null;
    protected String      currentfile = "";
    protected int         filecnt     = 0;
    
    @Override
    /**
     * Default to using the older DataMap interface.
     */
	public int harnessVersion(){
    	return 1;
    }
    /**
     * Return the map Name for this Rule Set.  Override this method if your 
     * map needed for your test isn't named "default".
     */
    public String mapName(){
    	return "default";
    }
    
    public String getCurrentFile () {
        return currentfile;
    }
    
    public void executeDecisionTables(IRSession session)throws RulesException{
    	String [] decisionTables = getDecisionTableNames(); 
    	if(decisionTables == null){
    		decisionTables = new String[1];
    		decisionTables[0] = getDecisionTableName();
    	}
    	for(String table : decisionTables){
    		session.execute(table);
    	}
    };
    
    /**
     * An implementation must implement either this method, or 
     * getDecsionTableNames().  The later is preferred. 
     */
    public String getDecisionTableName() {
		return null;
	}
    
    /**
     * Returns "default" as the assumed entry point.  Override if you need to
     * test your decision tables from some other defined entry point.  The
     * entry point for your Rule Set is defined in your Rules Engine configuration
     * file (DTRules.xml).
     *  
     * @return
     */
    @Override
    public String entrypoint(){
    	return "default";
    }
    
	/**
     * By default we do not have a list of Decision Table names.  The
     * implementation can either implement this method, or the method
     * getDecisionTableName();
     */
    public String[] getDecisionTableNames() {
		return null;
	}

	public DataMap getDataMap() { 
	    return datamap; 
	}

	/**
	 * Returns the autoDataMap (or null if there is no autoDataMap)
	 * @return
	 */
	public AutoDataMap getAutoDataMap () {
		return autoDataMap;
	}
	
	/**
     * Path to the XML Directory holding all the XML files for this
     * Rule Set
     */
    public String getXMLDirectory() { return getPath()+"xml/"; }
    
    /**
     * Default Rules directory file name is DTRules.xml.
     */
    public String getRulesDirectoryFile(){
        return "DTRules.xml";
    }
    
    /**
     * Default directory with all the test files.
     */
    public String getTestDirectory(){
        return getPath()+"testfiles/"; 
    }
    
    /**
     * This is where we are going to put the trace files, report files, etc.
     * @return
     */
    public String getOutputDirectory(){ return getTestDirectory()+"output/"; }
    
    /**
     * Do you want to print the report data to the Console as well as to the
     * report file?  If so, this method should return true.
     * @return
     */
    public boolean Console(){ return true; }
    /**
     * If verbose, we are going to print the EDD before we run the rules as 
     * well as after we run the rules.
     * @return
     */
    public boolean Verbose() { return true; }
    
    /**
     * If numbered, we will number the files generated (result files, trace files, 
     * etc.)
     * 
     * @return By default we do not number generated files.
     */
    public boolean numbered() { return false; }
    
    /**
     * By default, we will trace your code.
     */
    public boolean Trace() { return true; }
    
    /**
     * By default, we will produce a coverage report as long as you have trace
     * turned on.  (No use if you don't, because we generate the coverage report
     * from the trace files)
     */
    public boolean coverageReport() { return true; }
    
    /**
     * The name of the report file.
     * @return
     */
    public String getReportFileName() { 
        return getOutputDirectory()+"report.txt"; 
    }
    
    @Override
    public File [] getFiles(){
    	File   dir      = new File(getTestDirectory());
        File   files[]  = dir.listFiles();
        
		for (int i = 0; i < files.length-1; i++){
			for(int j = 0; j < files.length-1-i; j++){
				if(files[j].getName().compareTo(files[j+1].getName())>0 || !files[j].getName().endsWith(".xml")){
					File hld = files[j];
					files[j] = files[j+1];
					files[j+1] = hld;
				}
			}
		}
		ArrayList<File> simFiles = new ArrayList<File>();
		for(int i = 0; i < files.length; i++){
			File afile = files[i];
			if(afile != null && afile.getName().endsWith(".xml")){
				simFiles.add(files[i]);
			}
		}
		File [] returnfiles = new File[simFiles.size()];
		for(int i = 0; i< simFiles.size(); i++){
			returnfiles[i]=simFiles.get(i);
		}
		return returnfiles;
    }
    
    PrintStream rpt=null; // Report file where summaries are written.
    public void runTests(){
         
        try{
            // Delete old output files
            File dir         = new File(getOutputDirectory());
            File oldOutput[] = dir.listFiles();
            for(File file : oldOutput){
               file.delete(); 
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        try {
            rpt = new PrintStream(getReportFileName());            
             
             // Allocate a RulesDirectory.  This object can contain many different Rule Sets.
             // A Rule set is a set of decision tables defined in XML, 
             // the Entity Description Dictionary (the EDD, or schema) assumed by those tables, and
             // A Mapping file that maps data into this EDD.
             
             RulesDirectory rd       = new RulesDirectory(getPath(),getRulesDirectoryFile());
             
             // Select a particular rule set and create a session to load the data and evaluate
             // that data against the rules within this ruleset.  
           
             String         ruleset  = getRuleSetName();
             RName          rsName   = RName.getRName(ruleset);
             RuleSet        rs       = rd.getRuleSet(rsName);
         	 File           dir      = new File(getTestDirectory());
             File           files[]  = getFiles();
             int            dfcnt    = 1;
             
             if(rs == null){
            	 System.out.println("Could not find the Rule Set '"+ruleset+"'");
            	 throw new RuntimeException("Undefined: '"+ruleset+"'");
             }
             
             Date start = new Date();
             
             {
                 System.out.println(start);  
                 for(File file : files){
                     if(!Console())System.out.print(dfcnt+" ");
                     Date now = new Date();
                     if(dfcnt%20==0){
                         long dt  = (now.getTime()-start.getTime())/dfcnt;
                         long sec = dt/1000;
                         long lms  = dt-(sec*1000);
                         String ms = lms<100 ? lms<10 ? "00"+lms : "0"+lms : ""+lms;
                         System.out.println("\nAvg execution time: "+sec+"."+ms);
                     }
                     String err = runfile(rd,rs,dfcnt,dir.getAbsolutePath(),file.getName());
                     Date after = new Date();
                     long dt  = (after.getTime()-now.getTime());
                     long sec = dt/1000;
                     long lms  = dt-(sec*1000);
                     String ms = lms<100 ? lms<10 ? "00"+lms : "0"+lms : ""+lms;
                     rpt.println(dfcnt+"\t"+sec+"."+ms+"\t"+file.getName());
                     if(err!=null)rpt.println(err);
                     dfcnt++;
                 }
                 Date end = new Date();
                 long dt = end.getTime() - start.getTime();
                 dt /= 1000;
                 long sec = dt%60;
                 dt /= 60;
                 long min = dt%60;
                 dt /= 60;
                 long hour = dt;
                 long lms  = dt-(sec*1000);
                 String ms = lms<100 ? lms<10 ? "00"+lms : "0"+lms : ""+lms;
                 System.out.println("\r\n\r\nTotal Execution Time (h:m:s.ms): "+hour+":"+min+":"+sec+"."+ms);
             }
             
             if(Trace() && coverageReport()){
                 Coverage c = new Coverage(rs,getOutputDirectory());
                 c.compute();
                 c.printReport(new PrintStream(getOutputDirectory()+"coverage.xml"));
             }
             
             {
                 Date now = new Date();
                 int filecnt = dfcnt-1;
                 if(filecnt == 0) filecnt = 1;
                 long dt  = (now.getTime()-start.getTime())/(filecnt);
                 long sec = dt/1000;
                 long lms  = dt-(sec*1000);
                 String ms = lms<100 ? lms<10 ? "00"+lms : "0"+lms : ""+lms;
                 System.out.println("\nDone.  Avg execution time: "+sec+"."+ms);
             }
             
         } catch ( Exception ex ) {
             rpt.println("An Error occurred while running the example:\n"+ex);
             rpt.print("<-ERR  ");
             if(Console()){
                 System.out.print(ex);
             }
         }
         
         rpt.close();
         try{
             compareTestResults();
         }catch(Exception e){
             System.out.println("Error comparing Test Results: "+e);
         }
     }
     
     public void loadData(IRSession session, String path, String dataset)throws Exception {
    	 InputStream input = new FileInputStream(path+"/"+dataset);
    	 if( harnessVersion() < 2){
	         Mapping   mapping  = session.getMapping();
	         
	         datamap = session.getDataMap(mapping,null);
	         
	         datamap.loadXML(input);
	         
	         mapping.loadData(session, datamap);
    	 }else{
    		 autoDataMap = session.getRuleSet().getAutoDataMap(session, mapName());
    		 
    	     autoDataMap.setCurrentGroup("applicationDataload");
    		 autoDataMap.LoadXML(input);
    		 
    		 autoDataMap.mapDataToTarget("dtrules");
    	 }
     }
    
     
     /**
      * Returns the error if an error is thrown.  Otherwise, a null.
      * @param rd
      * @param rs
      * @param dfcnt
      * @param path
      * @param dataset
      * @return
      */
     public String runfile(RulesDirectory rd, RuleSet rs,  int dfcnt, String path, String dataset) {
         
         PrintStream    out          = null;
         OutputStream   tracefile    = null;
         OutputStream   entityfile   = null;
         
         currentfile = dataset;
  
         String root = dataset;
         int offset = dataset.indexOf(".");
         
         if(offset >= 0){
        	 root = dataset.substring(0,offset);
         }
  
         /** Adding in the number will do nothing if we are not numbered. **/
         String number = "";
         
         /** But if we are numbered, put number in the format of "0001_" **/
         if(numbered()){
        	 filecnt++;
        	 if(filecnt < 10  ) number += "0";
        	 if(filecnt < 100 ) number += "0";
        	 if(filecnt < 1000) number += "0";
        	 number += filecnt+"_";
         }
         
         try {
              
              out        = new PrintStream     (getOutputDirectory()+number+root+"_results.xml");
              if(Trace()){
                  tracefile  = new FileOutputStream(getOutputDirectory()+number+root+"_trace.xml");
              }
              IRSession      session    = rs.newSession();
              DTState        state      = session.getState();
              state.setOutput(tracefile, out);
              if(Trace()){
                  state.setState(DTState.DEBUG | DTState.TRACE);
                  if(Verbose()){
                      state.setState(DTState.VERBOSE);
                  }
                  state.traceStart();
              }
              // Get the XML mapping for the rule set, and load a set of data into the EDD
                  
              loadData(session, path, dataset);
              
              if(Verbose()){
            	  if(harnessVersion() < 2){
                     datamap.print(new FileOutputStream(getOutputDirectory()+number+root+"_datamap.xml"));
            	  }else{
            		 autoDataMap.printDataLoadXML(new FileOutputStream(getOutputDirectory()+number+root+"_datamap.xml"));
            	  }
                  entityfile = new FileOutputStream(getOutputDirectory()+number+root+"_entities_before.xml");
                  RArray entitystack = new RArray(0,false,false);
                  for(int i=0; i< session.getState().edepth()-2; i++){
                      entitystack.add(session.getState().entityfetch(i));
                  }
                  session.printEntityReport(new XMLPrinter(entityfile), false, false, session.getState(), "entitystack", entitystack);
              }
              
              // Once the data is loaded, execute the rules.
              RulesException ex = null;
              try{
                  executeDecisionTables(session);
              }catch(RulesException e){
                  ex = e;
              }
              
              // Then if asked, dump the entities.
              if(Verbose()){
                  entityfile = new FileOutputStream(getOutputDirectory()+number+root+"_entities_after.xml");
                  RArray entitystack = new RArray(0,false,false);
                  for(int i=0; i< session.getState().edepth()-2; i++){
                      entitystack.add(session.getState().entityfetch(i));
                  }
                  session.printEntityReport(new XMLPrinter(entityfile),false, false, session.getState(), "entitystack", entitystack);
               }
              
              if(ex!=null)throw ex;
              
              // Print the report
              try{
                 printReport(dfcnt, session, out);
              }catch(Throwable e){
                 if(!Console()){                    // If we are going to the console, assume the same
                                                    // error will get thrown, so don't print twice.    
                     System.out.println(e.toString());
                 }
              }
              
              // If asked, print the report again to the console.
              if(Console()){
                  try{
                      printReport(dfcnt, session,System.out);
                  }catch(Throwable e){
                      System.out.println(e.toString());
                   }
              }

             
              
              if(Trace()){
                  session.getState().traceEnd();
              }
             
          } catch ( Exception ex ) {
              System.out.print("<-ERR  ");
              if(Console()){
                  System.out.print(ex);
              }
              return "\nAn Error occurred while running the example:\n"+ex+"\n";
          }
          return null;
      }
    
    /**
     * By default, we will simply dump the entities as the report file.
     * In general, we usually print a valid XML file as a report, but nothing
     * says you have to.
     */
    public void printReport(int runNumber, IRSession session, PrintStream out)
            throws Exception {
        RArray entitystack = new RArray(0,false,false);
        for(int i=0; i< session.getState().edepth()-2; i++){
            entitystack.add(session.getState().entityfetch(i));
        }
        session.printEntityReport(new XMLPrinter(out),true, false, session.getState(), "entitystack", entitystack);
    }

    public String referenceRulesDirectoryFile ()       { return null; }; 
    public String referencePath ()                     { return null; };
    public void   changeReportXML(OutputStream report){
        ChangeReport cr = new ChangeReport(
                getRuleSetName(), 
                getPath(),getRulesDirectoryFile(),"development",
                referencePath(),referenceRulesDirectoryFile(),"deployed");
        try{
            cr.compare(report);
        }catch(Exception e){
            System.out.println("Could not compare rule sets");
        }
    }

    /**
     * Returns a message about the difference between the nodes.
     * If there is no difference, a null is returned.
     */
    public String compareNodes(Node thisResult, Node oldResult){
         MATCH v = thisResult.compareToNode(oldResult, false); 
    	 if(MATCH.match == v) {
    		 // If this node matches, check all the children of this node.
    		 if(thisResult.getTags()!= null){
    			 for(int i=0; i < thisResult.getTags().size(); i++){
    				 String r = compareNodes(thisResult.getTags().get(i),
    						                  oldResult .getTags().get(i));
    				 if(r!=null){	// If any of the children do not match,
    					 return r;	//   return the message found, and quit
    				 }				//   checking.
    			 }
    		 }
    		 return null;	// We only get here if all matches; claim all 
    	 }					//    matches.
    	 String msg ="";
         switch(v){
	         case differentAttributes : msg = "Different Attributes"; break;
	         case differentBody       : msg = "Different Body";       break;
	         case differentType       : msg = "Different Type";       break;
         }
         String message = msg+" found on a tag: new:{" +thisResult.getName()+"} old:{"+oldResult.getName()+"} "
        		        + "with a body: new:{" + thisResult.getBody() + "} old:{"+oldResult.getBody()+"}" ;
         return message;
     }
    
    public void removeIds(Node node){
    	//Remove any DTRules
    	node.getAttributes().remove("DTRulesId");
    	node.getAttributes().remove("id");
    	if(   node.getName().equals("mapping_key") 
    	   && node.getAttributes().get("name")!=null
    	   && node.getAttributes().get("name").equals("mapping*key")){
    		node.setBody("");
    	}
    	if(node.getTags()!=null) for( Node child : node.getTags()){
    		removeIds(child);
    	}
    }
     
    public void compareTestResults() throws Exception {
        XMLPrinter report = new XMLPrinter(compareTestResultsReport());
        
        System.out.println();
        
        report.opentag("results");
        File outputs = new File(getOutputDirectory());
        if(outputs == null || !outputs.isDirectory()){
            System.out.println("'"+getOutputDirectory()+"' does not exist or is not a directory");
        }
        boolean changes = false;
        boolean missingResults = false;
        File files[] = outputs.listFiles();
        for(File file : files){
            if(file.getName().endsWith("_results.xml")){
                Node result1=null, result2=null;
                try{
                    result1 = XMLTree.BuildTree(new FileInputStream(file),false,false);
                    result2 = XMLTree.BuildTree(new FileInputStream(getResultDirectory()+file.getName()),false, false);
                    if(result1 != null && result2 != null){
                        removeIds(result1);
                        removeIds(result2);
                        String msg = compareNodes(result1,result2);
                        if(msg == null){
                            report.printdata("match","file",file.getName(),"");
                        }else{
                        	changes = true;
                        	System.out.flush(); 
                        	System.err.println(file.getName()+"--> "+msg);
                        	System.err.flush();
                        	report.printdata("resultChanged","file",file.getName(),msg);
                        }
                    }else{
                    	System.out.flush();
                    	System.err.println(file.getName()+" has no result file; No compare done.");
                    	System.err.flush();
                        report.printdata("error","file",file.getName(),"");
                    }
                }catch (Exception e){
                	missingResults = true;
                    report.printdata("unknown","file",file.getName(),"Missing Files to do the compare");
                }
            }
        }
        report.closetag();
        
        if(changes){
        	System.err.println("\nSome results have changed.  Check the TestResults.xml for all results.");
        }else{
        	System.out.println("\nALL PASS: No changes found when compared to results files.");
        }
        if(missingResults){
        	System.err.println("\nSome tests have no results with which to compare.  Check the TestResults.xml for details.");
        }
    }

    /**
     * By default, we will look for a directory: <br> 
     * <br>
     *      <testdirectory>/results <br>
     */
    public String getResultDirectory() {
        return getOutputDirectory()+"results/";        
    }
    
    /**
     * Returns standard out by default.
     */
    public PrintStream  compareTestResultsReport () throws Exception {
        PrintStream ctrr = new PrintStream(getOutputDirectory()+"TestResults.xml");
        return ctrr;
        
    }
    
     
     
}

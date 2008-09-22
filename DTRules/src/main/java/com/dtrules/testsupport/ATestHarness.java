package com.dtrules.testsupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RName;
import com.dtrules.mapping.Mapping;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;
import com.dtrules.session.RuleSet;
import com.dtrules.session.RulesDirectory;
import com.dtrules.xmlparser.XMLPrinter;

public abstract class ATestHarness implements ITestHarness {
 
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
     * The name of the report file.
     * @return
     */
    public String getReportFileName() { 
        return getOutputDirectory()+"report.txt"; 
    }
    
    PrintStream rpt=null; // Report file where summaries are written.
    public void runTests(){
         
        try{
            rpt = new PrintStream(getReportFileName());            
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
             
             // Allocate a RulesDirectory.  This object can contain many different Rule Sets.
             // A Rule set is a set of decision tables defined in XML, 
             // the Entity Description Dictionary (the EDD, or schema) assumed by those tables, and
             // A Mapping file that maps data into this EDD.
             
             RulesDirectory rd       = new RulesDirectory(getRulesDirectoryPath(),getRulesDirectoryFile());
             
             // Select a particular rule set and create a session to load the data and evaluate
             // that data against the rules within this ruleset.  
           
             String         ruleset  = getRuleSetName();
             RName          rsName   = RName.getRName(ruleset);
             RuleSet        rs       = rd.getRuleSet(rsName);
             File           dir      = new File(getTestDirectory());
             File           files[]  = dir.listFiles();
             int            dfcnt    = 1;
             
             if(Verbose()){
                 /** Print out the balanced tables **/
                 PrintStream btables = new PrintStream(getOutputDirectory()+"balanced.txt");
                 IRSession s = rs.newSession();
                 s.printBalancedTables(btables);
             }
             
             Date start = new Date();
             System.out.println(start);  
             
             for(File file : files){
                 if(file.isFile() && file.getName().endsWith(".xml")){
                     rpt.println(dfcnt+" "+file.getName());
                     if(!Console())System.out.print(dfcnt+" ");
                     if(dfcnt%20==0){
                         Date now = new Date();
                         long dt  = (now.getTime()-start.getTime())/dfcnt;
                         long sec = dt/1000;
                         long ms  = dt-(sec*1000);
                         System.out.println("\nAvg execution time: "+sec+"."+ms);
                     }
                     runfile(rd,rs,dfcnt,dir.getAbsolutePath(),file.getName());
                     dfcnt++;
                 }
                 
             }
             {
                 Date now = new Date();
                 long dt  = (now.getTime()-start.getTime())/dfcnt;
                 long sec = dt/1000;
                 long ms  = dt-(sec*1000);
                 System.out.println("\nDone.  Avg execution time: "+sec+"."+ms);
             }
             
         } catch ( Exception ex ) {
             rpt.println("An Error occurred while running the example:\n"+ex);
             rpt.print("<-ERR  ");
         }
         
         rpt.close();
     }
     
     public void runfile(RulesDirectory rd, RuleSet rs,  int dfcnt, String path, String dataset) {
         
         PrintStream    out          = null;
         OutputStream   tracefile    = null;
         OutputStream   entityfile   = null;

         try {
              
              out        = new PrintStream     (getOutputDirectory()+"results"   +dfcnt+ ".xml");
              tracefile  = new FileOutputStream(getOutputDirectory()+"trace_"    +dfcnt+ ".xml");
              entityfile = new FileOutputStream(getOutputDirectory()+"entities_" +dfcnt+ ".xml");
     
              IRSession      session    = rs.newSession();
              DTState        state      = session.getState();
              state.setOutput(tracefile, out);
              state.setState(DTState.DEBUG | DTState.TRACE | DTState.VERBOSE);
              state.traceStart();
              
              // Get the XML mapping for the rule set, and load a set of data into the EDD
                  
              Mapping   mapping  = session.getMapping();
              
              mapping.loadData(session, path+"/"+dataset);
              
              // Once the data is loaded, execute the rules.
              
              session.execute(getDecisionTableName());
              
              printReport(session, out);
              if(Console()){
                  printReport(session,System.out);
              }

              if(Verbose()){
                 RArray entitystack = new RArray(0,false,false);
                 for(int i=2; i< session.getState().edepth(); i++){
                     entitystack.add(session.getState().entityfetch(i));
                 }
                 session.printEntityReport(new XMLPrinter(entityfile), false, session.getState(), "entity stack", entitystack);
              }
              session.getState().traceEnd();
             
          } catch ( Exception ex ) {
              rpt.println("An Error occurred while running the example:\n"+ex);
              System.out.print("<-ERR  ");
          }
      }
    
    
    
}

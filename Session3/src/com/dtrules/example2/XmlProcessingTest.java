package com.dtrules.example2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import com.dtrules.entity.IREntity;
import com.dtrules.entity.REntity;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RName;
import com.dtrules.interpreter.RString;
import com.dtrules.interpreter.RTime;
import com.dtrules.mapping.MapGenerator;
import com.dtrules.mapping.Mapping;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;
import com.dtrules.session.RSession;
import com.dtrules.session.RuleSet;
import com.dtrules.session.RulesDirectory;
import com.dtrules.xmlparser.IXMLPrinter;
import com.dtrules.xmlparser.XMLPrinter;

import excel.util.Excel2XML;

public class XmlProcessingTest {
    
    static String path = Compile_Example.path;
    static {
        new UserOperators();
    }
    
    public static void main(String[] args) {
       try {
           
           // Set up the file path and configuration file names to be used in this example.
           
            String rulesDirectoryFile   = "xml/DTRules.xml";
            
            // Allocate a RulesDirectory.  This object can contain many different Rule Sets.
            // A Rule set is a set of decision tables defined in XML, 
            // the Entity Description Dictionary (the EDD, or schema) assumed by those tables, and
            // A Mapping file that maps data into this EDD.
            
            RulesDirectory rd       = new RulesDirectory(path,rulesDirectoryFile);
            
            // Select a particular rule set and create a session 
            // to load the data and evaluate that data against the 
            // rules within this ruleset.
            
            String         ruleset  = "rules_example";

            RuleSet        rs       = rd.getRuleSet(ruleset);
            IRSession      session  = rs.newSession();
            
            OutputStream tracefile = new FileOutputStream(rs.getWorkingdirectory()+"trace.xml");
            session.getState().setOutput(tracefile, System.out);
            session.getState().setState(DTState.DEBUG | DTState.TRACE | DTState.VERBOSE);
            session.getState().traceStart();
            // Get the XML mapping for the rule set, and load a set of data into the EDD
                
            Mapping   mapping  = session.getMapping();
//          String    dataset  = path+"xml/UTAP_data1.xml";
            String    dataset  = path+"temp/generatedData.xml";

            mapping.loadData(session, dataset);
            
            // Once the data is loaded, execute the rules.
            
            session.execute("UTAP_Eligibility");
                        
            RArray results = session.getState().find("results").rArrayValue();
            System.out.println("Results from processing: "+dataset+"\n");
            for(IRObject result : results){
            	prt(result,6,"CaseID");
            	prt(result,6,"CIN");
            	prt(result,3,"Age");
            	prt(result,5,"Total_Income");
            	prt(result,5,"Case_Total_Income");
            	prt(result,5,"Pregnant");
            	prt(result,5,"AI");
            	prt(result,3,"IncomeGroup");
            	prt(result,5,"eligible");
            	System.out.println();
            }

            System.out.println("\nHey!  It's Done!");

            FileOutputStream out = new FileOutputStream(rs.getWorkingdirectory()+"entities.xml");
            session.printEntityReport(new XMLPrinter(out), session.getState(), "job");
            FileOutputStream out2 = new FileOutputStream(rs.getWorkingdirectory()+"results.xml");    
            
            session.getState().traceEnd();
       
        } catch ( Exception ex ) {
            System.out.println("An Error occurred while running the example:\n"+ex);
            ex.printStackTrace();
        }
    }
    /**
     * This routine helps to print and format the fields and values of an Entity.
     * The Field Length specifies the length of the field to print the value.  If the
     *         value doesn't fit, then the filed is filled with ****** 
     * @param e
     * @param field
     * @param attribute
     * @throws Exception
     */
     static void prt (IRObject e, int field, String attribute)throws Exception {
    	String v = e.rEntityValue().get(attribute).stringValue().trim();
    	System.out.print(attribute+": ");
    	if(v.length()>field){
        	for(int i=0;i< field;i++)System.out.print("*");    		
    	}else{
    		for(int i=0;i< field-v.length();i++)System.out.print(" ");
    		System.out.print(v);
    	}	
    	System.out.print(" ");
    }
}

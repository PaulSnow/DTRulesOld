package com.dtrules.xmlprocessing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.dtrules.mapping.DataMap;
import com.dtrules.mapping.Mapping;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;
import com.dtrules.session.RuleSet;
import com.dtrules.session.RulesDirectory;

public class Test_XmlProcessing {
    
    static String path               = Compile_XmlProcessing.path;
    static String rulesDirectoryFile = Compile_XmlProcessing.rulesDirectoryFile;
    static String ruleset            = Compile_XmlProcessing.ruleset;
    
    public static void main(String[] args) {
       try {

           // Allocate a Rules Directory for looking up Rule Sets.
           
           RulesDirectory rd       = new RulesDirectory(path,rulesDirectoryFile);
           
           // Look up the rule set, and create a session.
           
           RuleSet        rs       = rd.getRuleSet(ruleset);
           IRSession      session  = rs.newSession();
          
           OutputStream tracefile = new FileOutputStream(rs.getWorkingdirectory()+"trace.xml");
           session.getState().setOutput(tracefile, System.out);
           session.getState().setState(DTState.DEBUG | DTState.TRACE | DTState.VERBOSE);
           session.getState().traceStart();
           
           // Load the Document to test into a DataMap
           
           DataMap document = new DataMap("test");
           document.loadXML(new FileInputStream(path+"temp/test.xml"));
           
           // Get the XML mapping for the rule set, and load a set of data into the EDD
               
           Mapping   mapping  = session.getMapping();
           
           // Map the document to the Rules Engine
           
           mapping.loadData(session, document);
           
           // Once the data is loaded, execute the rules.
           
           session.execute("Verify_Documents");
           session.getState().traceEnd();
           
           document.print(new FileOutputStream(path+"temp/test_out.xml"));
           System.out.println("Done");
           
           
       }catch(Exception e){
           System.out.println(e);
       }
    }   
}

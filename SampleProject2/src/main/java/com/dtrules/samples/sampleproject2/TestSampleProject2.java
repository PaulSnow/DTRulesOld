package com.dtrules.samples.sampleproject2;


import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;

import com.dtrules.entity.IREntity;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RArray;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;
import com.dtrules.testsupport.ATestHarness;
import com.dtrules.xmlparser.XMLPrinter;

public class TestSampleProject2 extends ATestHarness {
    
    public boolean  Trace()                   { return true;                            }
    public boolean  Console()                 { return true;                            }
    public String   getPath()                 { return CompileSampleProject2.path;      }
    public String   getRulesDirectoryPath()   { return getPath()+"xml/";                }
    public String   getRuleSetName()          { return "SampleProject2";                }
    public String   getDecisionTableName()    { return "Compute_Eligibility";  }
    public String   getRulesDirectoryFile()   { return "xml/DTRules.xml";               }             

    public void printReport(int runNumber, IRSession session, PrintStream out) throws RulesException {
        out.println("Results from RunNumber: "+runNumber+"\r\n");
        RArray results = session.getState().find("job.results").rArrayValue();
        for(IRObject r :results){
            IREntity result = r.rEntityValue();
            
            if(result.get("eligible").booleanValue()){
                out.print("Approved For ");
                prt(out,result,"program");
                prt(out,result,"programLevel");
            }else{
                out.print("Not Approved for ");
                prt(out,result,"program");
                RArray notes = result.get("notes").rArrayValue();
                for(IRObject n : notes){
                    out.print("     ");
                    out.print(n.stringValue());
                }
                out.println();
            }
            prt(out,result,"client_id");
            prt(out,result,"totalGroupIncome");
            prt(out,result,"client_fpl");
            out.println("\r\n");
        }
    }
 
    private void prt(PrintStream out, IREntity entity, String attrib){
        IRObject value = entity.get(attrib);
        out.print(attrib+" = "+value.stringValue()+"\r\n");
    }
    
    public static void main(String[] args) {
        TestSampleProject2 t = new TestSampleProject2();
        t.runTests();
    }
    
}    
    
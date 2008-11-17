package com.dtrules.samples.sampleproject2;

import java.util.Date;

import com.dtrules.mapping.MapGenerator;

import excel.util.Excel2XML;





/**
 * @author Paul Snow
 *
 */
public class CompileSampleProject2 {

    /**
     * In Eclipse, System.getProperty("user.dir") returns the project
     * directory.  We add a slash to insure the path ends with a slash.
     */
    public static String path    = System.getProperty("user.dir")+"/";
    
    /**
     * Routine to compile decision tables.
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception { 
        try {
            
            System.out.println("Starting: "+ new Date());
            String file    = "xml/DTRules.xml";
            String ruleset = "SampleProject2";
            
            Excel2XML converter     = new Excel2XML(path, file, ruleset);
            System.out.println("Converting: "+ new Date());

            converter.convertRuleset();
            System.out.println("Compiling: "+ new Date());

            converter.compile(2,System.out);
            System.out.println("Done: "+ new Date());

            MapGenerator mgen = new MapGenerator();
            
            mgen.generateMapping("main", 
            		path+"xml/"+converter.getRuleSet().getEDD_XMLName(), 
            		converter.getRuleSet().getWorkingdirectory()+"main.xml"); 
           
        } catch ( Exception ex ) {
            System.out.println("Failed to convert the Excel files");
            ex.printStackTrace();
            throw ex;
        }
        
        
        
     }
}

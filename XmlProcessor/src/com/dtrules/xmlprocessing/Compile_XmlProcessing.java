package com.dtrules.xmlprocessing;


import excel.util.Excel2XML;

/**
 * @author Paul Snow
 *
 */
public class Compile_XmlProcessing {
    
    /**
     * *****  This path is hard coded to the project where you check
     * *****  out the example.
     */
    public static String path               = "c:/eb/eb_dev2/RulesEngine/XmlProcessor/" ;
    public static String rulesDirectoryFile = "xml/DTRules.xml";
    public static String ruleset            = "process_test_xml";
   
    /**
     * Routine to compile decision tables.
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception { 
            
            Excel2XML converter     = new Excel2XML(path, rulesDirectoryFile, ruleset);
            
            converter.convertRuleset();
            converter.compile(5,System.out);
            converter.generateMap(1,"main","map.xml");
     }
}

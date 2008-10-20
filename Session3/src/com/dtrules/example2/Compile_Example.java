package com.dtrules.example2;


import com.dtrules.mapping.MapGenerator;
import com.dtrules.session.RuleSet;

import excel.util.Excel2XML;

/**
 * @author Paul Snow
 *
 */
public class Compile_Example {

	/**
	 * Static Reference to any Application or Project Specific 
	 * Operators
	 */
    static {
        new UserOperators();
    }
    
    /**
     * *****  This path is hard coded to the project where you check
     * *****  out the example.
     */
    public static String path    = "/home/paul/RulesEngine/Session3/";
    
    /**
     * Routine to compile decision tables.
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception { 
            
            String file    = "xml/DTRules.xml";
            String ruleset = "rules_example";
            
            Excel2XML converter     = new Excel2XML(path, file, ruleset);
            
            converter.convertRuleset();
            converter.compile(5,System.out);
            
            converter.generateMap("main",       "map.xml");
            System.out.println("done!");
     }
}

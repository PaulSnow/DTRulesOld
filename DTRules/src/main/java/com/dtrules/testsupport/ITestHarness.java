/**
 * 
 */
package com.dtrules.testsupport;

import java.io.PrintStream;

import com.dtrules.session.IRSession;

/**
 * @author ps24876
 *
 */
public interface ITestHarness {
    /**
     * Returns the path to the Project Directory under which we organize the
     * test files, defined the Rule Sets, etc. for this project.
     * @return
     */
    String getPath();
    /**
     * This is the directory where the Rule Set is defined.  By default in
     * the Abstract class, we get it relative to the Project Path.  But you
     * can override this.
     * @return
     */
    String getXMLDirectory();
    /**
     * Get the path to the Rules Directory Control File.
     */
    String getRulesDirectoryPath();
    /**
     * Defines the Rules Directory for this RuleSet.  Generally it will
     * be named "DTRules.xml" and located in the xml directory.  But not
     * always, depending on how Rules are deployed.
     * @return
     */
    String getRulesDirectoryFile();
    
    /**
     * This is the name of the Decision Table that has to be executed to evaluate
     * a data set against this rule set.
     * @return
     */
    String getDecisionTableName();
    
    /**
     * Defines the name of the RuleSet we are testing
     */
    String getRuleSetName();
    
    /**
     * Specifies a directory of test files.  Directories or files that are
     * not .xml files are ignored.  Each test file is executed, and a report
     * entry is made in the report file.
     * @return
     */
    String getTestDirectory();
   
    /**
     * This is where we are going to put the trace files, report files, etc.
     * @return
     */
    String getOutputDirectory();
    
   
    /**
     * Do you want to print the report data to the Console as well as to the
     * report file?  If so, this method should return true.
     * @return
     */
    boolean Console();
    /**
     * If verbose, we are going to print the EDD before we run the rules as 
     * well as after we run the rules.
     * @return
     */
    boolean Verbose();
    
    /**
     * If you want to trace a run, this function should return true;
     * @return
     */
    boolean Trace();

    
    /**
     * The name of the report file.
     * @return
     */
    String getReportFileName();
    /**
     * Runs all the test files in the TestDirectory;
     */
    void runTests();
    /**
     * Print reports; Since the test harness can run multiple tests, the number of 
     * the test having been run is passed down to printReport.  That way, if it needs
     * to print additional reports or results, it can tag them with the appropriate
     * run number
     * @param runNumber the number of the test being run
     * @param session the session of the Rules Engine to use as the basis of the report
     * @param the result file opened by the TestHarness... Most tests need one, so we
     * open one for you.
     */
    void printReport(int runNumber, IRSession session, PrintStream out) throws Exception;
}

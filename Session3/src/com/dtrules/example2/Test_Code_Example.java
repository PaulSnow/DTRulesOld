package com.dtrules.example2;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RName;
import com.dtrules.mapping.DataMap;
import com.dtrules.mapping.Mapping;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;
import com.dtrules.session.RSession;
import com.dtrules.session.RuleSet;
import com.dtrules.session.RulesDirectory;
import com.dtrules.xmlparser.XMLPrinter;
import java.util.Date;

public class Test_Code_Example {
    
    static String path = Compile_Example.path;
    static {
        new UserOperators();
    }
    static public class Income {
        boolean     earned = true;
        String      type = "tips"; 
        BigDecimal  amount = new BigDecimal(300);
        public boolean isEarned() {
            return earned;
        }
        public void setEarned(boolean earned) {
            this.earned = earned;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public BigDecimal getAmount() {
            return amount;
        }
        public void setAmount(int amount) {
            this.amount = new BigDecimal(amount);
        }   
        
    }
    static public class Person {
        boolean     xapplicant = false;
        String      cin = "1001";
        int         age = 74;
        boolean     pregnant = false;
        Date        dob = new Date();
        String      addressid="1";
        /**
         * @return the dob
         */
        public Date getDob() {
            return dob;
        }
        /**
         * @param dob the dob to set
         */
        public void setDob(Date dob) {
            this.dob = dob;
        }
        public boolean isXapplicant() {
            return xapplicant;
        }
        public void setXapplicant(boolean applicant) {
            this.xapplicant = applicant;
        }
        public String getCin() {
            return cin;
        }
        public void setCin(String cin) {
            this.cin = cin;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public boolean isPregnant() {
            return pregnant;
        }
        public void setPregnant(boolean pregnant) {
            this.pregnant = pregnant;
        }
        /**
         * @return the addressid
         */
        public String getAddressid() {
            return addressid;
        }
        /**
         * @param addressid the addressid to set
         */
        public void setAddressid(String addressid) {
            this.addressid = addressid;
        }
        
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
            
            // Select a particular rule set and create a session to load the data and evaluate
            // that data against the rules within this ruleset.
            
            String         ruleset  = "rules_example";
            RName          rsName   = RName.getRName(ruleset);
            RuleSet        rs       = rd.getRuleSet(rsName);
            IRSession      session  = rs.newSession();
          
           
            OutputStream tracefile = new FileOutputStream(rs.getWorkingdirectory()+"trace.xml");
            session.getState().setOutput(tracefile, System.out);
            session.getState().setState(DTState.DEBUG | DTState.TRACE | DTState.VERBOSE);
            session.getState().traceStart();
            // Get the XML mapping for the rule set, and load a set of data into the EDD
                
            Mapping   mapping  = session.getMapping();
            DataMap   genData  = generateData(session,mapping);

            System.out.println("\n-----\n");
            genData.print(System.out);
            System.out.println("\n-----\n");
            
            mapping.loadData(session, genData);
            
            // Once the data is loaded, execute the rules.
            
            session.execute("UTAP_Eligibility");
            
            RArray results = session.getState().find("results").rArrayValue();
            for(IRObject result : results){
                Test_Example.prt(result,6,"CaseID");
                Test_Example.prt(result,6,"CIN");
                Test_Example.prt(result,3,"Age");
                Test_Example.prt(result,5,"Total_Income");
                Test_Example.prt(result,5,"Case_Total_Income");
                Test_Example.prt(result,5,"Pregnant");
                Test_Example.prt(result,5,"AI");
                Test_Example.prt(result,3,"IncomeGroup");
                Test_Example.prt(result,5,"eligible");
                Test_Example.prt(result,5,"dob");
                System.out.println();
            }

            System.out.println("\nHey!  It's Done!");

            FileOutputStream out = new FileOutputStream(rs.getWorkingdirectory()+"entities.xml");
            session.printEntityReport(new XMLPrinter(out), session.getState(), "job");
            
            session.getState().traceEnd();
       
        } catch ( Exception ex ) {
            System.out.println("An Error occurred while running the example:\n"+ex);
            ex.printStackTrace();
        }
    }
    
    static private Person getPerson1001(){
        Person person = new Person();
        person.setCin("1001");
        person.setAge(3);
        person.setPregnant(false);
        person.setXapplicant(false);
        return person;
    }
    static private Person getPerson1002(){
        Person person = new Person();
        person.setCin("1002");
        person.setAge(24);
        person.setPregnant(true);
        person.setXapplicant(true);
        return person;
    }

    static private Income getIncome1001 (){
        Income income = new Income();
        income.setEarned(true);
        income.setType("tips");
        income.setAmount(300);
        return income;
    }
    
    static private Income getIncome1002 (){
        Income income = new Income();
        income.setEarned(true);
        income.setType("salary");
        income.setAmount(800);
        return income;
    }
    
    static DataMap generateData(IRSession session, Mapping map) throws Exception{
       FileOutputStream fout = new FileOutputStream(session.getRuleSet().getWorkingdirectory()+"generatedData.xml");
       DataMap r = new DataMap(map,"job",fout); 

       Person p1001 = getPerson1001();
       Person p1002 = getPerson1002();
       
       r.opentag("case", "id", "1000");
         r.printdata("caseid", "1000");
         r.opentag("people");
           
           r.opentag(p1001,"person");
             r.readDO(p1001,"person");
             r.opentag("incomes");
               r.readDO(getIncome1001(), "income");
             r.closetag();  
           r.closetag();
           
           r.opentag(p1002, "person");
              r.readDO(p1002,"person");
              r.opentag("incomes");
                r.readDO(getIncome1002(),"income");
              r.closetag();
           r.closetag();
           
         r.closetag();
         
         r.opentag("relationship");
           r.opentag(p1002,"source"); r.closetag();
           r.opentag(p1001,"target"); r.closetag();
           r.printdata("type","child");
         r.closetag();  

         r.opentag("relationship");
           r.opentag(p1001,"source"); r.closetag();
           r.opentag(p1002,"target"); r.closetag();
           r.printdata("type","parent");
         r.closetag(); 

       r.closetag();  
       r.close();
       return r;   
    }
}

/*  
 * Copyright 2004-2007 MTBJ, Inc.  
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
 */ 

package excel.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.dtrules.xmlparser.XMLPrinter;

public class ImportRuleSets {
	
    String defaultColumns[]={"number","comments","dsl","table"};
    
    // These are the default columns for the decision tables.
    String columns[] = defaultColumns;

    /**
     * Return the column number for the given column name.
     * @param s
     * @return
     */
    int getColumn(String s){
       for(int i=0; i< columns.length && columns[i]!= null; i++){
           if(columns[i].equalsIgnoreCase(s)) return i;
       }
       return -1;
    }
  
    /** 
     * Convert all the Excel files in the given directory, and all sub
     * directories
     * @param directory
     * @param sb
     * @return true if some file to convert was found.
     * @throws Exception
     */    
    private boolean convertFiles(File directory,XMLPrinter out, int depth) throws Exception{
        boolean xlsFound = false;
        File[] files = directory.listFiles();
        for(int i=0; i < files.length; i++){
            if(files[i].isDirectory()){
                if(convertFiles(files[i],out,depth+1)){
                   for(int j=0;j<depth;j++)System.out.print(" "); 
                   xlsFound = true;
                }   
            }else{
                if(files[i].getName().endsWith(".xls")){ 
                  for(int j=0;j<depth;j++)System.out.print(" "); 
                  convertDecisionTable(files[i], out);
                  xlsFound = true;
                }  
            }    
        }
        return xlsFound;
    }
        
    /**
     * Convert all the excel files in the given directory, and all sub directories,
     * returning a String Buffer of all the XML produced.
     * @param directory
     * @param destinationFile
     * @return
     * @throws Exception
     */    
	public void convertDecisionTables(String directory,String destinationFile) throws Exception{
		  XMLPrinter out = new XMLPrinter("decision_tables",new FileOutputStream(destinationFile));
		  convertFiles(new File(directory),out,0);
		  out.close();
    }
    
	private String getCellValue(HSSFSheet sheet, int row, int column){
            if(row > sheet.getLastRowNum()) return ""; 
            HSSFRow  theRow = sheet.getRow(row);
            if(theRow==null)return "";
            HSSFCell cell = theRow.getCell((short)column);
            if(cell==null)return "";
            switch(cell.getCellType()){
                case HSSFCell.CELL_TYPE_BLANK :     return "";
                case HSSFCell.CELL_TYPE_BOOLEAN :   return cell.getBooleanCellValue()? "true": "false"; 
                case HSSFCell.CELL_TYPE_NUMERIC :{   
                    Double v = cell.getNumericCellValue();
                    if(v.doubleValue() == (v.longValue())){
                        return Long.toString(v.longValue());
                    }
                    return Double.toString(v);
                }
                case HSSFCell.CELL_TYPE_STRING :    
                    String v = cell.getRichStringCellValue().getString().trim();
                    return v;
                
                default :                           return "";
            }        
    }
    /**
     * Looks for the value in some column, and returns that index.  This way we can be a bit more 
     * flexible in our format of the EDD.
     * @param value
     * @param sheet
     * @param row
     * @return  the Index of the value, or -1 if not found.
     */
    private int findvalue(String value, HSSFSheet sheet, int row){
        HSSFRow theRow = sheet.getRow(row);
        if(theRow==null)return -1;
        for(int i=0;i<theRow.getLastCellNum();i++){
            String v = getCellValue(sheet,row,i).trim();
            v=v.replaceAll(" ", "");
            if(v.equalsIgnoreCase(value))return i;
        }
        return -1;
    }
    
    public void convertEDD(String excelFileName, String outputXMLName) throws Exception {

        // First open the EDD input file.
        if(! (excelFileName.endsWith(".xls"))) throw new Exception("EDD Excel File name is invalid"); 
                
        InputStream input = new FileInputStream(new File(excelFileName));
        HSSFWorkbook wb = new HSSFWorkbook(input);
        HSSFSheet sheet = wb.getSheetAt(0);

        // Open the EDD.xml output file
        OutputStream xstrm = new FileOutputStream(outputXMLName);
        XMLPrinter   xout = new XMLPrinter("entity_data_dictionary", xstrm);
                
        // Write out a header in the EDD xml file.
        xout.opentag("edd_header");
           xout.printdata("edd_create_stamp",
                new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(new Date())
           );
           xout.printdata("Excel_File_Name",excelFileName);
        xout.closetag();
        xout.opentag("edd");

        
        // Get the indexes of the columns we need to write out the XML for this EDD.
        int rows = sheet.getLastRowNum();
        int entityIndex    = findvalue("entity",sheet,0);
        int attributeIndex = findvalue("attribute",sheet,0);
        int typeIndex      = findvalue("type",sheet,0);
        int subtypeIndex   = findvalue("subtype",sheet,0);
        int defaultIndex   = findvalue("defaultvalue",sheet,0);
        int inputIndex     = findvalue("input",sheet,0);
        int accessIndex    = findvalue("access",sheet,0);
        int commentIndex   = findvalue("comment",sheet,0);      // optional
        int sourceIndex    = findvalue("source",sheet,0);       // optional
        
        // Some columns we just have to have.  Make sure we have them here.
        if(entityIndex <0 || attributeIndex < 0 || typeIndex < 0 || defaultIndex < 0 || accessIndex < 0 || inputIndex <0 ){
            String err = " Couldn't find the following column header(s): "+ 
              (entityIndex<0?" entity":"")+
              (attributeIndex<0?" attribute":"")+
              (typeIndex<0?" type":"")+ 
              (defaultIndex<0?" default value":"")+ 
              (accessIndex<0?" access":"")+
              (inputIndex<0?" input":"");
            throw new Exception("This EDD may not be valid, as we didn't find the proper column headers\n"+err);
        }
        
        // Go through each row, writing out each entry to the XML.
        for(int row = 1; row <=rows; row++){
            String entityname = getCellValue(sheet,row,entityIndex);    // Skip all the rows that have no Entity
            if(entityname.length()>0){
                String src     = sourceIndex>=0 ? getCellValue(sheet,row,sourceIndex):"";
                String comment = commentIndex>=0 ? getCellValue(sheet,row,commentIndex):"";
                xout.opentag("entry");
                xout.opentag("entity",
                        "entityname"        , entityname,
                        "attribute"         , getCellValue(sheet,row,attributeIndex),
                        "type"              , getCellValue(sheet,row,typeIndex),
                        "subtype"           , getCellValue(sheet,row,subtypeIndex),
                        "default"           , getCellValue(sheet,row,defaultIndex),
                        "access"            , getCellValue(sheet,row,accessIndex),
                        "input"             , getCellValue(sheet,row,inputIndex)
                );
                xout.closetag();
                if(comment.length()>0)xout.printdata("comment",getCellValue(sheet,row,commentIndex));
                if(src    .length()>0 )xout.printdata("source", getCellValue(sheet,row,sourceIndex));
                xout.closetag();                
            }
        }
        xout.closetag();
        xout.close();
    }
    /**
     * Pulls the ATTRIBUTE name out of the next cell.  The assumption is that
     * all ATTRIBUTES (Including the main sections of the decision table) are
     * all in the first column of a row, followed by a colon.
     * 
     * We ignore numeric Attribute names.
     * @param sheet
     * @param row
     * @return
     */
    private String getNextAttrib(HSSFSheet sheet, int row){
        String value      = getCellValue(sheet, row, 0).trim();
        int    colonIndex = value.indexOf(":");
        if(colonIndex>1){
           String attrib  = value.substring(0,colonIndex);
           attrib  = attrib.replaceAll(" ", "_").toLowerCase();
           try {
			  Integer.parseInt(attrib);
			  attrib = "";
		   } catch (NumberFormatException e) { }
           return attrib;
        }
        return "";
    }
   /**
    * Pulls the ATTRIBUTE Value out of the next cell.  If no attribute value
    * is found, then the value of column 0 is returned, whitespace trimmed off.
    * 
	* Ah, but we add a wrinkle.  If the value
    * we find in column 0 is a number, then we return the value from column
    * column 3.  
    *  
    * @param sheet
    * @param row
    * @return
    */
    private String getNextAttribValue(HSSFSheet sheet, int row){
        String value      = getCellValue(sheet, row, 0).trim();
        int    colonIndex = value.indexOf(":");
        if(colonIndex>1){
           value = value.substring(colonIndex+1).trim();
        }
        try{
        	Integer.parseInt(value);
        	value = getCellValue(sheet,row,2).trim();
        }catch(NumberFormatException e){};
        return value;
    }
    /**
     * Returns the value of the number column.  You don't have to have one 
     * of these. 
     * 
     * @param sheet
     * @param row
     * @return
     */
    private String getNumber(HSSFSheet sheet, int row){        
        int field = getColumn("number");
        if(field==-1)return ""; 
        String value = getCellValue(sheet,row, field);
        return value.trim();
    }
    /**
     * Any Section can get the DSL specified in the section from this call. 
     * 
     * @param sheet
     * @param row
     * @return
     */
    private String getDSL(HSSFSheet sheet, int row){        
        int field = getColumn("dsl");
        if(field==-1)throw new RuntimeException("No DSL Column"); 
        String value = getCellValue(sheet,row, field);
        return value.trim();
    }
    /**
     * Returns the contents of the Comment column... You don't have to have one
     * of these.
     *  
     * @param sheet
     * @param row
     * @return
     */
    private String getComments(HSSFSheet sheet, int row){
        int field = getColumn("comments");
        if(field==-1)return "";  
        String value = getCellValue(sheet,row, field);
        return value.trim();
    }
        
    /**
     * Returns the Table value
     * of these.
     *  
     * @param sheet
     * @param row
     * @return
     */
    private String getTableValue(HSSFSheet sheet, int row, int tableIndex){
        int field = getColumn("table");
        if(field==-1)return "";  
        String value = getCellValue(sheet,row, field+tableIndex);
        return value.trim();
    }
    
    /**
     * Returns the value of the Requirement Reference column... You don't have to have one
     * of these.
     *  
     * @param sheet
     * @param row
     * @return
     */
    private String getRequirement(HSSFSheet sheet, int row){
        int field = getColumn("requirement");
        if(field==-1)return "";  
        String value = getCellValue(sheet,row, field);
        return value.trim();
    }
    
    
    /**
     * Returns the index of the heading of the next block.
     * @param sheet
     * @param row
     * @return
     */
    int nextBlock(HSSFSheet sheet, int row){
        String attrib = getNextAttribValue(sheet, row);
        while(attrib == ""){
            row++;
            attrib = getNextAttribValue(sheet, row);
            if(row > sheet.getLastRowNum()) return row-1;
        }
        return row;
    }
    
    /**
     * Reads the decision table out of an Excel spreadsheet and generates the
     * approriate XML. 
     * @param file
     * @param sb
     * @return true if at least one decision table was found in this file
     * @throws Exception
     */
	public boolean convertDecisionTable(File file,XMLPrinter out) throws Exception{
		if(! (file.getName().endsWith(".xls"))) return false; 
		
		InputStream input = new FileInputStream(file.getAbsolutePath());
        POIFSFileSystem fs = new POIFSFileSystem( input );
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        boolean tablefound = false;
        for(int i=0; i< wb.getNumberOfSheets(); i++){
            tablefound |= convertOneSheet(file.getName(),wb.getSheetAt(i),out);
        }
        return tablefound;
        
	}    
    /**
     * Returns true if the given sheet describes a valid DecisionTable.
     *
     * @param filename
     * @param sheet
     * @param sb
     * @return
     */
    private boolean convertOneSheet(String filename, HSSFSheet sheet,XMLPrinter out){    
        
        columns = defaultColumns;
        
        // The first row of a decision table has to provide the decision table name.  This is required!
        // Must be the first row, must have a NAME: tag, followed by a decision table name!
        String attrib = getNextAttrib(sheet, 0);
        String value  = getNextAttribValue(sheet, 0);
        if(!attrib.equalsIgnoreCase("name") || value.length()==0){
        	return false;
        }
        out.opentag("decision_table");
        
        String dtName = value.replaceAll("[\\s]+", "_");
        
        ArrayList<String> attributes = new ArrayList<String>();
        
        int rowIndex = 1;
        // Go through the attribute rows, identified by some tag followed by a colon.  When we reach a heading 
        // tag of CONDITIONS: then we stop!  These headers we preserve in the resulting XML.
        for(rowIndex=1;true;rowIndex++){
            
          attrib = getNextAttrib(sheet, rowIndex);
          value  = getNextAttribValue(sheet, rowIndex);  
          
          // Once we transitioned to the table upon seeing the CONDITIONS: tag.  We are
          // grandfathering that into our parsing.  Any other segment (Context or Initial Actions)
          // requires a blank line to step the processing on.  First optional segment is Contexts:,
          // followed by Initial_Actions:, followed by the Condition table.
          if( attrib.equalsIgnoreCase("conditions") || (attrib.length()==0 && value.length()==0)){  
              break;
          }
          String v = value.trim();
          attributes.add(attrib); 
          attributes.add(v);
          
          // If we have a columns attribute, then the column numbers and order are specfied there.
          // COLUMNS:  number, comment, requirements, dsl, table                                                                        
          if(attrib.equalsIgnoreCase("columns")){
              v = v.substring(2).trim();
              columns = v.split("[,\\s]+");
          }
          
        }  
        out.printdata("table_name",dtName);
        out.printdata("xls_file",filename);
        out.opentag("attribute_fields");
          for(int i=0; i< attributes.size(); i+=2){
              out.printdata(attributes.get(i),attributes.get(i+1));
          }
        out.closetag();
        
        rowIndex = nextBlock(sheet, rowIndex);
        attrib = getNextAttrib(sheet, rowIndex);
        
        if(attrib.equalsIgnoreCase("contexts")){
            rowIndex++;
            out.opentag("contexts");
            while(true){
                attrib           = getNextAttrib(sheet, rowIndex);
                if(attrib.length()>0)break;   
                String context   = getDSL(sheet, rowIndex);
                if(context != "") 
                {
                    out.opentag("context_details");
                    out.printdata("context_description",context);
                    out.closetag();
                }
                rowIndex++;
            }
            out.closetag();
        }
        
        rowIndex = nextBlock(sheet, rowIndex);
        attrib = getNextAttrib(sheet, rowIndex);
        
        if(attrib.equalsIgnoreCase("initial_actions")){
            rowIndex++;
            out.opentag("initial_actions");
            while(isAction(sheet,rowIndex)){                 
                String initialActionDescription = getDSL(sheet, rowIndex); 
    
                if(initialActionDescription != "") 
                {
                    out.opentag("initial_action_details");
                    String actionNumber = getNumber(sheet, rowIndex); 
                    out.printdata("initial_action_number",actionNumber);
                    
                    String initialActionComment = getComments(sheet, rowIndex);
                    out.printdata("initial_action_comment",initialActionComment);
    
                    out.printdata("initial_action_description",initialActionDescription);
                    out.closetag();
                }
                rowIndex++;
            }
            out.closetag();
        }
        
        rowIndex = nextBlock(sheet, rowIndex);
        attrib = getNextAttrib(sheet, rowIndex);
        rowIndex++;
        
        out.opentag("conditions");
        while(isCondition(sheet, rowIndex)){
            
        	String conditionDescription = getDSL(sheet, rowIndex); 

        	if(conditionDescription != "") {
        		out.opentag("condition_details");
	        	String conditionNumber = getNumber(sheet, rowIndex); 
                out.printdata("condition_number",conditionNumber);
	        	
	        	String conditionComment = getNumber(sheet, rowIndex);
                out.printdata("condition_comment",conditionComment);

                out.printdata("condition_description",conditionDescription);
	        	
	        	for(int j=0; j<16;j++){
	        		String columnValue =getTableValue(sheet, rowIndex, j);  
	        		if(columnValue.equals(""))columnValue = "-";
	        		if ((columnValue.equalsIgnoreCase("*")) ||
	        		    (columnValue.equalsIgnoreCase("-")) || 
	        		    (columnValue.equalsIgnoreCase("y")) || 
	        		    (columnValue.equalsIgnoreCase("n"))) {	               
	        		    out.printdata("condition_column","column_number",""+(j+1),"column_value",columnValue,null);
	        		}else{
	//        			if(columnValue != "")
	//	                throw new Exception("Undesired value in the condition matrix");
	        		}
	        	}
                out.closetag();
        	}
            rowIndex++;
        }
        out.closetag();
        
        rowIndex = nextBlock(sheet, rowIndex);
        attrib = getNextAttrib(sheet, rowIndex);
        rowIndex++;
        
        out.opentag("actions");
        	while(isAction(sheet,rowIndex)){
        	    String actionDescription = getDSL(sheet, rowIndex);  
                if(actionDescription.length()>0){
                    out.opentag("action_details");
            		String actionNumber = getNumber(sheet, rowIndex);
            		out.printdata("action_number",actionNumber);
                	
                	String actionComment = getComments(sheet, rowIndex); 
                    out.printdata("action_comment",actionComment);
                	
                    out.printdata("action_description",actionDescription);
                	
                	for(int j=0; j<16;j++){
                		String columnValue = getTableValue(sheet, rowIndex, j);  
                		if (columnValue.equalsIgnoreCase("x") ||
                			columnValue.equalsIgnoreCase("s")    ) {
                            out.printdata("action_column","column_number",""+(j+1),"column_value",columnValue,null);
                		}else{
                			if(columnValue.length() != 0){
        	                   System.out.println(dtName+": Undesired value '"+columnValue+"' in the action matrix ("+j+","+rowIndex+")");
                			}  
                		}
                	}
                    out.closetag();
                }	
            	rowIndex++;  
            	
        	}
            out.closetag();
            out.closetag();
        	return true;
	}
	
	/**
	 * We used to do something really smart.  Now we just return false at the end of the spread
	 * sheet or if we encounter another block.
	 * @param sheet
	 * @param rowIndex
	 * @return
	 */
	private boolean isAction(HSSFSheet sheet, int rowIndex){
	     String attrib = getNextAttrib(sheet, rowIndex);
	     if (attrib.length()>0) return false;
		 if(rowIndex > sheet.getLastRowNum()) return false;
	     return true;
	}
	
    /**
     * We used to do something really smart.  Now we just return false at the end of the spread
     * sheet or if we encounter another block.
     * @param sheet
     * @param rowIndex
     * @return
     */
    private boolean isCondition(HSSFSheet sheet, int rowIndex){
         String attrib = getNextAttrib(sheet, rowIndex);
         if (attrib.length()>0) return false;
         if(rowIndex > sheet.getLastRowNum()) return false;
         return true;
    }
	
	public void setContents(File aFile, String aContents)
    throws FileNotFoundException, IOException {
		if (aFile == null) {
		throw new IllegalArgumentException("File should not be null.");
		}
		
		//declared here only to make visible to finally clause; generic reference
		Writer output = null;
		try {
		//use buffering
		//FileWriter always assumes default encoding is OK!
		output = new BufferedWriter( new FileWriter(aFile) );
		output.write( aContents );
		}
		finally {
		//flush and close both "output" and its underlying FileWriter
		if (output != null) output.close();
		}
	}
	

}

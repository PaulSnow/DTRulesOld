package com.dtrules.compiler.excel.util;

import java.io.FileOutputStream;

import com.dtrules.admin.IRulesAdminService;
import com.dtrules.session.RulesDirectory;

public class Rules2Excel {
    public static void writeDecisionTables(IRulesAdminService admin, String excelName){
        try{
            RulesDirectory rd = admin.getRulesDirectory();
            String tablefilename = rd.getSystemPath()+excelName;
            FileOutputStream dt = new FileOutputStream(tablefilename);
        }catch(Exception e){
            
        }
    }
}

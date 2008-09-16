package com.dtrules.example2;

import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.operators.ROperator;
import com.dtrules.session.DTState;

public class UserOperators {
    
    static {
        new PrintMessage();
    }
       /**
        * ( Object --  )
        * 
        * Prints the string representation of the object to System.out
        * @author Paul Snow
        *
        */
        private static class PrintMessage extends ROperator {
            PrintMessage (){super("printmessage"); alias("aaa");}

            public void execute(DTState state) throws RulesException {
                String o = state.datapop().stringValue();
                System.out.println(o);
            }
        }

}    





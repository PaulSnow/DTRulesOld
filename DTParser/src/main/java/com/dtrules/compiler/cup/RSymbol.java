/**
 * 
 */
package com.dtrules.compiler.cup;

import java_cup.runtime.Symbol;

/**
 * @author ps24876
 *
 * We return this symbol only if the identifier is local.
 */
public class RSymbol extends Symbol {
    boolean local      = false;
    String  leftvalue;
    String  rightvalue;
    /**
     * @param id
     * @param l
     * @param r
     * @param o
     */
    public RSymbol(boolean local, Symbol s) {
        super(s.sym);
        this.local       = local;
        this.left        = s.left;
        this.parse_state = s.parse_state;
        this.right       = s.right;
        this.sym         = s.sym;
        this.value       = s.value;
        if(s.value != null) this.rightvalue  = s.value.toString();
    }
}

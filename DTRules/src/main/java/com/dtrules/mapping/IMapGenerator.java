/**
 * 
 */
package com.dtrules.mapping;

import java.io.InputStream;

import com.dtrules.xmlparser.XMLPrinter;

/**
 * @author ps24876
 *
 */
public interface IMapGenerator {

    public abstract void generateMapping(String mapping, String inputfile,
            String outputfile) throws Exception;

    /**
     * Given an EDD XML, makes a good stab at generating a Mapping file for a given
     * mapping source.  The mapping source is specified as an input in the input column
     * in the EDD.
     * @param mapping
     * @param input
     * @param out
     * @throws Exception
     */
    public abstract void generateMapping(String mapping, InputStream input,
            XMLPrinter out) throws Exception;

}
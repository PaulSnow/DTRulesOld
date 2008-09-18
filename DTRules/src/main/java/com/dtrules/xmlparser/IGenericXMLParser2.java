/**
 * 
 */
package com.dtrules.xmlparser;

/**
 * This is a simple interface for handling parsing events
 * from the generic parser.  At this time, only two events
 * are generated, the beginTag and the endTag events.  The
 * endTag() provides the body, if present.
 * Creation date: (9/15/2003 8:35:17 AM)
 * @author: Paul Snow, MTBJ, Inc.
 */
public interface IGenericXMLParser2 extends IGenericXMLParser {
       /**
        * Called with the contents of a comment line.
        * @param comment
        */
       void comment(String comment);
       
       /**
        * Called with the contexts of a header line
        * @param header
        */
       void header(String header);
}
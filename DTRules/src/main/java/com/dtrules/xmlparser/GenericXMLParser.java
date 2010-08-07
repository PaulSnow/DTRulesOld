/* The following code was generated by JFlex 1.4.3 on 8/6/10 3:09 AM */

/** 
 * Copyright 2004-2009 DTRules.com, Inc.
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
 **/ 
package com.dtrules.xmlparser;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.io.IOException;
@SuppressWarnings({"unchecked","unused"})

/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 8/6/10 3:09 AM from the specification file
 * <tt>C:/maximus/eb_dev2/RulesEngine/DTRules/src/main/java/com/dtrules/xmlparser/GenericXMLParser.flex</tt>
 */
public class GenericXMLParser {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int EndTag = 10;
  public static final int GetEndTag = 6;
  public static final int NestedTag = 12;
  public static final int YYINITIAL = 0;
  public static final int Tag = 4;
  public static final int Attributes = 2;
  public static final int GetNestedEndTag = 8;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6, 6
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\7\1\6\1\0\1\7\1\5\22\0\1\7\1\26\1\11"+
    "\3\0\1\0\1\10\5\0\1\3\1\2\1\31\12\2\1\4\1\0"+
    "\1\12\1\30\1\25\1\27\1\0\2\1\1\13\1\21\26\1\4\0"+
    "\1\1\1\0\1\15\5\1\1\23\1\14\1\22\10\1\1\16\1\20"+
    "\1\24\2\1\1\17\3\1\uff85\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\2\1\1\2\1\0\1\3\2\1\1\4\7\3"+
    "\1\5\1\3\1\6\1\3\1\7\1\3\1\10\1\1"+
    "\1\11\1\12\1\13\1\2\1\14\1\15\1\4\13\0"+
    "\1\16\2\0\1\17\1\20\11\0\1\6\1\10\1\12"+
    "\1\0\1\21\1\0\1\22\3\0\1\23";

  private static int [] zzUnpackAction() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\32\0\64\0\116\0\150\0\202\0\234\0\266"+
    "\0\320\0\266\0\352\0\u0104\0\u011e\0\u0138\0\u0152\0\u016c"+
    "\0\u0186\0\u01a0\0\266\0\u01ba\0\u01d4\0\u01ee\0\266\0\u0208"+
    "\0\u0222\0\u023c\0\266\0\u0256\0\266\0\u0270\0\u028a\0\266"+
    "\0\u02a4\0\u02be\0\u02d8\0\u02f2\0\u030c\0\u0152\0\u0326\0\u0340"+
    "\0\u035a\0\u0374\0\u038e\0\u03a8\0\266\0\u03c2\0\u03dc\0\266"+
    "\0\266\0\u03f6\0\u0410\0\u042a\0\u0444\0\u045e\0\u0478\0\u0492"+
    "\0\u04ac\0\u04c6\0\u04e0\0\u04fa\0\u0514\0\u052e\0\266\0\u0548"+
    "\0\266\0\u0562\0\u057c\0\u0596\0\266";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\5\10\1\11\2\12\2\10\1\13\1\14\3\10\1\15"+
    "\1\10\1\16\11\10\1\17\3\10\1\11\2\12\3\10"+
    "\1\20\3\17\1\21\1\17\1\22\3\17\1\23\3\10"+
    "\1\24\1\10\1\25\3\10\1\11\2\12\2\10\1\26"+
    "\12\25\1\27\3\10\1\30\1\0\1\31\3\0\3\32"+
    "\3\0\12\31\1\33\5\0\1\34\3\0\3\32\3\0"+
    "\12\34\1\35\4\0\12\36\1\37\12\36\1\40\4\36"+
    "\5\10\1\11\2\12\2\10\1\41\1\14\3\10\1\15"+
    "\1\10\1\16\10\10\40\0\1\12\51\0\1\42\1\43"+
    "\16\0\1\44\35\0\1\10\33\0\1\45\10\0\3\46"+
    "\1\47\3\50\3\0\12\46\3\0\1\51\2\0\3\46"+
    "\1\47\3\50\3\0\1\46\1\52\10\46\3\0\1\51"+
    "\2\0\3\46\1\47\3\50\3\0\5\46\1\17\4\46"+
    "\3\0\1\51\2\0\3\46\1\47\3\50\3\0\7\46"+
    "\1\53\2\46\3\0\1\51\26\0\1\23\5\0\3\25"+
    "\1\54\6\0\12\25\33\0\1\42\30\0\1\55\5\0"+
    "\3\31\1\56\6\0\12\31\12\0\3\32\23\0\3\34"+
    "\1\57\6\0\12\34\5\0\12\36\1\0\12\36\1\0"+
    "\4\36\26\0\1\42\2\0\1\60\26\0\1\42\2\0"+
    "\1\61\3\0\1\62\26\0\27\43\1\63\2\43\15\0"+
    "\1\64\37\0\1\65\7\0\1\66\11\0\12\66\12\0"+
    "\3\50\20\0\1\51\6\0\3\51\1\67\1\70\21\0"+
    "\3\46\1\47\3\50\3\0\2\46\1\71\7\46\3\0"+
    "\1\51\2\0\3\46\1\47\3\50\3\0\10\46\1\72"+
    "\1\46\3\0\1\51\2\0\1\73\11\0\12\73\6\0"+
    "\1\74\11\0\12\74\6\0\1\75\11\0\12\75\10\0"+
    "\1\76\26\0\25\43\1\77\4\43\16\0\1\10\35\0"+
    "\1\100\10\0\3\66\1\0\3\50\3\0\12\66\3\0"+
    "\1\51\1\0\10\67\1\101\21\67\11\70\1\101\20\70"+
    "\1\0\3\46\1\47\3\50\3\0\3\46\1\17\6\46"+
    "\3\0\1\51\2\0\3\46\1\47\3\50\3\0\7\46"+
    "\1\102\2\46\3\0\1\51\2\0\3\73\7\0\12\73"+
    "\6\0\3\74\7\0\12\74\6\0\3\75\7\0\12\75"+
    "\5\0\3\76\1\103\26\76\24\0\1\10\6\0\3\46"+
    "\1\47\3\50\3\0\11\46\1\17\3\0\1\51\1\0"+
    "\3\76\1\104\53\76\1\105\4\76";

  private static int [] zzUnpackTrans() {
    int [] result = new int[1456];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\3\1\1\0\1\11\1\1\1\11\10\1\1\11"+
    "\3\1\1\11\3\1\1\11\1\1\1\11\2\1\1\11"+
    "\1\1\13\0\1\11\2\0\2\11\11\0\3\1\1\0"+
    "\1\11\1\0\1\11\3\0\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
    private String sourcename = "unknown name";
    String            		tagstk []       = new String[1000];
    int               		tagstkptr       = 0;
    int               		statestk []     = new int[1000];
    int               		statestkptr     = 0;
    ArrayList         		attribstk       = new ArrayList();
    HashMap<String,String>    attribs         = new HashMap<String,String>();
    boolean           		printflg        = true;
    IGenericXMLParser2 		parser          = null;
    
    String            		body            = "";
    String            		currenttag      = "";
    String 		     		Source;

	public GenericXMLParser(String filename) throws FileNotFoundException {
		this(new FileInputStream(filename));
		sourcename = filename;
	}

    void pushTag(String tag){
       tagstk[tagstkptr++] = tag;
       currenttag = tag;
       attribstk.add(attribs);    // We save and restore the attribs hashmap
       attribs = new HashMap();
    }
    
    String popTag(String endtag) {
       attribs = (HashMap) attribstk.remove(attribstk.size()-1);	
       if(tagstkptr<=0){
          System.err.print("Stack Underflow\n");
       }
       String tag = tagstk[--tagstkptr];
       if(!tag.equals(endtag)){
          System.err.print("Begin and End tags do not match:\n"+
                           "Begin tag: "+tag+"\n"+
                           "End tag:   "+endtag+"\n");
       }

       if(tagstkptr<=0){
          currenttag = "";
       }else{
          currenttag = tagstk[tagstkptr-1];
       }

       return tag;
    }

	public void setSource(String source){
	   this.sourcename=source;
	}

   class Shell implements IGenericXMLParser2 {
        IGenericXMLParser p;
        Shell(IGenericXMLParser p){this.p = p;}
        public void comment(String comment) {}
        public void header(String header) {}
        public void beginTag(String[] tagstk, int tagstkptr, String tag,
                HashMap<String, String> attribs) throws IOException, Exception {
            p.beginTag(tagstk, tagstkptr, tag, attribs);
        }
        public void endTag(String[] tagstk, int tagstkptr, String tag,
                String body, HashMap<String, String> attribs) throws Exception,
                IOException {
            p.endTag(tagstk, tagstkptr, tag, body, attribs);
        }
        public boolean error(String v) throws Exception {
            return p.error(v);
        }
        
    }
    public void setParser (IGenericXMLParser p_parser){
        parser = new Shell(p_parser);
    }

    public void setParser (IGenericXMLParser2 p_parser){
        parser = p_parser;
    }

    String getcomment(){
        String s = yytext();
        s = yytext().substring(4,s.length()-3);
        return s;
    }
    
    String getheader(){
        String s = yytext();
        s = yytext().substring(2, s.length()-2);
        return s;
    }
    void pushstate(int state) { 
       statestk[statestkptr++]=yystate();
       yybegin(state); 
    }

    int  popstate (){ 
       int newstate = statestk[--statestkptr];
       statestk[statestkptr]=0;
       yybegin(newstate);
       return newstate;  
    }
    
    void error(String v)throws Exception{
       if(!parser.error(v)){
         throw new Exception("Unmatched characters, parsing cannot continue at" + where());
       }  
    }

    static private Pattern xamp = Pattern.compile("&amp;"); 
    static private Pattern xlt = Pattern.compile("&lt;"); 
    static private Pattern xgt = Pattern.compile("&gt;"); 
    static private Pattern xsqu = Pattern.compile("&apos;");
    static private Pattern xdqu = Pattern.compile("&quot;"); 

     static public String unencode (String s){
         if(s.indexOf("&")>=0){
             s= xlt.matcher(s).replaceAll("<");
             s= xgt.matcher(s).replaceAll(">");
             s= xsqu.matcher(s).replaceAll("'");
             s= xdqu.matcher(s).replaceAll("\"");
             s= xamp.matcher(s).replaceAll("&");
         }
        return s;
      } 
     static private Pattern tst = Pattern.compile("[&<>'\"]+");
     static private Pattern amp = Pattern.compile("&"); 
     static private Pattern lt = Pattern.compile("<"); 
     static private Pattern gt = Pattern.compile(">"); 
     static private Pattern squ = Pattern.compile("'");
     static private Pattern dqu = Pattern.compile("\""); 
     
     static public String encode (String s) {
         if(tst.matcher(s).find()){
             s= amp.matcher(s).replaceAll("&amp;");
             s= lt.matcher(s).replaceAll("&lt;");
             s= gt.matcher(s).replaceAll("&gt;");
             s= squ.matcher(s).replaceAll("&apos;");
             s= dqu.matcher(s).replaceAll("&quot;");
         }
        return s;
      }


  static public StringBuffer encode (StringBuffer sb) {

	return new StringBuffer(encode(sb.toString()));	

  }    
    

  public String where() {
  	return sourcename + ":" + (yyline+1) + "." + (yycolumn+1) ;
  }

  public int getYYLine() { return yyline+1;}
  public int getYYColumn() { return yycolumn+1;}
  
   /**
     * Loads an XML file with the given Generic Parser.
     * <br><br>
     * @param file  An inputStream providing the XML
     * @param gp    A parser implementing the IGenericXMLParser interface.
     */
    static public void load(java.io.InputStream xmlStream, IGenericXMLParser2 gp) throws Exception{
        GenericXMLParser parser = new GenericXMLParser(xmlStream);
        parser.setParser(gp);
        while(true){
            if(GenericXMLParser.YYEOF == parser.yylex()) break;
        }
     } 
     
    /**
	 * Loads an XML file with the given Generic Parser.
	 * <br><br>
	 * @param file  An inputStream providing the XML
	 * @param gp    A parser implementing the IGenericXMLParser interface.
	 */
    static public void load(java.io.InputStream xmlStream, IGenericXMLParser gp) throws Exception{
	    GenericXMLParser parser = new GenericXMLParser(xmlStream);
        parser.setParser(gp);
		while(true){
			if(GenericXMLParser.YYEOF == parser.yylex()) break;
		}
	 } 
	
	/**
      * Loads an XML file with the given Generic Parser.
      * @param xmlStream
      * @param gp
      * @throws Exception
      */
     static public void load(java.io.Reader xmlStream, IGenericXMLParser2 gp) throws Exception{
        GenericXMLParser parser = new GenericXMLParser(xmlStream);
        parser.setParser(gp);
        while(true){
            if(GenericXMLParser.YYEOF == parser.yylex()) break;
        }
     } 
	 
	 /**
	  * Loads an XML file with the given Generic Parser.
	  * @param xmlStream
	  * @param gp
	  * @throws Exception
	  */
     static public void load(java.io.Reader xmlStream, IGenericXMLParser gp) throws Exception{
		GenericXMLParser parser = new GenericXMLParser(xmlStream);
		parser.setParser(gp);
		while(true){
			if(GenericXMLParser.YYEOF == parser.yylex()) break;
		}
	 } 

    /**
     * Loads an XML file with the given Generic Parser, extended from AGenericXMLParser.  This
     * parser allows the "calling" of other AGenericXMLParser's to process tags.  The process
     * all of the given tag (and sub tags).  Then processing returns to the original parser.
     * <br><br>
     * @param file  An inputStream providing the XML
     * @param gp    A parser implementing the IGenericXMLParser interface.
     */
    static public void load(java.io.InputStream xmlStream, AGenericXMLParser gp) throws Exception{
        GenericXMLParser      parser = new GenericXMLParser(xmlStream);
        GenericXMLParserStack stack  = new GenericXMLParserStack(parser);
        gp.genericXMLParserStack = stack;
        stack.parseTagWith(gp);
        parser.setParser(stack);
        while(true){
            if(GenericXMLParser.YYEOF == parser.yylex()) break;
        }
     } 
  
    


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public GenericXMLParser(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public GenericXMLParser(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 90) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int yylex() throws java.io.IOException, Exception {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 3: 
          { error(yytext());
          }
        case 20: break;
        case 4: 
          { pushstate(Tag);
          }
        case 21: break;
        case 10: 
          { String endTag = yytext();
     parser.endTag(tagstk,tagstkptr,currenttag,"",attribs);
     popTag(endTag);
          }
        case 22: break;
        case 14: 
          { parser.beginTag(tagstk,tagstkptr,currenttag,attribs);
     parser.endTag(tagstk,tagstkptr,currenttag,"",attribs);

     popTag(currenttag);
     popstate();
     return 1;
          }
        case 23: break;
        case 15: 
          { popstate();
     pushstate(GetEndTag);
          }
        case 24: break;
        case 19: 
          { parser.comment(getcomment());
          }
        case 25: break;
        case 12: 
          { popstate();
     pushstate(NestedTag);
     pushstate(Tag);
          }
        case 26: break;
        case 6: 
          { pushTag(yytext());
     body="";
     pushstate(Attributes);
          }
        case 27: break;
        case 2: 
          { String text = yytext();
     body += text;
          }
        case 28: break;
        case 13: 
          { body += yytext(); error(yytext());
          }
        case 29: break;
        case 5: 
          { yypushback(1);
     if(yytext().indexOf("/")>=0) yypushback(1);
     popstate();
          }
        case 30: break;
        case 18: 
          { String text  = yytext();
     String key   = text.substring (0,text.indexOf('=')).trim();
     String value = text.substring (text.indexOf('=')+1).trim();
	 char delimiter = value.charAt(0);
	 value = value.substring(1,value.lastIndexOf(delimiter));
	 
     attribs.put(key,unencode(value));
          }
        case 31: break;
        case 16: 
          { popstate();
     pushstate(GetNestedEndTag);
          }
        case 32: break;
        case 7: 
          { parser.beginTag(tagstk,tagstkptr,currenttag,attribs);
     popstate();
     pushstate(EndTag);
          }
        case 33: break;
        case 17: 
          { parser.header(getheader());
          }
        case 34: break;
        case 11: 
          { popstate();
     return 1;
          }
        case 35: break;
        case 8: 
          { String endTag = yytext();
     parser.endTag(tagstk,tagstkptr,currenttag,unencode(body),attribs);
     popTag(endTag);
          }
        case 36: break;
        case 9: 
          { popstate();
          }
        case 37: break;
        case 1: 
          { 
          }
        case 38: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            return YYEOF;
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}

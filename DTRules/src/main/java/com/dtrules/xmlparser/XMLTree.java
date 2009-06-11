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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ps24876
 *
 */
public class XMLTree {
    
    public enum NodeType {
        Root,
        Header,
        Comment,
        Tag
    };
    
    public static interface NoteDifference {
        void AttributeDiff(Node n);
        void BodyDiff(Node n);
        void AttributeBodyDiff(Node n);
        void NewTag(Node n);
        void DeletedTag(Node n);
    }
    
    public static class Node {
        final private String                  name;
        final private NodeType                type;
        final private HashMap<String,String>  attributes;
        private ArrayList<Node>               tags        = new ArrayList<Node>();
        private String                        body;
        private Node                          parent;
        
        /**
         * Find a Node under this node with the given tagName.  Return 
         * null if it isn't found.
         */
        public Node findTag(String tagName){
            return findTag(this,tagName);
        }
        /**
         * Find the next Node  in a subtree with the given name.  Return
         * null if it isn't found.
         * @param subtree
         * @param tagName
         * @return
         */
        private Node findTag(Node subtree, String tagName){
            if (subtree.getName().equals(tagName)){
                return subtree;
            }
            for(Node n : subtree.getTags()){
                Node f = findTag(n,tagName);
                if(f != null){
                    return f;
                }
            }
            return null;
        }
        
        public Node( String name, NodeType type, HashMap<String,String> attribs, Node parent){
            this.type       = type;
            this.parent     = parent;
            this.attributes = attribs;
            this.name       = name;
        }
        
        public enum MATCH {
            match,                      // Node matches in every way.
            differentType,              // Type Node is different
            differentBody,              // Type is the same, but the body is different
            differentAttributes,        // Type is the same, but attributes are different
            differentBodyAttributes,    // Type is the same, but the body and the attributes are different
        }
        
        private String fix(String s, boolean whitespace ){
            if(s==null)return "";
            if(whitespace)return s;
            s = s.trim().replaceAll("[\r\n\t]"," ");
            while(s.indexOf("  ")>=0) {
                s = s.replaceAll("  "," ");
            }
            return s;
        }
        
        /**
         * Compares this node to another node.  If they are exactly the same, we return true.  Otherwise
         * we return false.  If whitespace is true, we insist whitespace matches;  Otherwise we disregard
         * differences in line breaks and spaces (reducing all white space to a single space before the
         * compare).
         * 
         * @param n
         * @param whitespace
         * @return code
         */
        public MATCH compareToNode(Node n, boolean whitespace){
            
            if(!type.equals(n.getType())){      // If not the same type, it makes no sense to
                return MATCH.differentType;     // compare further...
            }
            boolean attribs = compareAttributes(this.attributes,n.getAttributes()) &&
                              compareAttributes(n.attributes   ,this.getAttributes());
            boolean body    = fix(this.body,whitespace).equals(fix(n.getBody(),whitespace));
            if(attribs && body ) {
                return MATCH.match;
            }
            if(!body && !attribs ){
                return MATCH.differentBodyAttributes;
            }
            if(!body){
                return MATCH.differentBody;
            }
            
            return MATCH.differentAttributes;   // Only case that remains...
        }
        
        /**
         * Compare the attributes of two nodes
         * @param a1
         * @param a2
         * @return
         */
        private boolean compareAttributes(HashMap<String,String> a1, HashMap<String,String> a2){
            for(String key : a1.keySet()){
                String v1 = a1.get(key);
                String v2 = a2.get(key);
                if(v2==null) return false;
                if(!v1.equals(v2)) return false;
            }
            return true;
        }
        /**
         * Compares two node trees to see if they are exactly equal
         * @param n
         * @param whitespace
         * @return
         */
        public boolean absoluteMatch(Node n, boolean whitespace){
            
            if(compareToNode(n,whitespace) != MATCH.match){
                return false;
            }
            if(tags.size() != n.getTags().size()){ 
                return false; 
            }
            for(int i=0;i<tags.size();i++){
                if(!tags.get(i).absoluteMatch(n.getTags().get(i),whitespace)){
                    return false;
                }
            }
            return true;
        }
        
        public void addTag(Node tag){
            tags.add(tag);
        }
        
        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the body
         */
        public String getBody() {
            return body;
        }

        /**
         * If we have a null length body, we set a null; makes processing
         * the tree later a bit easier.
         * 
         * @param body the body to set
         */
        public void setBody(String body) {
            if(body != null && body.length() == 0) body = null;
            this.body = body;
        }

        /**
         * @return the parent
         */
        public Node getParent() {
            return parent;
        }

        /**
         * @param parent the parent to set
         */
        public void setParent(Node parent) {
            this.parent = parent;
        }

        /**
         * @return the type
         */
        public NodeType getType() {
            return type;
        }

        /**
         * @return the attributes
         */
        public HashMap<String, String> getAttributes() {
            return attributes;
        }

        /**
         * @return the tags
         */
        public ArrayList<Node> getTags() {
            return tags;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return name.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return name;
        }
        
    }
        
    private static class Loader implements IGenericXMLParser2 {
 
        private Node root = new Node("root node",NodeType.Root, new HashMap<String,String>(), null);
        private boolean keepComments;
        private boolean keepHeader;
        
        Loader(boolean keepComments, boolean keepHeader){
            this.keepComments = keepComments;
            this.keepHeader   = keepHeader;
        }
        
        private Node currentTag = root;
        
        public void beginTag(String[] tagstk, int tagstkptr, String tag,
                HashMap<String, String> attribs) throws IOException, Exception {
            
            Node n = new Node(tag, NodeType.Tag, attribs, currentTag);
            
            currentTag.addTag(n);
            currentTag = n;
        
        }

        public void endTag(String[] tagstk, int tagstkptr, String tag,
                String body, HashMap<String, String> attribs) throws Exception,
                IOException {
            currentTag.body = body;
            currentTag = currentTag.parent;
        }

        public boolean error(String v) throws Exception {
            return false;
        }

        public void comment(String comment) {
            if(!keepComments) return;
            Node c = new Node("comment node",NodeType.Comment,new HashMap<String,String>(), currentTag);
            c.body = comment;
            c.addTag(c);
        }

        public void header(String header) {
            if(!keepHeader) return;
            Node c = new Node("header node", NodeType.Header,new HashMap<String,String>(), currentTag);
            c.body = header;
            c.addTag(c);            
        }
     
        Node loadStream(InputStream s) throws Exception {
            GenericXMLParser.load(s,this);
            return root;
        }

        
        
    }
    
    static public Node BuildTree(String filename, boolean keepHeader, boolean keepComments) throws Exception {
        FileInputStream f = new FileInputStream(filename);
        return BuildTree(f, keepHeader, keepComments);
    }
    
    static public Node BuildTree(InputStream f, boolean keepHeader, boolean keepComments) throws Exception {
        Loader l = new Loader(keepComments, keepHeader);
        return l.loadStream(f);
    }
    
   
    
    /**
     * Bubble sort with quick out. Very fast on previously sorted data
     * and pretty fast on nearly sorted data.
     * @param array
     */
    public static void sortByAttribute(boolean ascending, ArrayList<Node> nodes, String attribute){
        int fence = nodes.size()-1;
        boolean sorted = false;
        for(int i=0; i < fence && !sorted ; i++){
            for(int j = 0; j < fence-i; j++){
                Node jth = nodes.get(j);
                Node jplusOne = nodes.get(j+1);
                if( jth.getAttributes().get(attribute).toString().compareTo(
                        jplusOne.getAttributes().get(attribute).toString())>0 ^ !ascending){
                    sorted = false;
                    nodes.set(j,jplusOne);
                    nodes.set(j+1,jth);
                }
            }
        }
    }
    
    
}

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

package com.dtrules.test;

import junit.framework.TestCase;

/**
 * <p>Title : DTRules</p>
 * <p>Description : The Rules Engine</p>
 * <p>Date : Jan 26, 2005 , 4:41:14 PM</p>
 *
 * @author Prasath Ramachandran
 * @version $Revision: 1.0 $
 */

public class BaseTest extends TestCase {
	
	public BaseTest(String arg) {
        super(arg);
    }

    public void assertNotEqual(String message, Object expected, Object actual) {
        if (expected == null) {
            if (actual == null) {
                fail("They are equal");
            }
        } else {
            if (actual != null) {
                if (actual.equals(expected)) {
                    fail("They are equal");
                }
            }
        }
    }
}

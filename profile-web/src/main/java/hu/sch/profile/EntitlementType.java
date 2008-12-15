/*
 *  Copyright 2008 konvergal.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package hu.sch.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author konvergal
 */
public enum EntitlementType {
    TAG() {
        @Override
        public String toString() {
            return "tag";
        }
        
        @Override
        public String getLdapName() {
            return "tag";
        }
    },
    
    KORVEZETO {
        @Override
        public String toString() {
            return "körvezető";
        }
        
        @Override
        public String getLdapName() {
            return "korvezeto";
        }
    },
    
    GAZDASAGIS {
        @Override
        public String toString() {
            return "gazdaságis";
        }
        
        @Override
        public String getLdapName() {
            return "gazdasagis";
        }
    };
    
    public String getLdapName() {
        return "";
    }

    public static EntitlementType get(String ldapName) {
        EntitlementType entitlementType = null;
        for (EntitlementType e : EntitlementType.values()) {
            if (e.getLdapName().equals(ldapName)) {
                entitlementType = e;
            }
        }
        
        return entitlementType;
    }
}

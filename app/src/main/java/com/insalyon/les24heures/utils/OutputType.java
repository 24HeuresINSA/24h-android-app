package com.insalyon.les24heures.utils;

import com.insalyon.les24heures.R;

/**
 * Created by remi on 26/12/14.
 */
public enum OutputType {
        MAPS ("Maps"),
        LIST ("List");

        private String name = "";

        OutputType(String name){
            this.name = name;
        }

        public String toString(){
            return name;
        }
    
}

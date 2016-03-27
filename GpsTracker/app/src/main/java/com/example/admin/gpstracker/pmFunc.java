package com.example.admin.gpstracker;

import android.app.Activity;

/**
 * Created by Admin on 3/26/2016.
 */
public class pmFunc {

    //Method returns true if search string length is greater than 2 characters
    //Replace with methods that returns true if location is recognized.
    public boolean searchLocExist(String searchedLoc){
        if(searchedLoc.length() >=  2)
            return true;

        return false;
    }

}

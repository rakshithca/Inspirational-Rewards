package com.inspiration.inspirationrewards.utils;

import android.widget.EditText;

/**
 * Created by zolipe on 31-Mar-19.
 */

public class Validation {

    public static boolean validateTextField(EditText editText){
        if (editText.getText().toString().trim() == null){
            return false;
        }else if (editText.getText().toString().trim().length() <= 0 ){
            return false;
        }else {
            return true;
        }
    }
}

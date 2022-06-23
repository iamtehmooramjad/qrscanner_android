package com.example.qrscannerappzl.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

public class CheckSettingPrefernces {

    //method to check whether night mode is on or off
    public static boolean getMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("night_mode", false);
    }

    //method to check whether beep is on or off
    public static boolean getBeepState(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("beep", false);
    }

    //method to check whether vibration mode is on or off
    public static boolean getVibrateState(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("vibrate", false);
    }

    //method to check whether copy to clipboard mode is on or off
    public static boolean getClipboardState(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("clipboard", false);
    }

    //method to check whether auto url opening mode is on or off
    public static boolean getAutoUrlOpenState(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("auro_url", false);
    }

}

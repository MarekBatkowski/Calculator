package com.example.mb.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public class SPSingleton extends AppCompatActivity
{
    static final String PREFERENCES_NAME = "Preferences";
    static final String PREFERENCES_THEME = "theme";
    static final String PREFERENCES_HISTORY = "history";
    static final String PREFERENCES_OPERATION = "operation";
    static final String PREFERENCES_VIBRATION = "vibration";
    static final String PREFERENCES_VIBRATION_LENGTH = "vibration_length";

    private SharedPreferences preferences;

    public SharedPreferences getPreferences()
    {
        return preferences;
    }

    public SharedPreferences.Editor getEditor()
    {
        return preferences.edit();
    }

    private SPSingleton(Context context)
    {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    }

    private static SPSingleton instance;

    public static SPSingleton getInstance(Context context)
    {
        if(instance == null)
            instance = new SPSingleton(context);
        return instance;
    }
}

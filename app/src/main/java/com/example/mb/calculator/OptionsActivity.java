package com.example.mb.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity
{
    private static final String PREFERENCES_NAME = "Preferences";
    private static final String PREFERENCES_THEME = "theme";
    //  private static final String PREFERENCES_LANGUAGE = "language";

    private RadioButton RadioThemeDefault, RadioThemeLight, RadioThemeDark, RadioThemeBlack;
    ListView historyList;

    int Language;

    public void onThemeChange(View view)
    {
        String ThemeName = view.getTag().toString();
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_THEME, ThemeName);
        editor.commit();
        OptionsActivity.this.recreate();
    }

    private void loadTheme(SharedPreferences preferences)
    {
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String ThemeName = preferences.getString(PREFERENCES_THEME, "Default");
        if (ThemeName.equals("Default")) setTheme(R.style.DefaultTheme);
        if (ThemeName.equals("Light")) setTheme(R.style.LightTheme);
        if (ThemeName.equals("Dark")) setTheme(R.style.DarkTheme);
        if (ThemeName.equals("Black")) setTheme(R.style.BlackTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        //  Language = preferences.getInt(PREFERENCES_LANGUAGE, 0);
        loadTheme(preferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        RadioThemeDefault = findViewById(R.id.Radio_ThemeDefault);
        RadioThemeLight = findViewById(R.id.Radio_ThemeLight);
        RadioThemeDark = findViewById(R.id.Radio_ThemeDark);
        RadioThemeBlack = findViewById(R.id.Radio_Theme_Black);

        String ThemeName = preferences.getString(PREFERENCES_THEME, "Default");
        if (ThemeName.equals("Default")) RadioThemeDefault.setChecked(true);
        if (ThemeName.equals("Light")) RadioThemeLight.setChecked(true);
        if (ThemeName.equals("Dark")) RadioThemeDark.setChecked(true);
        if (ThemeName.equals("Black")) RadioThemeBlack.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.Menu_RestoreDefaults:
                SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREFERENCES_THEME, "Default");
                //  editor.putInt(PREFERENCES_LANGUAGE, 0);
                editor.commit();
                Toast.makeText(OptionsActivity.this, R.string.DefaultsRestored, Toast.LENGTH_SHORT).show();
                OptionsActivity.this.recreate();
                RadioThemeDefault.setChecked(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


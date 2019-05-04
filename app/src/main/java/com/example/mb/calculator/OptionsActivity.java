package com.example.mb.calculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity
{
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private RadioButton RadioThemeDefault, RadioThemeLight, RadioThemeDark, RadioThemeBlack;
    private Switch SwitchVibrations;
    private SeekBar SeekbarVibations;
    private TextView TextVibrationLength;
    ListView historyList;

    public void onThemeChange(View view)
    {
        String ThemeName = view.getTag().toString();
        String CurrTheme = preferences.getString(SPSingleton.PREFERENCES_THEME, "Default");
        if(!CurrTheme.equals(ThemeName))
        {
            editor.putString(SPSingleton.PREFERENCES_THEME, ThemeName).apply();
            Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadTheme()
    {
        String ThemeName = preferences.getString(SPSingleton.PREFERENCES_THEME, "Default");
        if (ThemeName.equals("Default"))    setTheme(R.style.DefaultTheme);
        if (ThemeName.equals("Light"))      setTheme(R.style.LightTheme);
        if (ThemeName.equals("Dark"))       setTheme(R.style.DarkTheme);
        if (ThemeName.equals("Black"))      setTheme(R.style.BlackTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        preferences = SPSingleton.getInstance(OptionsActivity.this).getPreferences();
        editor = SPSingleton.getInstance(OptionsActivity.this).getEditor();
        loadTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        RadioThemeDefault = findViewById(R.id.Radio_ThemeDefault);
        RadioThemeLight = findViewById(R.id.Radio_ThemeLight);
        RadioThemeDark = findViewById(R.id.Radio_ThemeDark);
        RadioThemeBlack = findViewById(R.id.Radio_Theme_Black);

        SwitchVibrations = findViewById(R.id.Switch_Vibration);
        SeekbarVibations = findViewById(R.id.Seekbar_Vibrations);
        TextVibrationLength = findViewById(R.id.Text_VibrationLenth);

        final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        String ThemeName = preferences.getString(SPSingleton.PREFERENCES_THEME, "Default");
        if (ThemeName.equals("Default")) RadioThemeDefault.setChecked(true);
        if (ThemeName.equals("Light")) RadioThemeLight.setChecked(true);
        if (ThemeName.equals("Dark")) RadioThemeDark.setChecked(true);
        if (ThemeName.equals("Black")) RadioThemeBlack.setChecked(true);

        Boolean VibState = preferences.getBoolean(SPSingleton.PREFERENCES_VIBRATION, true);
        SwitchVibrations.setChecked(VibState);
        SeekbarVibations.setEnabled(VibState);
        TextVibrationLength.setEnabled(VibState);

        int VibLenght = preferences.getInt(SPSingleton.PREFERENCES_VIBRATION_LENGTH, 10);
        SeekbarVibations.setProgress(VibLenght);
        TextVibrationLength.setText(getString(R.string.Options_VibrationLength)+" "+VibLenght);

        SwitchVibrations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                editor.putBoolean(SPSingleton.PREFERENCES_VIBRATION, isChecked).apply();

                SeekbarVibations.setEnabled(isChecked);
                TextVibrationLength.setEnabled(isChecked);
            }
        });

        SeekbarVibations.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                TextVibrationLength.setText(getString(R.string.Options_VibrationLength)+" "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                int progress = seekBar.getProgress();
                editor.putInt(SPSingleton.PREFERENCES_VIBRATION_LENGTH, progress).apply();
                vib.vibrate(progress);
            }
        });
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

                editor.putString(SPSingleton.PREFERENCES_THEME, "Default")
                      .putBoolean(SPSingleton.PREFERENCES_VIBRATION, true)
                      .putInt(SPSingleton.PREFERENCES_VIBRATION_LENGTH, 10).apply();

                Toast.makeText(OptionsActivity.this, R.string.DefaultsRestored, Toast.LENGTH_SHORT).show();

                RadioThemeDefault.setChecked(true);
                SwitchVibrations.setChecked(true);
                SeekbarVibations.setProgress(10);

                Intent intent = new Intent(getApplication().getApplicationContext(), OptionsActivity.class);
                startActivity(intent);
                finish();

                /*
                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        Intent intent = new Intent(getApplication().getApplicationContext(), OptionsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 100);
                */

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


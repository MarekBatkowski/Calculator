package com.example.mb.calculator;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity
{
    private static final String PREFERENCES_NAME = "Preferences";
    private static final String PREFERENCES_THEME = "theme";
    //  private static final String PREFERENCES_LANGUAGE = "language";
    private static final String HISTORY = "history";

    int Language;
    ListView historyList;

    private ArrayList<String> operationsHistory;

    private void loadTheme(SharedPreferences preferences)
    {
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String ThemeName = preferences.getString(PREFERENCES_THEME, "Default");
        if (ThemeName.equals("Default"))    setTheme(R.style.DefaultTheme);
        if (ThemeName.equals("Light"))      setTheme(R.style.LightTheme);
        if (ThemeName.equals("Dark"))       setTheme(R.style.DarkTheme);
        if (ThemeName.equals("Black"))      setTheme(R.style.BlackTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        //  Language = preferences.getInt(PREFERENCES_LANGUAGE, 0);
        loadTheme(preferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        String json = getIntent().getStringExtra("operationsHistory");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        operationsHistory = gson.fromJson(json, type);

        if(operationsHistory.isEmpty())
            operationsHistory.add(getString(R.string.History_empty));
        else
            Collections.reverse(operationsHistory);

        historyList = findViewById(R.id.historyList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, operationsHistory);
        historyList.setAdapter(arrayAdapter);

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String operation = parent.getAdapter().getItem(position).toString();
                if(!operation.equals(getString(R.string.History_empty)))
                {
                    Intent intent = new Intent();
                    intent.putExtra("operation", operation);
                    setResult(1, intent);                   //  respond with operation from history
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.Menu_ClearHistory:

                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setTitle(R.string.Dialogue_ConfirmOperation);
                builder.setMessage(R.string.Dialogue_ClearHistory);

                builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        Gson gson = new Gson();
                        editor.putString(HISTORY, "[]");
                        editor.apply();

                        ArrayList<String> emptyArray = new ArrayList<String>();
                        emptyArray.add(getString(R.string.History_empty));

                        ArrayAdapter<String> adapterEmpty = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_list_item_1, emptyArray);
                        historyList.setAdapter(adapterEmpty);
                        Toast.makeText(HistoryActivity.this, R.string.History_Cleared ,Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which){}
                });

                builder.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.example.mb.calculator;

import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    ListView historyList;

    private ArrayList<String> operationsHistory;

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
        preferences = SPSingleton.getInstance(HistoryActivity.this).getPreferences();
        editor = SPSingleton.getInstance(HistoryActivity.this).getEditor();
        loadTheme();

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
                finish();
            }
        });

        String json = getIntent().getStringExtra("operationsHistory");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        operationsHistory = gson.fromJson(json, type);

        if(operationsHistory == null)
            operationsHistory = new ArrayList<String>();

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
                        editor.putString(SPSingleton.PREFERENCES_HISTORY, "[]").apply();
                        Toast.makeText(HistoryActivity.this, R.string.History_Cleared ,Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                        startActivity(intent);
                        finish();
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

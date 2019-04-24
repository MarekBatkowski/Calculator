package com.example.mb.calculator;

import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.*;

import org.mariuszgromada.math.mxparser.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private static final String PREFERENCES_NAME = "Preferences";
    private static final String PREFERENCES_THEME = "theme";
    //  private static final String PREFERENCES_LANGUAGE = "language";
    private static final String HISTORY = "history";

    private ArrayList<String> operationsHistory;
    private String currentOperation;
    private String memory;

    private Button ButtonC, Delete, Equals, ButtonMS, ButtonMR, ButtonMC, ButtonChangeSign;
    private TextView OperationField;
    private TextView ResultField;

    private String getContextRegEx(String currChar)
    {
        if      (currChar.equals("."))  return ".*[0-9]$";
        else if (currChar.equals("!"))  return ".*[0-9)]$";
        else if (currChar.equals("%"))  return ".*[0-9)]$";
        else if (currChar.equals("^"))  return ".*[0-9)]$";
        //else if(currChar.matches(".*[0-9]$"))dd       return
        //else if(currChar.matches(".*[\\+\\-x/]$")     return
        else                return ".*";
    }

    public void onClick(View view)
    {

        String currChar = view.getTag().toString();
        String Operation = OperationField.getText().toString();
        /*
        if (Operation.length()==0 || Operation.matches(getContextRegEx(currChar)))
        {
            Toast.makeText(MainActivity.this, "last char matches "+getContextRegEx(currChar) + " (" +currChar+ ")", Toast.LENGTH_SHORT).show();
            OperationField.append(currChar);
            updateResult(OperationField, ResultField);
        }
        else    Toast.makeText(MainActivity.this, "last char doesnt match "+getContextRegEx(currChar), Toast.LENGTH_SHORT).show();
        */
        OperationField.append(currChar);
        updateResult(OperationField, ResultField);
    }

    private String formatString(double d)
    {
        if (d == (long) d) return String.format("%d", (long) d);
        else return String.format("%s", d);
    }

    private String getResult(String expressionText)
    {
        expressionText = expressionText.replace("√", "sqrt");
        expressionText = expressionText.replace("x", "*");
        return formatString(new Expression(expressionText).calculate());
    }

    private void updateResult(TextView OperationField, TextView ResultField)
    {
        String expressionText = OperationField.getText().toString();
        String result = getResult(expressionText);

        if (!result.equals("NaN") && !result.equals(expressionText))
            ResultField.setText(result);
        else
            ResultField.setText("");
    }

    private void loadTheme(SharedPreferences preferences)
    {
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String ThemeName = preferences.getString(PREFERENCES_THEME, "Default");
        if (ThemeName.equals("Default"))    setTheme(R.style.DefaultTheme);
        if (ThemeName.equals("Light"))      setTheme(R.style.LightTheme);
        if (ThemeName.equals("Dark"))       setTheme(R.style.DarkTheme);
        if (ThemeName.equals("Black"))      setTheme(R.style.BlackTheme);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String json = preferences.getString(HISTORY, "[]");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        operationsHistory =  new Gson().fromJson(json, type);
        loadTheme(preferences);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        memory = null;

        OperationField = findViewById(R.id.OperationField);
        ResultField = findViewById(R.id.ResultField);

        OperationField.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                return true;
            }
        });

        ResultField.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                return true;
            }
        });

        ButtonMS = findViewById(R.id.ButtonMemoryStore);
        ButtonMR = findViewById(R.id.ButtonMemoryRead);
        ButtonMC = findViewById(R.id.ButtonMemoryClear);
        ButtonChangeSign = findViewById(R.id.ButtonChangeSign);
        ButtonC = findViewById(R.id.ButtonC);
        Delete = findViewById(R.id.Delete);
        Equals = findViewById(R.id.Equals);

        ButtonMS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String expressionText = OperationField.getText().toString();
                String result = getResult(expressionText);
                if (!result.equals("NaN"))
                {
                    memory = expressionText;
                    Toast.makeText(MainActivity.this, R.string.Memory_Saved, Toast.LENGTH_SHORT).show();
                }
                else    Toast.makeText(MainActivity.this, R.string.Memory_CannotSet, Toast.LENGTH_SHORT).show();
            }
        });

        ButtonMR.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (memory != null && !memory.equals(""))
                {
                    if (OperationField.getText().toString().matches(".*[\\+\\-x/]$"))
                    {
                        updateResult(OperationField, ResultField);
                        OperationField.append(memory);
                    }
                    else    Toast.makeText(MainActivity.this, R.string.Memory_CannotPaste, Toast.LENGTH_SHORT).show(); // operation must end with +,-,*,/ to paste
                }
                else    Toast.makeText(MainActivity.this, R.string.Memory_Empty, Toast.LENGTH_SHORT).show();
            }
        });

        ButtonMC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                memory = null;
                Toast.makeText(MainActivity.this, R.string.Memory_Cleared, Toast.LENGTH_SHORT).show();
            }
        });

        ButtonChangeSign.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String temp = OperationField.getText().toString();
                if (temp.startsWith("-(") && temp.endsWith(")"))
                {   //  it already is "-(<some expression>)"
                    temp = temp.substring(2);                       // remove "-("
                    temp = temp.substring(0, temp.length() - 1);    // remove ")"
                    OperationField.setText(temp);
                    updateResult(OperationField, ResultField);
                }
                else
                {
                    OperationField.setText("-(" + OperationField.getText() + ")");
                    updateResult(OperationField, ResultField);
                }
            }
        });

        ButtonC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                OperationField.setText("");
                ResultField.setText("");
            }
        });

        Delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String text = OperationField.getText().toString();
                if (text.length() > 1)
                {
                    OperationField.setText(text.substring(0, text.length() - 1));
                    updateResult(OperationField, ResultField);
                }
                else
                {
                    OperationField.setText("");
                    ResultField.setText("");
                }
            }
        });

        Equals.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                String expressionText = OperationField.getText().toString();
                String result = getResult(expressionText);
                if (!result.equals("NaN"))
                {
                    operationsHistory.add(OperationField.getText().toString());                         //add to History
                    SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(operationsHistory);
                    editor.putString(HISTORY, json);
                    editor.apply();

                    OperationField.setText(result);
                    ResultField.setText("");
                }
                else    Toast.makeText(MainActivity.this, R.string.Toast_InvalidOperation, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 1)
        {
            OperationField.setText(data.getStringExtra("operation"));
            updateResult(OperationField, ResultField);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.Menu_Exit:

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.Menu_Exit);
                builder.setMessage(R.string.Dialogue_ExitQuestion);

                builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MainActivity.this.finish();
                        System.exit(0);
                    }
                });

                builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which){}
                });

                builder.show();
                return true;

            case R.id.Menu_AboutAuthor:

                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialogue_about, null);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                builder2.setTitle(R.string.Menu_AboutAuthor);
                builder2.setView(view);

                builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which){}
                });

                builder2.show();

                TextView Github = ((TextView) view.findViewById(R.id.GithubText));
                Github.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://github.com/MarekBatkowski"));
                        startActivity(intent);
                    }
                });
                return true;

            case R.id.Menu_History:
                Intent intent2 = new Intent(MainActivity.this, HistoryActivity.class);
                intent2.putExtra("operationsHistory", new Gson().toJson(operationsHistory));
                startActivityForResult(intent2, 1);     //  ask for operation from history
                return true;

            case R.id.Menu_Options:
                startActivity(new Intent(MainActivity.this, OptionsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("Operation", OperationField.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        String Operation = savedInstanceState.getString("Operation");
        OperationField.setText(Operation);
        updateResult(OperationField, ResultField);
    }
}




package com.example.mb.calculator;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.*;

import org.mariuszgromada.math.mxparser.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String ThemeName;
    private ArrayList<String> operationsHistory;
    private String memory;
    private Vibrator vib;

    private Button ButtonC, Delete, Equals, ButtonMS, ButtonMR, ButtonMC, ButtonChangeSign;
    private EditText OperationField;
    private TextView ResultField;
    private Boolean VibState;
    private int VibLenght;

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
        if(VibState)    vib.vibrate(VibLenght);
        String currChar = view.getTag().toString();
        /*
        if (Operation.length()==0 || Operation.matches(getContextRegEx(currChar)))
        {
            Toast.makeText(MainActivity.this, "last char matches "+getContextRegEx(currChar) + " (" +currChar+ ")", Toast.LENGTH_SHORT).show();
            OperationField.append(currChar);
            updateResult(OperationField, ResultField);
        }
        else    Toast.makeText(MainActivity.this, "last char doesnt match "+getContextRegEx(currChar), Toast.LENGTH_SHORT).show();
        */

        OperationField.getText().insert(OperationField.getSelectionStart(), currChar);
        updateResult();
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

    private void updateResult()
    {
        String expressionText = OperationField.getText().toString();
        String result = getResult(expressionText);

        if (!result.equals("NaN") && !result.equals(expressionText))
            ResultField.setText(result);
        else
            ResultField.setText("");
    }

    private void loadTheme()
    {
        String ThemeName = preferences.getString(SPSingleton.PREFERENCES_THEME, "Default");
        if (ThemeName.equals("Default"))    setTheme(R.style.DefaultTheme);
        if (ThemeName.equals("Light"))      setTheme(R.style.LightTheme);
        if (ThemeName.equals("Dark"))       setTheme(R.style.DarkTheme);
        if (ThemeName.equals("Black"))      setTheme(R.style.BlackTheme);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        preferences = SPSingleton.getInstance(MainActivity.this).getPreferences();
        editor = SPSingleton.getInstance(MainActivity.this).getEditor();
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        loadTheme();

        ThemeName = preferences.getString(SPSingleton.PREFERENCES_THEME, "Default");

        String json = preferences.getString(SPSingleton.PREFERENCES_HISTORY, "[]");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        operationsHistory =  new Gson().fromJson(json, type);

        VibState = preferences.getBoolean(SPSingleton.PREFERENCES_VIBRATION, true);
        VibLenght = preferences.getInt(SPSingleton.PREFERENCES_VIBRATION_LENGTH, 10);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        memory = null;

        OperationField = findViewById(R.id.OperationField);
        ResultField = findViewById(R.id.ResultField);
        OperationField.setShowSoftInputOnFocus(false);

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
                    if(VibState)    vib.vibrate(VibLenght);
                }
                else
                {
                    Toast.makeText(MainActivity.this, R.string.Memory_CannotSet, Toast.LENGTH_SHORT).show();
                    if(VibState)    vib.vibrate(VibLenght+100);
                }
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
                        updateResult();
                        OperationField.append(memory);
                        if(VibState)    vib.vibrate(VibLenght);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, R.string.Memory_CannotPaste, Toast.LENGTH_SHORT).show(); // operation must end with +,-,*,/ to paste
                        if(VibState)    vib.vibrate(VibLenght+100);
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, R.string.Memory_Empty, Toast.LENGTH_SHORT).show();
                    if(VibState)    vib.vibrate(VibLenght+100);
                }
            }
        });

        ButtonMC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                memory = null;
                Toast.makeText(MainActivity.this, R.string.Memory_Cleared, Toast.LENGTH_SHORT).show();
                if(VibState)    vib.vibrate(VibLenght);
            }
        });

        ButtonChangeSign.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(VibState)    vib.vibrate(VibLenght);
                String temp = OperationField.getText().toString();
                if (temp.startsWith("-(") && temp.endsWith(")"))
                {   //  it already is "-(<some expression>)"
                    temp = temp.substring(2);                       // remove "-("
                    temp = temp.substring(0, temp.length() - 1);    // remove ")"
                    OperationField.setText(temp);
                    updateResult();
                }
                else
                {
                    OperationField.setText("-(" + OperationField.getText() + ")");
                    updateResult();
                }
            }
        });

        ButtonC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(VibState)    vib.vibrate(VibLenght);
                OperationField.setText("");
                ResultField.setText("");
            }
        });

        Delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(VibState)    vib.vibrate(VibLenght);

                int selectionStart=OperationField.getSelectionStart();
                int selectionEnd=OperationField.getSelectionEnd();
                String inputString=OperationField.getText().toString();

                String selectedText = inputString.substring(selectionStart, selectionEnd);

                if(!selectedText.isEmpty())
                {
                    String selectionDeletedString=inputString.replace(selectedText,"");
                    OperationField.setText(selectionDeletedString);
                    OperationField.setSelection(selectionStart);
                    updateResult();
                }
                else
                {
                    int cursorPos = OperationField.getSelectionStart();
                    if (cursorPos > 0)
                    {
                        OperationField.getText().delete(cursorPos - 1, cursorPos).toString();
                        updateResult();
                    }
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
                    if(VibState)    vib.vibrate(VibLenght);
                    operationsHistory.add(OperationField.getText().toString());                         //add to History

                    Gson gson = new Gson();
                    String json = gson.toJson(operationsHistory);
                    editor.putString(SPSingleton.PREFERENCES_HISTORY, json).apply();

                    OperationField.setText(result);
                    ResultField.setText("");
                }
                else
                {
                    Toast.makeText(MainActivity.this, R.string.Toast_InvalidOperation, Toast.LENGTH_SHORT).show();
                    if(VibState)    vib.vibrate(VibLenght+100);
                }
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
            updateResult();
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
        updateResult();
    }


    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        String CurrThemeName = preferences.getString(SPSingleton.PREFERENCES_THEME, "Default");
        Boolean CurrVibstate = preferences.getBoolean(SPSingleton.PREFERENCES_VIBRATION, true);
        int CurrVibLengt = preferences.getInt(SPSingleton.PREFERENCES_VIBRATION_LENGTH, 10);

        String json = SPSingleton.getInstance(MainActivity.this).getPreferences().getString(SPSingleton.PREFERENCES_HISTORY, "[]");
        Type type = new TypeToken<ArrayList<String>>()
        {
        }.getType();
        ArrayList<String> CurrOperationsHistory = new Gson().fromJson(json, type);

        if (!CurrThemeName.equals(ThemeName) || CurrVibstate != VibState || !(CurrVibLengt == VibLenght) || !CurrOperationsHistory.equals(operationsHistory))
        {
            System.out.print("dupa");
            recreate();
        }
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        editor.putString(SPSingleton.PREFERENCES_OPERATION, OperationField.getText().toString()).apply();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(OperationField.getText().toString()=="")
        {
            String Operation = preferences.getString(SPSingleton.PREFERENCES_OPERATION, "");
            OperationField.setText(Operation);
            updateResult();
        }
    }
}




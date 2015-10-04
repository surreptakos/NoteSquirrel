package com.example.dan.notesquirrel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public static final String DEBUGTAG = "DJG";
    public static final String TEXTFILE = "notesquirrel.txt";
    public static final String FILESAVED = "FileSaved";
    public static final String RESET_PASSPOINTS = "ResetPasspoints";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addSaveButtonListener();
        addLockButtonListener();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (prefs.getBoolean(FILESAVED, false)) {
            loadSavedFile();
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.NoTextSaved), Toast.LENGTH_LONG).show();
        }

    }

    private void loadSavedFile() {
        try {
            FileInputStream fis = openFileInput(TEXTFILE);

            BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(fis)));
            EditText editText = (EditText) findViewById(R.id.text);
            String line;

            while ((line = reader.readLine()) != null) {
                editText.append(line);
                editText.append(getString(R.string.NewLineChar));
            }
            fis.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, getString(R.string.UnableToReadFile), Toast.LENGTH_LONG).show();
        }

    }


    private void addSaveButtonListener() {
        Button saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.text);

                String text = editText.getText().toString();

                try {

                    FileOutputStream fos = openFileOutput(TEXTFILE, Context.MODE_PRIVATE);
                    fos.write(text.getBytes());
                    fos.close();

                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(FILESAVED, true);
                    editor.commit();

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_cant_save), Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private void addLockButtonListener() {
        Button lockBtn = (Button) findViewById(R.id.lock);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, Image.class);
                startActivity(i);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menu_passpoints_reset:
                Intent i = new Intent(this, Image.class);
                i.putExtra(RESET_PASSPOINTS, true);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }
}



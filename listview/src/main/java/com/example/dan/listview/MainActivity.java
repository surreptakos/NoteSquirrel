package com.example.dan.listview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListListener();
    }

    //need to wait until this weekend to do any coding

    private void setListListener() {

        // simple_list_item_1 is part of android.R.layout which is a simple XML layout that makes a simple list item in your list
        String[] values = getResources().getStringArray(R.array.list_options);
        values[0] = "Hamster Steve";

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);

        ListView list = (ListView) findViewById(R.id.List);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // Avoid having your code depend on text that the user sees bc you may want to change
            // the text that the user sees without editing all of your code

            // Adapter is a thing that takes the array of strings and turns them into listitems;
            // it adapts the strings into listitems
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Position: " + position + "; Value: " +
                        parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //dah two


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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

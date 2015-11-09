package com.example.mary.customlists;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupEmailList();
    }

    private void setupEmailList() {
        // Retrieve these messages from somewhere...
        List<Message> messages = new ArrayList<>();

        messages.add(new Message(0, "Bob Smith", "My cat has eaten my wife.", true));
        messages.add(new Message(1, "Bob's Cat", "She was delicious. You're next.", false));
        messages.add(new Message(2, "Bob's Wife", "MmmmmmMFFFFFfrrrff", false));
        messages.add(new Message(3, "Chimps Ahoy!", "Now with 66.6% more chimps! Buy now now NOW!", true));

        ListView listView = (ListView) findViewById(R.id.email_list);
        //Need an adapter to put objects into a ListView. Need a custom adapter to do anything fancy
        // Adapters change the objects into Listable objects
        MessageAdapter adapter = new MessageAdapter(this, messages);

        listView.setAdapter(adapter);

        // Implement handling OnItemClick here

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

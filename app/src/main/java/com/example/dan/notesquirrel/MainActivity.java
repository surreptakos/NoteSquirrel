package com.example.dan.notesquirrel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public static final String DEBUGTAG = "DJG";
    public static final String TEXTFILE = "notesquirrel.txt";
    public static final String FILESAVED = "FileSaved";
    private static final int PHOTO_TAKEN_REQUEST = 0;
    private static final int BROWSE_GALLERY_REQUEST = 1;
    private Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addSaveButtonListener();
        addLockButtonListener();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (prefs.getBoolean(FILESAVED, false)) {
            loadSavedFile();
        }

    }

    /*
     * This method invokes the Image activity, telling it to get the user to
	 * enter new passpoints. If an image uri is passed, the Image activity
	 * should also reset the image to the given image.
	 */
    private void resetPasspoints(Uri image) {
        Intent i = new Intent(this, Image.class);
        i.putExtra(Image.RESET_PASSPOINTS, true);

        if (image != null) {
            i.putExtra(Image.RESET_IMAGE, image.getPath());
        }

        startActivity(i);
    }

    private void loadSavedFile() {
        try {
            FileInputStream fis = openFileInput(TEXTFILE);

            BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(fis)));
            EditText editText = (EditText) findViewById(R.id.text);
            String line;

            while ((line = reader.readLine()) != null) {
                editText.append(line);
                editText.append("\n");
            }
            fis.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, getString(R.string.toast_cant_load), Toast.LENGTH_LONG).show();
        }

    }


    private void addSaveButtonListener() {
        Button saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveText();


            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        saveText();
    }

    private void saveText() {
        EditText editText = (EditText) findViewById(R.id.text);

        String text = editText.getText().toString();

        try {
            FileOutputStream fos = openFileOutput(TEXTFILE,
                    Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();

            // Save a "file saved" preference so that next time
            // the application runs, we'll know that this
            // file has been saved and we have to load it.
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FILESAVED, true);
            editor.commit();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,
                    getString(R.string.toast_cant_save), Toast.LENGTH_LONG)
                    .show();
        }
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

    private void replaceImage() {

        // Offer a choice of methods to replace the image in a dialog;
        // the user can either take a photo or browse the gallery.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.replace_image, null);
        builder.setTitle(R.string.replace_lock_image);
        builder.setView(v);

        final AlertDialog dlg = builder.create();
        dlg.show();

        Button takePhoto = (Button) dlg.findViewById(R.id.take_photo);
        Button browseGallery = (Button) dlg.findViewById(R.id.browse_gallery);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Invoke the camera activity.
                takePhoto();
            }
        });

        browseGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Browse the gallery.
                browseGallery();
            }
        });
    }

    private void browseGallery() {
        //here we go

    }

    private void takePhoto() {
        // Figure out where to put the photo when it's taken.
        File picturesDirectory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(picturesDirectory, "passpoints_image");

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Tell the activity to save the photo to the given file.
        // (it will also be added to the gallery)
        image = Uri.fromFile(imageFile);
        i.putExtra(MediaStore.EXTRA_OUTPUT, image);
        startActivityForResult(i, PHOTO_TAKEN_REQUEST);
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
                resetPasspoints(null);
                return true;
            case R.id.menu_replace_image:
                replaceImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }
}



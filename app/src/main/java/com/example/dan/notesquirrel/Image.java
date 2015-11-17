package com.example.dan.notesquirrel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class Image extends AppCompatActivity implements PointCollectorListener {

    public static final String RESET_PASSPOINTS = "ResetPasspoints";
    public static final String RESET_IMAGE = "ResetImage";
    private final static String PASSWORD_SET = "PasswordSet";
    private final static String CURRENT_IMAGE = "CurrentImage";
    private static final int MAX_DIST = 40;
    private boolean doPasspointReset = false;
    private PointCollector pointCollector = new PointCollector();
    private Database db = new Database(this);

    // Now I understand where DDMS and File Explorer are. SharedPreferences are stored on the
    // device, which is emulated in our case. So I need to run Android Device Monitor to access the
    // files.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        addTouchListener();

        pointCollector.setListener(this);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);

        String newImage = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // See if a flag was passed to this intent to indicate
            // that the user wants to change the passpoint sequence.
            doPasspointReset = extras.getBoolean(RESET_PASSPOINTS);

            // See if an image path was passed to this intent,
            // indicating that the user wants to change the image.
            newImage = extras.getString(RESET_IMAGE);
        }


        if (newImage == null) {
            // If the user didn't specify a new image via the reset image
            // option, check to see if we've got an image stored which
            // we should use.
            newImage = prefs.getString(CURRENT_IMAGE, null);
        } else {
            // If the user has specified a new image to be used,
            // save it in the preferences.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CURRENT_IMAGE, newImage);
            editor.commit();
        }

        // Set the current image. If newImage is null, this will
        // display the default.
        setImage(newImage);

        if (!passpointsSet || doPasspointReset) {
            showSetPasspointsPrompt();
        } else {
            showLoginPrompt();
        }


    }

    private void setImage(String path) {

        ImageView imageView = (ImageView) findViewById(R.id.touch_image);

        if (path == null) {
            Drawable image = ContextCompat.getDrawable(this, R.drawable.image_default);
            imageView.setImageDrawable(image);
        } else {
            imageView.setImageURI(Uri.parse(path));
        }

    }

    private void showSetPasspointsPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setTitle(R.string.create_passpoints);
        builder.setMessage(R.string.create_passpoints_text);

        AlertDialog dlg = builder.create();

        dlg.show();


    }

    private void showLoginPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setTitle(R.string.enter_passpoints_title);
        builder.setMessage(R.string.enter_passpoints_text);

        AlertDialog dlg = builder.create();

        dlg.show();
    }

    private void addTouchListener() {
        ImageView image = (ImageView) findViewById(R.id.touch_image);

        image.setOnTouchListener(pointCollector);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
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

    private void savePasspoints(final List<Point> points) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.saving_passpoints));

        final AlertDialog dlg = builder.create();
        dlg.show();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                db.storePoints(points);
                Log.d(MainActivity.DEBUGTAG, "Points saved " + db.getPoints().size());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PASSWORD_SET, true);
                editor.commit();

                pointCollector.clear();
                dlg.dismiss();
            }
        };

        task.execute();


    }

    private void verifyPasspoints(final List<Point> touchedPoints) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.checking_passpoints);

        final AlertDialog dlg = builder.create();
        dlg.show();

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                List<Point> savedPoints = db.getPoints();

                Log.d(MainActivity.DEBUGTAG, "Loaded points: " + savedPoints.size());

                if (savedPoints.size() != PointCollector.NUM_POINTS
                        || touchedPoints.size() != PointCollector.NUM_POINTS) {
                    return false;
                }

                for (int i = 0; i < PointCollector.NUM_POINTS; i++) {
                    Point savedPoint = savedPoints.get(i);
                    Point touchedPoint = touchedPoints.get(i);

                    int xDiff = savedPoint.x - touchedPoint.x;
                    int yDiff = savedPoint.y - touchedPoint.y;

                    double distanceSquared = xDiff * xDiff + yDiff * yDiff;

                    if (distanceSquared > MAX_DIST * MAX_DIST) {
                        return false;
                    }
                }

                return true;
            }

            //Whatever we return in doInBackground is sent to this method
            @Override
            protected void onPostExecute(Boolean pass) {
                dlg.dismiss();
                pointCollector.clear();

                if (pass) {
                    // Intents are used to start new activities
                    Intent i = new Intent(Image.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(Image.this, R.string.access_denied, Toast.LENGTH_LONG).show();
                }

            }
        };

        task.execute();


    }

//derp

    @Override
    public void pointsCollected(final List<Point> points) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);

        if (!passpointsSet) {
            Log.d(MainActivity.DEBUGTAG, "Saving passpoints...");
            savePasspoints(points);
        } else {
            Log.d(MainActivity.DEBUGTAG, "Verifying passpoints...");
            verifyPasspoints(points);
        }
    }
}

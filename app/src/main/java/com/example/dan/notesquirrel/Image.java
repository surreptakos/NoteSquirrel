package com.example.dan.notesquirrel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class Image extends AppCompatActivity implements PointCollectorListener {

    private static final String PASSWORD_SET = "PASSWORD_SET";
    private static final int MAX_DIST = 40;
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

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Boolean resetPasspoints = extras.getBoolean(MainActivity.RESET_PASSPOINTS);

            if (resetPasspoints) {
                resetPasspoints();
            }
        }
        //party tiem

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);

        Log.d(MainActivity.DEBUGTAG, "Value of passpointsSet: " + passpointsSet);

        if (!passpointsSet) {
            showSetPasspointsPrompt();
        }

        pointCollector.setListener(this);


    }

    private void showSetPasspointsPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setTitle("Create your PassPoint Sequence");
        builder.setMessage("Touch four points on the image to set your passpoint sequence. You will need to click these same points in the future to access NoteSquirrel.");

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

    private void resetPasspoints() {

        Toast.makeText(Image.this, R.string.passpointsResetSuccessfully, Toast.LENGTH_LONG).show();

        db.resetPoints();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PASSWORD_SET, false);
        editor.commit();


    }

    private void savePasspoints(final List<Point> points) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.storing_data));

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
                Log.d(MainActivity.DEBUGTAG, "Points saved " + points.size());

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
        builder.setMessage(R.string.verifying_passpoints);

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
                    Toast.makeText(Image.this, R.string.Access_Denied, Toast.LENGTH_LONG).show();
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

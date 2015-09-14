package com.example.dan.notesquirrel;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**

 * Created by Dan on 8/30/2015.
 */
public class PointCollector implements View.OnTouchListener {
    private List<Point> points = new ArrayList<Point>();
    private PointCollectorListener listener;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        String message = String.format("Coordinates: (%d, %d)", x, y);

        Log.d(MainActivity.DEBUGTAG, message);

        points.add(new Point(x, y));

        if (points.size() == 4) {
            if(listener != null) {
                listener.pointsCollected(points);

            }

        }


        return false;
    }

    public void setListener(PointCollectorListener listener) {
        this.listener = listener;
    }

    //Call this after saving your points
    public void clear() {
        points.clear();
    }
}

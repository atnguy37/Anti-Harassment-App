package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.Viewport;

public class MainActivity extends AppCompatActivity {
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
//    private LineGraphSeries<DataPoint> seriesStop;
    private int lastX = 0;
    private boolean buttonStartStop = false;
    private Button mButtonStartStop;
//    private Button mButtonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // we get graph view instance
        // get GraphView from activity_main.xml with ID
        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        // Use LineGraphSeries from GraphView Library to add Data Point
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(2000);
//        viewport.setScrollable(true);
//        viewport.setScalable(true);

        //get Button Start Stop from ID getting from activity_main.xml
        mButtonStartStop = findViewById(R.id.button_start_stop);
//        mButtonReset = findViewById(R.id.button_reset);
        // set click function for button
        mButtonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonStartStop)
                    buttonStartStop = false;
                else
                    buttonStartStop = true;

                if(buttonStartStop) {
                    startGraph();
                }
                else {
                    stopGraph();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 1000d), false, 10);
        /* @param dataPoint values the values must be in the correct order!
                *                  x-value has to be ASC. First the lowest x value and at least the highest x value.
     * @param scrollToEnd true => graphview will scroll to the end (maxX) - I do not know why It only work when setting scrollToEnd: False
                * @param maxDataPoints if max data count is reached, the oldest data
     *                      value will be lost to avoid memory leaks
     * @param silent    set true to avoid rerender the graph */
    }

    private void startGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(series);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                while (buttonStartStop) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();

//        buttonStartStop = false;
        mButtonStartStop.setText("Stop");
//        mButtonReset.setVisibility(View.VISIBLE);
    }


    private void stopGraph() {
//        graph.removeSeries(series);
        // get GraphView from activity_main.xml with ID
        GraphView graph = (GraphView) findViewById(R.id.graph);
//        System.out.println(series.getDataPointsRadius());
//        graph.removeSeries(series);
        graph.removeAllSeries();
//        // data
//        seriesStop = new LineGraphSeries<DataPoint>();
//        graph.addSeries(seriesStop);

        mButtonStartStop.setText("Start");

//        mButtonReset.setVisibility(View.INVISIBLE);

    }
}

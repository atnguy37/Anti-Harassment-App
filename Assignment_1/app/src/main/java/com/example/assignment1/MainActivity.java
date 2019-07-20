package com.example.assignment1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
//    private LineGraphSeries<DataPoint> seriesStop;
    private int lastX = 0;
    private boolean buttonStartStop = false;
    private boolean buttonStart = true;

//    private Button mButtonReset;

    private Button stopButton;
    private Button startButton;
    private TextView patientID;
    private TextView patientName;
    private TextView age;
    private RadioButton female;
    private RadioButton male;
    private static final String STATE_LASTX = "LastX";
    private static final String STATE_START = "ButtonStartStop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // we get graph view instance
        // get GraphView from activity_main.xml with ID
        GraphView graph = (GraphView) findViewById(R.id.graph);
        stopButton = (Button) findViewById(R.id.button_stop);
        patientID = (TextView) findViewById(R.id.patient_id);
        patientName = (TextView) findViewById(R.id.patient_name);
        age = (TextView) findViewById(R.id.patient_age);
        female = (RadioButton) findViewById(R.id.radioButton_female);
        male = (RadioButton) findViewById(R.id.radioButton_male);

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
        if (savedInstanceState != null) {
//            System.out.println("Rotate and Restore");
            lastX = savedInstanceState.getInt(STATE_LASTX, 0);
            buttonStartStop = savedInstanceState.getBoolean(STATE_START, false);
        }

        startButton = findViewById(R.id.button_start);
//        mButtonReset = findViewById(R.id.button_reset);
        // set click function for button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startGraph();
                buttonStartStop = true;
                //If not included it will create mutliple startGraph (speed it up)
                if(buttonStart) {
                    buttonStart = false;
                    startGraph();
                }

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStartStop = false;
                buttonStart = true;
                stopGraph();

               /* if(buttonStartStop)
                    buttonStartStop = false;
                else
                    buttonStartStop = true;

                if(buttonStartStop) {
                    startGraph();
                }
                else {
                    stopGraph();
                }*/
            }
        });

        if(buttonStartStop) {
            startGraph();
        }
        else {
            stopGraph();
        }

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
        //mButtonStartStop.setText("Stop");
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

        //mButtonStartStop.setText("Start");

//        mButtonReset.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        System.out.println("Before Rotate: " + lastX);
        outState.putInt(STATE_LASTX, lastX);
        outState.putBoolean(STATE_START, buttonStartStop);
    }
}

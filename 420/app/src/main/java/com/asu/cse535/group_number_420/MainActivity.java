package com.asu.cse535.group_number_420;

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

    private int lastX = 0;
    private boolean buttonStartStop = false;
    private boolean buttonStart = true;


    private Button stopButton;
    private Button startButton;
    private TextView patientID;
    private TextView patientName;
    private TextView age;
    private RadioButton female;
    private RadioButton male;
    private static final String STATE_LASTX = "LastX";
    private static final String STATE_START = "ButtonStartStop";

    /**
     * Starts the application, the graph view and the rest of the UI components
     * and lastly sets an on click listener for the stop and start buttons
     * @param savedInstanceState
     */

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

        //get Button Start Stop from ID getting from activity_main.xml
        if (savedInstanceState != null) {
            lastX = savedInstanceState.getInt(STATE_LASTX, 0);
            buttonStartStop = savedInstanceState.getBoolean(STATE_START, false);
        }

        startButton = findViewById(R.id.button_start);

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

    // Randomly adds a max of 10 points of the viewpoint. The graph is scrolled to the end.
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 1000d), false, 10);

    }

    /**
     * It adds the series of points to the graph and created a new threat
     * We wrote one threat that adds entries to the graph and another threat that changes the UI
     * elements, it keeps running until the stop button is clicked.
     */

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

    }

    /**
     * Graphs the graph view component and then clears the graph.
     */

    private void stopGraph() {

        GraphView graph = (GraphView) findViewById(R.id.graph);

        graph.removeAllSeries();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        System.out.println("Before Rotate: " + lastX);
        outState.putInt(STATE_LASTX, lastX);
        outState.putBoolean(STATE_START, buttonStartStop);
    }
}
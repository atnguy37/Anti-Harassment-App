package com.asu.cse535.group_number_420;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
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
    private TextView patientAge;
    private RadioButton femaleButton;
    private RadioButton maleButton;
    private static final String STATE_LASTX = "LastX";
    private static final String STATE_START = "ButtonStartStop";
    RadioGroup radioGroup;
    DBHelper dbHandler;
    PersonInfo person;

    public  static final int RequestPermissionCode_WRITE_EXTERNAL_STORAGE  = 1 ;

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
        patientAge = (TextView) findViewById(R.id.patient_age);
        femaleButton = (RadioButton) findViewById(R.id.radioButton_female);
        maleButton = (RadioButton) findViewById(R.id.radioButton_male);
        radioGroup = (RadioGroup) findViewById(R.id.radioButtonGroup);

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

        EnableRuntimePermission();


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startGraph();
                buttonStartStop = true;
                //If not included it will create mutliple startGraph (speed it up)
                if(buttonStart) {
                    buttonStart = false;
                    CreateTable();
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
                AddNewDBEntry();


            }
        });

        if(buttonStartStop) {
            startGraph();
        }
        else {
            stopGraph();
        }

    }

    private void CreateTable(){

        String name = patientName.getText().toString();
        String id = patientID.getText().toString();
        String age = patientAge.getText().toString();
        Button sexTypeButton;

        int selectedId = radioGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        sexTypeButton = (RadioButton) findViewById(selectedId);
        String sex = sexTypeButton.getText().toString();

        person = new PersonInfo(name, age, sex, id);

        dbHandler = new DBHelper(MainActivity.this, person);
    }
    //private static final String TAG = "MyActivity";

    private void AddNewDBEntry(){
        dbHandler.insertNewData("1","2","3");
        ArrayList<HashMap<String, String>> results = dbHandler.GetDataFromCurrentPatient();


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


    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this,"WRITE_EXTERNAL_STORAGE permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestPermissionCode_WRITE_EXTERNAL_STORAGE);
        }

    }



    @Override
    public void onRequestPermissionsResult(int RequestCode, String per[], int[] PResult) {
        switch (RequestCode) {
            case RequestPermissionCode_WRITE_EXTERNAL_STORAGE:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted _ Write_External.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "Permission Canceled.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}

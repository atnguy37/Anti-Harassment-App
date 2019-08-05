package com.asu.cse535.group_number_420;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final Random RANDOM = new Random();

    String dateFormat;
    private int lastX = 0;
    private int lastY = 0;
    private int lastZ = 0;

    private boolean buttonStartStop = false;
    private boolean buttonStart = true;
    private boolean checkSensor = false;
    private boolean datapointExist = false;

    private Button stopButton;
    private Button startButton;
    private TextView patientID;
    private TextView patientName;
    private TextView patientAge;
    private RadioButton femaleButton;
    private RadioButton maleButton;
    private static final String STATE_LASTX = "LastX";
    private static final String STATE_LASTY = "LastY";
    private static final String STATE_LASTZ = "LastZ";

    private static final String STATE_START = "ButtonStartStop";

    RadioGroup radioGroup;
    DBHelper dbHandler;
    PersonInfo person;
    private SensorManager sensorManager;
    Sensor accelerometer;
    long tstamp;


    private final static long ACC_CHECK_INTERVAL = 1000;
    private long lastAccCheck;
    float x;
    float y;
    float z;

    String TAG;

    public  static final int RequestPermissionCode_WRITE_EXTERNAL_STORAGE  = 1 ;

    GraphView graph;

    private LineGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> series_y;
    private LineGraphSeries<DataPoint> series_z;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // we get graph view instance
        // get GraphView from activity_main.xml with ID
        graph = (GraphView) findViewById(R.id.graph);



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
        series_y = new LineGraphSeries<DataPoint>();
        series_z = new LineGraphSeries<DataPoint>();


        graph.addSeries(series);
        series.setColor(Color.MAGENTA);
        graph.addSeries(series_y);
        series_y.setColor(Color.BLACK);
        graph.addSeries(series_z);
        series_z.setColor(Color.RED);

        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-40);
        viewport.setMaxY(40);

//

        //get Button Start Stop from ID getting from activity_main.xml
        if (savedInstanceState != null) {
            lastX = savedInstanceState.getInt(STATE_LASTX, 0);
            lastY = savedInstanceState.getInt(STATE_LASTY, 0);
            lastZ = savedInstanceState.getInt(STATE_LASTZ, 0);

            buttonStartStop = savedInstanceState.getBoolean(STATE_START, false);
            //buttonStart = savedInstanceState.getBoolean(BUTTON_START, false);
            //datapointExist = savedInstanceState.getBoolean(DATAPOINT_EXIST, false);
            //checkSensor = savedInstanceState.getBoolean(CHECK_SENSOR, false);


        }

        startButton = findViewById(R.id.button_start);

        EnableRuntimePermission();



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer , sensorManager.SENSOR_DELAY_NORMAL);





        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startGraph();
                buttonStartStop = true;
                //Start adding points to table
                //If not included it will create mutliple startGraph (speed it up)
                if(buttonStart) {
                    buttonStart = false;
                    CreateTable();
                    checkSensor = true;
                    startGraph();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStartStop = false;
                buttonStart = true;
                checkSensor = false;
                stopGraph();

            }
        });

       /* if(buttonStartStop) {
            startGraph();
        }
        else {
            stopGraph();
        }*/

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

    private void AddNewDBEntry(String x_value, String y_value, String z_value, String timestamp){
        dbHandler.insertNewData(x_value, y_value,z_value, timestamp);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // Randomly adds a max of 10 points of the viewpoint. The graph is scrolled to the end.
    private void addEntry(float x, float y, float z, float y_value) {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, x), false, 10);
        series_y.appendData(new DataPoint(lastY++, y), false, 10);
        series_z.appendData(new DataPoint(lastZ++, z), false, 10);

    }

    /**
     * It adds the series of points to the graph and created a new threat
     * We wrote one threat that adds entries to the graph and another threat that changes the UI
     * elements, it keeps running until the stop button is clicked.
     */

    private void startGraph() {
        graph.addSeries(series);
        series.setColor(Color.BLUE);
        graph.addSeries(series_y);
        series_y.setColor(Color.GREEN);
        graph.addSeries(series_z);
        series_z.setColor(Color.RED);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                while (buttonStartStop) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(datapointExist == true){
                                datapointExist = false;
                                addEntry(x, y, z, tstamp);
                            }
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

        graph.removeAllSeries();

    }



    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;


        if (checkSensor == true ){
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float temp_x = sensorEvent.values[0];
                float temp_y = sensorEvent.values[1];
                float temp_z = sensorEvent.values[2];

                long currTime = System.currentTimeMillis();

                if(currTime - lastAccCheck > ACC_CHECK_INTERVAL) {
                    Log.i(TAG, x + " " + y + " " + z);
                    lastAccCheck = currTime;
                    x = temp_x;
                    y = temp_y;
                    z = temp_z;

                    Date now2 = new Date();

                    tstamp = now2.getTime();

                    //Log.d("MainActivity", "Current Timestamp: " + tstamp);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

                    dateFormat = simpleDateFormat.format(tstamp);

                    //Log.d("MainActivity", "Current Timestamp: " + dateFormat);

                    AddNewDBEntry(Float.toString(x), Float.toString(y), Float.toString(z), dateFormat);
                    //Toast.makeText(MainActivity.this,"Add point to graph", Toast.LENGTH_LONG).show();

                    datapointExist = true;
                }

            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    //Creates the optional menu (three dots that contains settings)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // This is for the optional menu and handles what happens when you click on the
    // options like settings.
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        long id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload_db) {

            DBHelper temp_dbHandler = new DBHelper();

            SQLiteDatabase sqlDB = temp_dbHandler.getDBInfo("/Android/Data/CSE535_ASSIGNMENT2");


            Toast.makeText(MainActivity.this,"upload db", Toast.LENGTH_LONG).show();

            return true;
        }


        if (id == R.id.download_db) {
            Toast.makeText(MainActivity.this,"download db", Toast.LENGTH_LONG).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
         outState.putInt(STATE_LASTX, lastX);
        outState.putInt(STATE_LASTY, lastY);
        outState.putInt(STATE_LASTZ, lastZ);
        outState.putBoolean(STATE_START, buttonStartStop);
        //outState.putBoolean(BUTTON_START, buttonStart);
        //outState.putBoolean(CHECK_SENSOR, checkSensor);
        //outState.putBoolean(DATAPOINT_EXIST, datapointExist);

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

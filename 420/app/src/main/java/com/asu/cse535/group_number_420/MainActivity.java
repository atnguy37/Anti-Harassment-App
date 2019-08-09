package com.asu.cse535.group_number_420;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    private static final int NUMBER_OF_DATA = 10;
    private float x,y,z;
    private LineGraphSeries<DataPoint> seriesX,seriesY,seriesZ;

    public  static final int RequestPermissionCode_WRITE_EXTERNAL_STORAGE  = 1 ;

    private int lastX = 0;
    private boolean buttonStartStop = false;
    private boolean buttonStart = true;
    private boolean checkSensor = false;
    private boolean datapointExist = false;

    private Button stopButton;
    private Button startButton;
    private TextView patient_id;
    private TextView patient_name;
    private TextView patient_age;
    private RadioButton female;
    private RadioButton male;
    private SensorManager sensorManager;
    Sensor accelerometer;
    private String patientID;
    private String patientName;
    private String patientAge;
    private String patientSex;

    //    GraphView graph;
    private static final String STATE_LASTX = "LastX";
    private static final String STATE_START = "ButtonStartStop";


    private final static long ACC_CHECK_INTERVAL = 1000; // 1000ms
    private long lastSaved = System.currentTimeMillis();

    private FileUpload fileupload;

    RadioGroup radioGroup;
    DBHelper dbHandler;
    PersonInfo person;

    /**
     * Starts the application, the graph view and the rest of the UI components
     * and lastly sets an on click listener for the stop and start buttons
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorListener, accelerometer , sensorManager.SENSOR_DELAY_NORMAL);
        // we get graph view instance
        // get GraphView from activity_main.xml with ID
        GraphView graph = (GraphView) findViewById(R.id.graph);
        stopButton = (Button) findViewById(R.id.button_stop);
        patient_id = (TextView) findViewById(R.id.patient_id);
        patient_name = (TextView) findViewById(R.id.patient_name);
        patient_age = (TextView) findViewById(R.id.patient_age);
        female = (RadioButton) findViewById(R.id.radioButton_female);
        male = (RadioButton) findViewById(R.id.radioButton_male);
        radioGroup = (RadioGroup) findViewById(R.id.radioButtonGroup);

        EnableRuntimePermission();

        // Use LineGraphSeries from GraphView Library to add Data Point
        seriesX = new LineGraphSeries<DataPoint>();
        seriesY = new LineGraphSeries<DataPoint>();
        seriesZ = new LineGraphSeries<DataPoint>();

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-40);
        viewport.setMaxY(40);

        //get Button Start Stop from ID getting from activity_main.xml
        if (savedInstanceState != null) {
            lastX = savedInstanceState.getInt(STATE_LASTX, 0);
            buttonStartStop = savedInstanceState.getBoolean(STATE_START, false);
        }

        startButton = findViewById(R.id.button_start);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startGraph();
                buttonStartStop = true;
                //If not included it will create mutliple startGraph (speed it up)
                if(buttonStart) {
                        if(CreateTable()) {
                            buttonStart = false;
                            checkSensor = true;
                            startGraph();
                        }
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


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Randomly adds a max of 10 points of the viewpoint. The graph is scrolled to the end.
    private void addEntry(float x, float y, float z) {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        seriesX.appendData(new DataPoint(lastX, x), false, 10);
        seriesY.appendData(new DataPoint(lastX, y), false, 10);
        seriesZ.appendData(new DataPoint(lastX, z), false, 10);

    }

    private final SensorEventListener sensorListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;
            if (checkSensor == true ){
                if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                index++;
                    if ((System.currentTimeMillis() - lastSaved) > ACC_CHECK_INTERVAL) {
//                    Log.d("Test","ResultX: "+x);
//                    Log.d("Test","ResultY: "+y);
//                    Log.d("Test","ResultZ: "+z);
                        lastSaved = System.currentTimeMillis();
                        x = sensorEvent.values[0];
                        y = sensorEvent.values[1];
                        z = sensorEvent.values[2];

                        if(buttonStartStop) {
                            //Log.d("Test","lastX: "+lastX);
                            addEntry(x, y, z);
                            AddNewDBEntry();
                            lastX++;
                        }

                    }
                }

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor,int accuracy) {
        }
    };

    /*
     * It adds the series of points to the graph and created a new threat
     * We wrote one threat that adds entries to the graph and another threat that changes the UI
     * elements, it keeps running until the stop button is clicked.
     */
    private void startGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(seriesX);
        seriesX.setColor(Color.BLUE);
        graph.addSeries(seriesY);
        seriesY.setColor(Color.GREEN);
        graph.addSeries(seriesZ);
        seriesZ.setColor(Color.RED);
    }

    /**
     * Graphs the graph view component and then clears the graph.
     */

    private void stopGraph() {

        GraphView graph = (GraphView) findViewById(R.id.graph);

        graph.removeAllSeries();


    }

    private boolean checkUserInfo(){

        String name = patient_name.getText().toString();
        String id = patient_id.getText().toString();
        String age = patient_age.getText().toString();
        Button sexTypeButton;
        if(radioGroup.getCheckedRadioButtonId()!=-1 && !name.isEmpty() && !id.isEmpty() && !age.isEmpty()) {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            sexTypeButton = (RadioButton) findViewById(selectedId);
            String sex = sexTypeButton.getText().toString();

            if (!name.equals(patientName) || !id.equals(patientID) || !age.equals(patientAge) || !sex.equals(patientSex)) {
//                Log.d(TAG, "Name: " + name + " Age: " + age + " Sex: " + sex + " ID: " + id );
//                Log.d(TAG, "Name2: " + patientName + " Age2: " + patientAge + " Sex2: " + patientSex + " ID2: " + patientID );
                person = new PersonInfo(name, age, sex, id);

                patientName = name;
                patientAge = age;
                patientSex = sex;
                patientID = id;

                lastX = 0;
                stopGraph();

                dbHandler = new DBHelper(MainActivity.this, person);

                seriesX = new LineGraphSeries<DataPoint>();
                seriesY = new LineGraphSeries<DataPoint>();
                seriesZ = new LineGraphSeries<DataPoint>();
                //Log.d(TAG,"Checking CheckInfo ");
            }
            //Log.d(TAG,"Duplicate ");
            return true;
        }
        Toast.makeText(MainActivity.this,"Your information is not enough", Toast.LENGTH_LONG).show();
        return false;
    }

    private boolean CreateTable(){
        //Log.d(TAG,"Check Info create");
        if(checkUserInfo()) {
            //Log.d(TAG, "Created DB");
            dbHandler.CreateTable();
            return true;
        }
        return false;
    }

    private void AddNewDBEntry(){
        dbHandler.insertNewData(Float.toString(x),Float.toString(y),Float.toString(z));
    }

    //Creates the optional menu (three dots that contains settings)
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
            fileupload = new FileUpload();

            final String path = "/Android/Data/CSE535_ASSIGNMENT2";
            final String file_name = "CSE535_ASSIGNMENT2";
            final String server_address = "http://10.0.2.2:8888/upload/upload.php";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fileupload.uploadFile(path, file_name, server_address);
                }
            }).start();

            Toast.makeText(MainActivity.this,"upload db", Toast.LENGTH_LONG).show();

            return true;
        }


        if (id == R.id.download_db) {
            //Log.d(TAG,"Before CheckInfo: ");
            if(buttonStartStop) {
                Toast.makeText(MainActivity.this,"You should press Stop before download database", Toast.LENGTH_LONG).show();
                return true;
            }

            checkUserInfo();
            Toast.makeText(MainActivity.this,"download db", Toast.LENGTH_LONG).show();

                //Log.d(TAG,"CheckInfo: ");
                //String tableName = person.getName() + "_" + person.getId() + "_" + person.getAge() + "_" + person.getSex();
                //new FileDownload(getApplicationContext()).execute("http://192.168.0.23:8080/CSE535_ASSIGNMENT2","/Android/Data/CSE535_ASSIGNMENT2");
            new FileDownload(getApplicationContext()).execute("http://10.0.2.2:8888/upload/CSE535_ASSIGNMENT2","/Android/Data/CSE535_ASSIGNMENT2");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(TAG,"Load Data: ");
            ArrayList<HashMap<String, String>> results = dbHandler.GetDataFromCurrentPatient();
            //Log.d(TAG,"Stored Data: " + results.toString());
            if(results != null ) {
                seriesX = new LineGraphSeries<DataPoint>(data(results,"xvalues"));
                seriesY = new LineGraphSeries<DataPoint>(data(results,"yvalues"));
                seriesZ = new LineGraphSeries<DataPoint>(data(results,"zvalues"));
                    //Log.d(TAG,"Load Data: " + results.toString());
                startGraph();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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


    private DataPoint[] data(ArrayList<HashMap<String, String>> data, String axis){
        int n=data.size(); //to find out the no. of data-points
        DataPoint[] values = new DataPoint[NUMBER_OF_DATA]; //creating an object of type
        //Log.d(TAG,"Data Size: " + n);
        for(int i=0;i<NUMBER_OF_DATA;i++){
            DataPoint v = new
                    DataPoint(n - NUMBER_OF_DATA + i + 1,Float.parseFloat(data.get(i).get(axis)));
            values[i] = v;
            //Log.d(TAG,"Data New: " + (n - NUMBER_OF_DATA + i + 1));
        }
        lastX = n + 1;
        return values;
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
package com.asu.cse535.group_number_420.Final;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import com.asu.cse535.group_number_420.Assignment2.DBHelper;
import com.asu.cse535.group_number_420.Assignment2.PersonInfo;
import com.asu.cse535.group_number_420.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class GraphFunctions {

    private LineGraphSeries<DataPoint> seriesX,seriesY,seriesZ;
    private int lastX = 0;
    private Sensor accelerometer;
    private SensorManager sensorManager;
    private float x, y, z;
    private static final String TAG = "MyActivity";


    private boolean buttonStartStop = false;
    private boolean checkSensor = false;
    private final static long ACC_CHECK_INTERVAL = 1000; // 1000ms
    private long lastSaved = System.currentTimeMillis();
    private GraphView graph;
    ArrayList<ArrayList<Float>> series;
    ArrayList<Float> series_x;
    ArrayList<Float> series_y;
    ArrayList<Float> series_z;
    private Context context;

    //might delete later
    DBHelper dbHandler;
    PersonInfo person;


    public GraphFunctions(Context context, View view){

        this.context = context;
        sensorManager = (SensorManager) context.getApplicationContext().getSystemService(this.context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorListener, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        seriesX = new LineGraphSeries<DataPoint>();
        seriesY = new LineGraphSeries<DataPoint>();
        seriesZ = new LineGraphSeries<DataPoint>();
        graph = (GraphView) view.findViewById(R.id.graph);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-40);
        viewport.setMaxY(40);

        series_x = new ArrayList<>();
        series_y = new ArrayList<>();
        series_z = new ArrayList<>();

        series = new ArrayList<>();

    }

    public void startGraph(View view) {
        graph.addSeries(seriesX);
        seriesX.setColor(Color.BLUE);
        graph.addSeries(seriesY);
        seriesY.setColor(Color.GREEN);
        graph.addSeries(seriesZ);
        seriesZ.setColor(Color.RED);
    }

    public ArrayList<ArrayList<Float>> stopGraph() {
        graph.removeAllSeries();
        series.add(series_x);
        series.add(series_y);
        series.add(series_z);
        return series;
    }

    public void addEntry(float x, float y, float z) {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        seriesX.appendData(new DataPoint(lastX, x), false, 10);
        seriesY.appendData(new DataPoint(lastX, y), false, 10);
        seriesZ.appendData(new DataPoint(lastX, z), false, 10);


    }

    public final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;
            if (checkSensor == true) {
                if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    if ((System.currentTimeMillis() - lastSaved) > ACC_CHECK_INTERVAL) {
//                    Log.d("Test","ResultX: "+x);
//                    Log.d("Test","ResultY: "+y);
//                    Log.d("Test","ResultZ: "+z);
                        lastSaved = System.currentTimeMillis();
                        x = sensorEvent.values[0];
                        y = sensorEvent.values[1];
                        z = sensorEvent.values[2];

                        if (buttonStartStop) {
                            //Log.d("Test","lastX: "+lastX);
                            series_x.add(x);
                            series_y.add(y);
                            series_z.add(z);

                            //might delete later
                            AddNewDBEntry();
                            addEntry(x, y, z);
                            lastX++;
                        }

                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    public boolean isButtonStartStop() {
        return buttonStartStop;
    }

    public void setButtonStartStop(boolean buttonStartStop) {
        this.buttonStartStop = buttonStartStop;
    }

    public boolean isCheckSensor() {
        return checkSensor;
    }

    public void setCheckSensor(boolean checkSensor) {
        this.checkSensor = checkSensor;
    }


    //Might delete all the db functions later
    public void AddNewDBEntry(){
        dbHandler.insertNewData(Float.toString(x),Float.toString(y),Float.toString(z));
    }


    public boolean CreateTable(String name, String id, String age, String sex){
        //Log.d(TAG,"Check Info create");
        if(checkUserInfo(name, id, age, sex)) {
            //Log.d(TAG, "Created DB");
            dbHandler = new DBHelper(context.getApplicationContext(), person);
            return true;
        }
        return false;
    }

    private boolean checkUserInfo(String name, String id, String age, String sex){

        person = new PersonInfo(name, age, sex, id);
        return true;

    }

}


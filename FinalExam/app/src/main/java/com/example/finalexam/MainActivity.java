package com.example.finalexam;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button collectCop;
    Button collectHungry;
    Button collectHeadache;
    Button collectAbout;
    Button predictCop;
    Button predictHungry;
    Button predictHeadache;
    Button predictAbout;
    RelativeLayout overlayView;
    RelativeLayout linearLayout1;
    RelativeLayout predictGestureLayout;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private SensorManager sensorManager;

    private static final String TAG = "MyActivity";
    TextView falsePositive;
    TextView truePositive;
    short check_result;

    private boolean buttonStartStop = false;
    private boolean checkSensor = false;
    private boolean buttonStart = true;

    ArrayList<ArrayList<Float[]>> collectGestures_accelero;
    ArrayList<ArrayList<Float[]>> collectGestures_gyroscope;
    private GraphView graph;

    //    ArrayList<ArrayList<Float>> series_accelero;
//    ArrayList<ArrayList<Float>> series_gyroscope;
    ArrayList<Float[]> series_accelero;
    ArrayList<Float[]> series_gyroscope;
    Float[] sensor = new Float[3];

    private final static long ACC_CHECK_INTERVAL_BEGIN = 500; // 1000ms
    private final static long DURATION_DATA = 2000;
    private long begin = System.currentTimeMillis();

    //    GraphFunctions gf;
    View view;
    PredictGesture gestures;

    TextView title_gv;
    int number;
    short[] label = {0,0,0,0,1,1,1,1,2,2,2,2,3,3,3,3};
    float diff_x,diff_z,diff_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        collectCop = (Button) findViewById(R.id.collect_cop_data);
        collectHungry = (Button) findViewById(R.id.collect_hungry_data);
        collectHeadache = (Button) findViewById(R.id.collect_headache_data);
        collectAbout = (Button) findViewById(R.id.collect_about_data);
        predictCop = (Button) findViewById(R.id.predict_cop_data);
        predictHungry = (Button) findViewById(R.id.predict_hungry_data);
        predictHeadache = (Button) findViewById(R.id.predict_headache_data);
        predictAbout = (Button) findViewById(R.id.predict_about_data);
        title_gv = (TextView) findViewById(R.id.title_gv);
        falsePositive = (TextView) findViewById(R.id.false_positive);
        truePositive = (TextView) findViewById(R.id.true_positive);

        //overlayView = findViewById(R.id.top_layout);
        linearLayout1 = findViewById(R.id.linearLayout1);
        predictGestureLayout = findViewById(R.id.predictGestureLayout);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(sensorListener, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, gyroscope, sensorManager.SENSOR_DELAY_NORMAL);

        createNewSeries();

        collectGestures_accelero = new ArrayList<>();
        collectGestures_gyroscope = new ArrayList<>();

//        collectGestures = new ArrayList<>();
        number = 0;


        //COLLECT COP DATA - Mario Padilla
        collectCop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //overlayView.setVisibility(View.VISIBLE);
                //setButtons(false);
                Toast.makeText(getApplicationContext(), "collect Cop", Toast.LENGTH_SHORT).show();

                //gf.CreateTable("test", String.valueOf(number), String.valueOf(number), String.valueOf(number));
                startRunGraph("Collect Cop");
                number++;
            }
        });



        predictCop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestures = new PredictGesture(getApplicationContext(), view);
                setButtons(false);
                predictGestureLayout.setVisibility(View.VISIBLE);
                int[] results = gestures.PredictCopGesture(collectGestures_accelero,collectGestures_gyroscope,label);
                truePositive.setText(String.valueOf(results[0]) + "/" + "4");
                falsePositive.setText(String.valueOf(results[1])+ "/" + "12");

            }
        });

        predictGestureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    predictGestureLayout.setVisibility(View.GONE);
                    Thread.sleep(0);
                    setButtons(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


//        overlayView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //stopRunGraph();
//            }
//        });overlayView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //stopRunGraph();
//            }
//        });
        ///// End of mario

        collectHungry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Collect Hungry", Toast.LENGTH_SHORT).show();
                //overlayView.setVisibility(View.VISIBLE);
                //setButtons(false);
//                Toast.makeText(getApplicationContext(), "collect copt", Toast.LENGTH_LONG).show();
                number++;
                startRunGraph("Collect Hungry");
            }
        });


        collectHeadache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Collect Headache", Toast.LENGTH_SHORT).show();
                //overlayView.setVisibility(View.VISIBLE);
                //setButtons(false);
//                Toast.makeText(getApplicationContext(), "collect copt", Toast.LENGTH_LONG).show();
                number++;
                startRunGraph("Collect Headache");
            }
        });

        collectAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Collect About", Toast.LENGTH_SHORT).show();
                //overlayView.setVisibility(View.VISIBLE);
                //setButtons(false);
//                Toast.makeText(getApplicationContext(), "collect copt", Toast.LENGTH_LONG).show();
                number++;
                startRunGraph("Collect About");
            }
        });

        predictHungry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Predict Hungry", Toast.LENGTH_SHORT).show();
                gestures = new PredictGesture(getApplicationContext(), view);
                setButtons(false);
                predictGestureLayout.setVisibility(View.VISIBLE);
                int[] results = gestures.PredictHungryGesture(collectGestures_accelero,collectGestures_gyroscope,label);
                truePositive.setText(String.valueOf(results[0]) + "/" + "4");
                falsePositive.setText(String.valueOf(results[1])+ "/" + "12");


            }
        });

        predictHeadache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Predict Headache", Toast.LENGTH_SHORT).show();
                gestures = new PredictGesture(getApplicationContext(), view);
                setButtons(false);
                predictGestureLayout.setVisibility(View.VISIBLE);
                int[] results = gestures.PredictHeadacheGesture(collectGestures_accelero,collectGestures_gyroscope,label);
                truePositive.setText(String.valueOf(results[0]) + "/" + "4");
                falsePositive.setText(String.valueOf(results[1])+ "/" + "12");

            }
        });


        predictAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Predict About", Toast.LENGTH_SHORT).show();
                gestures = new PredictGesture(getApplicationContext(), view);
                setButtons(false);
                predictGestureLayout.setVisibility(View.VISIBLE);
                int[] results = gestures.PredictAboutGesture(collectGestures_accelero,collectGestures_gyroscope,label);
                truePositive.setText(String.valueOf(results[0]) + "/" + "4");
                falsePositive.setText(String.valueOf(results[1])+ "/" + "12");
                //showResult();

            }
        });

    }


    public void setButtons(boolean enable) {
        for (int i = 0; i < linearLayout1.getChildCount(); i++) {
            View child = linearLayout1.getChildAt(i);
            //your processing....
            child.setEnabled(enable);
        }
    }

    public void startRunGraph(final String message){

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                gf.setButtonStartStop(true);
//                gf.setCheckSensor(true);
//                gf.startGraph();
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
////                            Toast.makeText(getApplicationContext(),"X1: " + total.get(0).get(check_result)[0] + " and X2: " +  total.get(0).get(check_result - 1)[0],Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(),"Done", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//        }).start();

        buttonStartStop = true;
        //If not included it will create mutliple startGraph (speed it up)
        if(buttonStart) {
            buttonStart = false;
            checkSensor = true;
            begin = System.currentTimeMillis();
//            sample = new ArrayList<Float[]>();
//            sampleGyro = new ArrayList<Float[]>();
            createNewSeries();
//            Toast.makeText(getApplicationContext(),"Let's do it ", Toast.LENGTH_SHORT).show();
//                        count_sample = 0;
//            startGraph();
        }

    }

//    public void stopRunGraph(){
//
//        gf.setButtonStartStop(false);
//        gf.setCheckSensor(false);
//        try {
//            Thread.sleep(1000);
////            collectGestures = gf.stopGraph();
////            collectGestures_accelero.add(collectGestures.get(0));
////            collectGestures_gyroscope.add(collectGestures.get(1));
//            //gf = new GraphFunctions(getApplicationContext(),graph);
//            overlayView.setVisibility(View.GONE);
//            Thread.sleep(1000);
//            setButtons(true);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }

    private void createNewSeries() {
//        series_x_accelero = new ArrayList<>();
//        series_y_accelero = new ArrayList<>();
//        series_z_accelero = new ArrayList<>();
//        series_x_gyroscope = new ArrayList<>();
//        series_y_gyroscope = new ArrayList<>();
//        series_z_gyroscope = new ArrayList<>();


        series_accelero = new ArrayList<>();
        series_gyroscope = new ArrayList<>();
    }

    public final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;
            if (checkSensor == true) {
                if ((System.currentTimeMillis() - begin) > ACC_CHECK_INTERVAL_BEGIN) {
                    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                    Log.d("Test","ResultX: "+x);
//                    Log.d("Test","ResultY: "+y);
//                    Log.d("Test","ResultZ: "+z);
//                        lastSaved = System.currentTimeMillis();
                        sensor = new Float[3];
                        sensor[0] = sensorEvent.values[0];
                        sensor[1] = sensorEvent.values[1];
                        sensor[2] = sensorEvent.values[2];
                        series_accelero.add(sensor);

//                        if (buttonStartStop) {
//                            //Log.d("Test","lastX: "+lastX);
//                            series_x_accelero.add(x);
//                            series_y_accelero.add(y);
//                            series_z_accelero.add(z);
//
//                            //might delete later
////                            AddNewDBEntry();
////                            addEntry(x, y, z);
////                            lastX++;
//                        }

                    }

                    else if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
//                        Log.d("Test","ResultX: "+x);
//                        Log.d("Test","ResultY: "+y);
//                        Log.d("Test","ResultZ: "+z);
//                        lastSaved = System.currentTimeMillis();
                        sensor = new Float[3];
                        sensor[0] = sensorEvent.values[0];
                        sensor[1] = sensorEvent.values[1];
                        sensor[2] = sensorEvent.values[2];
                        series_gyroscope.add(sensor);

//                        if (buttonStartStop) {
//                            //Log.d("Test","lastX: "+lastX);
//                            series_x_gyroscope.add(x);
//                            series_y_gyroscope.add(y);
//                            series_z_gyroscope.add(z);
//
//                            //might delete later
////                            AddNewDBEntry();
////                            addEntry(x, y, z);
////                            lastX++;
//                        }

                    }

                    if(System.currentTimeMillis() - begin > DURATION_DATA) {
                        checkSensor = false;
                        buttonStartStop = false;
                        collectGestures_accelero.add(series_accelero);
                        collectGestures_gyroscope.add(series_gyroscope);
                        buttonStart = true;
                        Toast.makeText(getApplicationContext(),"Done " + number, Toast.LENGTH_SHORT).show();

//                        buttonStart = true;
//                        Toast.makeText(getApplicationContext(),"Done ").show();
//                        Toast.makeText(getApplicationContext(),"Sample Size " + sample.size(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(),"Done " + count_gesture, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(),"Sample Size sampleGyro " + sampleGyro.size(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void showResult() {
//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        graph.addSeries(series);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries

                for (check_result = 0; check_result < collectGestures_accelero.size(); check_result++)  {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
//                            diff_x = collectGestures_accelero.get(0).get(check_result)[0] - collectGestures_accelero.get(0).get(check_result - 1)[0];
//
//                            diff_z = collectGestures_accelero.get(0).get(check_result)[2] - collectGestures_accelero.get(0).get(check_result-1)[2];
////                            diff_gyro_y = collectGestures_gyroscope.get(0).get(check_result)[1] - collectGestures_gyroscope.get(0).get(check_result-1)[1];
//                            diff_y = collectGestures_accelero.get(0).get(check_result)[1] - collectGestures_accelero.get(0).get(check_result-1)[1];
//                            Toast.makeText(getApplicationContext(),"X1: " + total.get(0).get(check_result)[0] + " and X2: " +  total.get(0).get(check_result - 1)[0],Toast.LENGTH_SHORT).show();
                            Float[] count = new Float[3];
                            count = check(collectGestures_accelero.get(check_result));
                            Toast.makeText(getApplicationContext(),"Sample: " + check_result +" and X: " + count[0] +  " and Z: " + count[2] +
                                    " and Y: " + count[1], Toast.LENGTH_SHORT).show();

                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();

//        buttonStartStop = false;
//        mButtonReset.setVisibility(View.VISIBLE);
    }

    private Float[] check (ArrayList<Float[]> data) {
        Float[] count = new Float[3];
        short countX = 0,countY = 0,countZ = 0;
        for (short i = 1; i < data.size(); i++) {
            if(Math.abs(data.get(i)[0] - data.get(i-1)[0] ) > 1)
                countX++;
            if(Math.abs(data.get(i)[1] - data.get(i-1)[1])  > 1)
                countY++;
            if(Math.abs(data.get(i)[2] - data.get(i-1)[2])  > 1)
                countZ++;
        }
        count[0] = (float)countX/(float)(data.size()-1);
        count[1] = (float)countY/(float)(data.size()-1);
        count[2] = (float)countZ/(float)(data.size()-1);
        return count;
    }
}


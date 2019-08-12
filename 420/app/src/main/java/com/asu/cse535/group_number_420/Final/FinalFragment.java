package com.asu.cse535.group_number_420.Final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.asu.cse535.group_number_420.R;

import java.util.ArrayList;

public class FinalFragment extends Fragment {

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

    private static final String TAG = "MyActivity";
    TextView falsePositive;
    TextView truePositive;

    ArrayList<ArrayList<ArrayList<Float>>> collectGestures;
    ArrayList<ArrayList<Float>> cop_x_y_z_series;

    GraphFunctions gf;
    View view;
    PredictGesture gestures;

    TextView title_gv;
    int number;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_final, container, false);

        getActivity().setTitle("CSE535 FINAL");

        collectCop = (Button) view.findViewById(R.id.collect_cop_data);
        collectHungry = (Button) view.findViewById(R.id.collect_hungry_data);
        collectHeadache = (Button) view.findViewById(R.id.collect_headache_data);
        collectAbout = (Button) view.findViewById(R.id.collect_about_data);
        predictCop = (Button) view.findViewById(R.id.predict_cop_data);
        predictHungry = (Button) view.findViewById(R.id.predict_hungry_data);
        predictHeadache = (Button) view.findViewById(R.id.predict_headache_data);
        predictAbout = (Button) view.findViewById(R.id.predict_about_data);
        title_gv = (TextView) view.findViewById(R.id.title_gv);
        falsePositive = (TextView) this.view.findViewById(R.id.false_positive);
        truePositive = (TextView) this.view.findViewById(R.id.true_positive);

        overlayView = view.findViewById(R.id.top_layout);
        linearLayout1 = view.findViewById(R.id.linearLayout1);
        predictGestureLayout = view.findViewById(R.id.predictGestureLayout);

        gf = new GraphFunctions(getContext(), view);

        collectGestures = new ArrayList<>();
        number = 0;

        //COLLECT COP DATA - Mario Padilla
        collectCop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlayView.setVisibility(View.VISIBLE);
                setButtons(false);
                Toast.makeText(getActivity(), "collect copt", Toast.LENGTH_LONG).show();

                cop_x_y_z_series = new ArrayList<>();
                gf.CreateTable("test", String.valueOf(number), String.valueOf(number), String.valueOf(number));
                startRunGraph("Collect Cop");
                number++;
            }
        });



        predictCop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gestures = new PredictGesture(getContext(), view);
                setButtons(false);
                predictGestureLayout.setVisibility(View.VISIBLE);
                int[] results = gestures.PredictCutGesture(collectGestures);
                truePositive.setText(String.valueOf(results[0]) + "/" + "4");
                falsePositive.setText(String.valueOf(results[1])+ "/" + String.valueOf(collectGestures.size()));

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


        overlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRunGraph();
            }
        });
        ///// End of mario

        collectHungry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Collect hungry", Toast.LENGTH_LONG).show();
                overlayView.setVisibility(View.VISIBLE);
                setButtons(false);
                Toast.makeText(getActivity(), "collect copt", Toast.LENGTH_LONG).show();

                cop_x_y_z_series = new ArrayList<>();
                startRunGraph("Collect Hungry");
            }
        });


        collectHeadache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Collect headache", Toast.LENGTH_LONG).show();
                overlayView.setVisibility(View.VISIBLE);
                setButtons(false);
                Toast.makeText(getActivity(), "collect copt", Toast.LENGTH_LONG).show();

                cop_x_y_z_series = new ArrayList<>();
                startRunGraph("Collect Hungry");
            }
        });

        collectAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Collect about", Toast.LENGTH_LONG).show();
                overlayView.setVisibility(View.VISIBLE);
                setButtons(false);
                Toast.makeText(getActivity(), "collect copt", Toast.LENGTH_LONG).show();

                cop_x_y_z_series = new ArrayList<>();
                startRunGraph("Collect Hungry");
            }
        });

        predictHungry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Predict Hungry", Toast.LENGTH_LONG).show();
                gestures = new PredictGesture(getContext(), view);
                setButtons(false);
                predictGestureLayout.setVisibility(View.VISIBLE);
                int[] results = gestures.PredictHungryGesture(collectGestures);
                truePositive.setText(String.valueOf(results[0]));
                falsePositive.setText(String.valueOf(results[1]));


            }
        });

        predictHeadache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Predict Headache", Toast.LENGTH_LONG).show();
                gestures = new PredictGesture(getContext(), view);
                setButtons(false);
                predictGestureLayout.setVisibility(View.VISIBLE);
                int[] results = gestures.PredictHeadacheGesture(collectGestures);
                truePositive.setText(String.valueOf(results[0]));
                falsePositive.setText(String.valueOf(results[1]));

            }
        });


        predictAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Predict About", Toast.LENGTH_LONG).show();
                gestures = new PredictGesture(getContext(), view);
                setButtons(false);
                predictGestureLayout.setVisibility(View.VISIBLE);
                int[] results = gestures.PredictAboutGesture(collectGestures);
                truePositive.setText(String.valueOf(results[0]));
                falsePositive.setText(String.valueOf(results[1]));

            }
        });

        return view;
    }


    public void setButtons(boolean enable) {
        for (int i = 0; i < linearLayout1.getChildCount(); i++) {
            View child = linearLayout1.getChildAt(i);
            //your processing....
            child.setEnabled(enable);
        }
    }

    public void startRunGraph(final String message){

        new Thread(new Runnable() {
            @Override
            public void run() {
                gf.setButtonStartStop(true);
                gf.setCheckSensor(true);
                gf.startGraph(view);
            }
        }).start();

    }

    public void stopRunGraph(){

        gf.setButtonStartStop(false);
        gf.setCheckSensor(false);
        try {
            Thread.sleep(1000);
            cop_x_y_z_series = gf.stopGraph();
            collectGestures.add(cop_x_y_z_series);
            gf = new GraphFunctions(getContext(), view);
            overlayView.setVisibility(View.GONE);
            Thread.sleep(1000);
            setButtons(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

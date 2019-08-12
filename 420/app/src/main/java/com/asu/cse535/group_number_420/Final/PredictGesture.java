package com.asu.cse535.group_number_420.Final;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

public class PredictGesture {


    private Context context;
    private View view;

    private int tp;
    private int fp;

    PredictGesture(Context context, View view){
        this.context = context;
        this.view = view;
        tp = 0;
        fp = 0;
    }


    public int[] PredictCutGesture(ArrayList<ArrayList<ArrayList<Float>>> collectedCopGestures){
        int [] result = {1,2};

        return result;
    }

    public int[] PredictHungryGesture(ArrayList<ArrayList<ArrayList<Float>>> collectedGestures){
        int [] result = {1,2};

        return result;
    }

    public int[] PredictAboutGesture(ArrayList<ArrayList<ArrayList<Float>>> collectedGestures){
        int [] result = {1,2};

        return result;
    }

    public int[] PredictHeadacheGesture(ArrayList<ArrayList<ArrayList<Float>>> collectedGestures){
        int [] result = {1,2};

        return result;
    }
}

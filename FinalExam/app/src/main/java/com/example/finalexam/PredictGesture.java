package com.example.finalexam;

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


    public int[] PredictCopGesture(ArrayList<ArrayList<Float[]>> collectedGestures,
                                   ArrayList<ArrayList<Float[]>> collectedGesturesGyroscope,
                                   short[] label){
        int FPvalues = 0;
        boolean gyroscopeValuePresent = false;

        int [] result = new int[2];

        boolean itExist = false;
        int truePositive = 0;

//        collectedGestures = addValues(collectedGestures);
        //      ArrayList<ArrayList<ArrayList<Float>>> collectedGesturess = new ArrayList<>();
        //     ArrayList<ArrayList<ArrayList<Float>>> collectedGesturesGyroscope = addValuesGyro(collectedGesturess);

        //First four gestures are cup
        for(int i = 0; i < 4; i++) {
            //Grap x-axis of i gesture
            ArrayList<Float[]> x_axis_arraylist =  collectedGestures.get(i);
            truePositive = truePositive + CheckCop(x_axis_arraylist);
        }

        result[0] = truePositive;


        //hungry
        for (int i = 4; i < 7; i++){
            ArrayList<Float[]> x_axis_arraylist =  collectedGestures.get(i);
            FPvalues = FPvalues + CheckCop(x_axis_arraylist);
        }

        //about & headache - checks if the gyroscope has a value (if so don't increase false positive)
        // if it doesn't have a gyrscope value then we will check for Cop gesture
        //        for (int i = 8; i < 15; i++){
        for (int i = 8; i < 15; i++){
            gyroscopeValuePresent = false;
            ArrayList<Float[]> x_axis_arraylist_gyroscope =  collectedGesturesGyroscope.get(i);
            ArrayList<Float[]> y_axis_arraylist_gyroscope =  collectedGesturesGyroscope.get(i);
            ArrayList<Float[]> z_axis_arraylist_gyroscope =  collectedGesturesGyroscope.get(i);

            ArrayList<Float[]> x_axis_arraylist =  collectedGestures.get(i);

            for(int j =0; j < x_axis_arraylist_gyroscope.size(); j++ ){

                if(x_axis_arraylist_gyroscope.get(j)[0] != 0){
                    gyroscopeValuePresent = true;
                    break;
                }
                if(y_axis_arraylist_gyroscope.get(j)[1] != 0){
                    gyroscopeValuePresent = true;
                    break;
                }
                if(z_axis_arraylist_gyroscope.get(j)[2] != 0){
                    gyroscopeValuePresent = true;
                    break;
                }

            }

            //Check cop
            if(gyroscopeValuePresent != true){
                FPvalues = FPvalues + CheckCop(x_axis_arraylist);
            }

        }

        result[1] = FPvalues;


        return result;
    }

    public int[] PredictHungryGesture(ArrayList<ArrayList<Float[]>> collectGestures_accelero,
                                      ArrayList<ArrayList<Float[]>> collectGestures_gyroscope,
                                      short[] label){
        int [] result = {0,0};
        int i = 0;
        int j;

        // Iterate over all 16 set of collected gestures data
        for(; i < collectGestures_accelero.size(); i++){

            int x_zero_crossing = 0;
            int y_zero_crossing = 0;
            int z_zero_crossing = 0;

            //Analyze x axis data
            for (j = 0 ; j < collectGestures_accelero.get(i).size()-1;j++){
                float current = collectGestures_accelero.get(i).get(j)[0];
                float next = collectGestures_accelero.get(i).get(j+1)[0];
                if(current < 0 && next > 0 ){

                    x_zero_crossing++;

                }
                else if(current > 0 && next < 0)
                    x_zero_crossing++;
            }

            //Analyze y axis data

            for (j = 0 ; j < collectGestures_accelero.get(i).size()-1;j++){
                float current = collectGestures_accelero.get(i).get(j)[1];
                float next = collectGestures_accelero.get(i).get(j+1)[1];
                if(current < 0 && next > 0 ){

                    y_zero_crossing++;

                }
                else if(current > 0 && next < 0)
                    y_zero_crossing++;

            }

            //Analyze z axis data

            for (j = 0 ; j < collectGestures_accelero.get(i).size()-1;j++){
                float current = collectGestures_accelero.get(i).get(j)[2];
                float next = collectGestures_accelero.get(i).get(j+1)[2];
                if(current < 0 && next > 0 ){

                    z_zero_crossing++;

                }
                else if(current > 0 && next < 0)
                    z_zero_crossing++;


            }

            if(i > 3 && i < 8){

                if (x_zero_crossing > 0 && y_zero_crossing < 2 && z_zero_crossing > 0)
                    result[0] += 1;

            }
            else{

                if (x_zero_crossing > 0 && y_zero_crossing < 2 && z_zero_crossing > 0)
                    result[1] += 1;

            }


        }

        return result;
    }

    public int[] PredictAboutGesture(ArrayList<ArrayList<Float[]>> collectGestures_accelero,
                                     ArrayList<ArrayList<Float[]>> collectGestures_gyroscope,
                                     short[] label){
        int [] result = {0,0};
        short count;
        float diff_x,diff_y, diff_z;
        float diff_gyro_y;

        float aboutAccle;
        float aboutGyro;
//        System.out.println(collectGestures_accelero.size());
//        System.out.println(collectGestures_gyroscope.size());
//        System.out.println(collectGestures_gyroscope.get(0).size());
//        System.out.println(collectGestures_accelero.get(0).size());
        short[] predict = new short[16];
        for (short i = 0; i < collectGestures_accelero.size(); i++) {
            count = 0;
//            Toast.makeText(getApplicationContext(),"Sampe Size " + i + " : " + total.get(i).size(), Toast.LENGTH_SHORT).show();
            for (short j = 1; j < collectGestures_accelero.get(i).size(); j++) {
                diff_x = collectGestures_accelero.get(i).get(j)[0] - collectGestures_accelero.get(i).get(j-1)[0];
                diff_z = collectGestures_accelero.get(i).get(j)[2] - collectGestures_accelero.get(i).get(j-1)[2];
                diff_y = collectGestures_accelero.get(i).get(j)[1] - collectGestures_accelero.get(i).get(j-1)[1];

                if(Math.abs(diff_x) > 0.5 && Math.abs(diff_z) > 0.5)
                    count++;
            }
            aboutAccle = (float)count/(float)(collectGestures_accelero.get(i).size()-1);
            count = 0;
            for (short j = 1; j < collectGestures_gyroscope.get(i).size(); j++) {
                diff_gyro_y = collectGestures_gyroscope.get(i).get(j)[1] - collectGestures_gyroscope.get(i).get(j-1)[1];
//                diff_z = collectGestures_accelero.get(i).get(2).get(j) - collectGestures_accelero.get(i).get(2).get(j - 1);

                if(Math.abs(diff_gyro_y) > 5)
                    count++;
            }
            aboutGyro = (float)count/(float)(collectGestures_gyroscope.get(i).size()-1);
            if(aboutAccle > 0.4 && aboutGyro < 0.2)
                predict[i] = 3;
            else
                predict[i] = 0;

        }

        for (short i = 0; i < collectGestures_accelero.size(); i++) {
            if(predict[i] == label[i] && predict[i] == 3)
                result[0]++;
            else if(predict[i] != label[i] && predict[i] == 3)
                result[1]++;

        }
//        float aboutAccle = (float)count/(float)(collectGestures_accelero.get(0).size()-1);
//
//        for (short i = 0; i < totalGyro.size(); i++) {
//            count = 0;
////            Toast.makeText(getApplicationContext(),"Sampe Size " + i + " : " + total.get(i).size(), Toast.LENGTH_SHORT).show();
//            for (short j = 1; j < totalGyro.get(i).size(); j++) {
//                if(Math.abs(totalGyro.get(i).get(j)[1] - totalGyro.get(i).get(j - 1)[1]) > 5)
//                    count++;
//            }
////            if (((float)count/(float)(total.get(i).size()-1)) >= 0.7)
////                predict[i] = 1;
////            else
////                predict[i] = 0;
//        }
//
//        float aboutGyro = (float)count/(float)(totalGyro.get(0).size()-1);
//
//        short truePositive = 0;
//        short falsePositive = 0;
//
//        for(short i = 0; i < total.size(); i++) {
//            if(predict[i] == label[i] && predict[i] == 1)
//                truePositive++;
//            else if(predict[i] == 1 && label[i] != 1)
//                falsePositive++;
//            Toast.makeText(getApplicationContext(),"Predict " + i + " : " + predict[i], Toast.LENGTH_SHORT).show();
//        }

        return result;
    }

    public int[] PredictHeadacheGesture(ArrayList<ArrayList<Float[]>> collectGestures_accelero,
                                        ArrayList<ArrayList<Float[]>> collectGestures_gyroscope,
                                        short[] label){
        int [] result = {0,0};
        short count;
        float diff_gyro_y;

        float aboutGyro;

        short[] predict = new short[16];
        for (short i = 0; i < collectGestures_gyroscope.size(); i++) {
//            count = 0;
////            Toast.makeText(getApplicationContext(),"Sampe Size " + i + " : " + total.get(i).size(), Toast.LENGTH_SHORT).show();
//            for (short j = 1; j < collectGestures_accelero.get(i).get(0).size(); j++) {
//                diff_x = collectGestures_accelero.get(i).get(0).get(j) - collectGestures_accelero.get(i).get(0).get(j - 1);
//                diff_z = collectGestures_accelero.get(i).get(2).get(j) - collectGestures_accelero.get(i).get(2).get(j - 1);
//
//                if(Math.abs(diff_x) > 1 && Math.abs(diff_z) > 1)
//                    count++;
//            }
//            aboutAccle = (float)count/(float)(collectGestures_accelero.get(i).get(0).size()-1);
            count = 0;
            for (short j = 1; j < collectGestures_gyroscope.get(i).size(); j++) {
                diff_gyro_y = collectGestures_gyroscope.get(i).get(j)[1] - collectGestures_gyroscope.get(i).get(j-1)[1];
//                diff_z = collectGestures_accelero.get(i).get(2).get(j) - collectGestures_accelero.get(i).get(2).get(j - 1);

                if(Math.abs(diff_gyro_y) > 8)
                    count++;
            }
            aboutGyro = (float)count/(float)(collectGestures_gyroscope.get(i).size()-1);
            if(aboutGyro > 0.4)
                predict[i] = 2;
            else
                predict[i] = 0;

        }

        for (short i = 0; i < collectGestures_accelero.size(); i++) {
            if(predict[i] == label[i] && predict[i] == 2)
                result[0]++;
            else if(predict[i] != label[i] && predict[i] == 2)
                result[1]++;

        }

        return result;
    }

    private int CheckCop(ArrayList<Float[]> x_axis){
        int count = 0;
        float current = 0;
        float future =0;
        int results = 0;
        boolean firstneg = false;
        boolean secondneg = false;
        boolean completeSinoWave = false;

        boolean positiveNum = false;
        int numberConsectivePositive = 0;
        int number2ndConsectivePositive = 0;

        for(int j = 0; j < x_axis.size()-1; j++){

            current = x_axis.get(j)[0];
            int temp = j + 1;
            future = x_axis.get(temp)[0];

            float diff =  future - current;

            if(firstneg == false){
                //Its neg
                if(diff < 0){
                    firstneg = true;
                }
                else{
                    numberConsectivePositive++;
                }
            }
            else{
                if(secondneg == false){
                    //found second negative diff
                    if(diff < 0){
                        secondneg = true;
                        completeSinoWave = true;
                    }
                    else{
                        number2ndConsectivePositive++;
                    }
                }

            }


            if(completeSinoWave == true){
                if(diff < 0){
                    count++;
                    //its decreasing  continue
                    if(count == 2){
//                        Log.i("tag", "increase results");
                        results = results+1;
                        break;
                    }

                }
                else{
                    break;
                }

            }


        }

        return results;

    }
}

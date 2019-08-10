package com.asu.cse535.project;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.asu.cse535.project.maps.MapFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * @author mario padilla, efren lopez
 */
public class HomeFragment extends Fragment {
    public boolean click = false;
    public Button alertButton;
    LinearLayout layout1;
    LinearLayout layout2;
    LinearLayout layout3;
    LinearLayout layout4;
    SmsManager smgr;
    User user;
    String textMessage = "";
    String LOG = "MainActivity";

    FirebaseUser FireBaseSignInAccount;
    FirebaseFirestore firebasedb;
    String UserID;
    ArrayList<String> keys;
    ArrayList<Object> values;
    ArrayList<String> phone_keys;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view  = inflater.inflate(R.layout.fragment_home, container, false);

        TextView test = (TextView) view.findViewById(R.id.welcome);

        alertButton = (Button) view.findViewById(R.id.alert_btn);
        layout1 = (LinearLayout) view.findViewById(R.id.layout1);
        layout2 = (LinearLayout) view.findViewById(R.id.layout2);
        layout3 = (LinearLayout) view.findViewById(R.id.layout3);
        layout4 = (LinearLayout) view.findViewById(R.id.layout4);


        alertButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                // Replace
                click = !click;
                if(click) {
                    alertButton.setText("Cancel");
                    Toast.makeText( getActivity(),"Alert!", Toast.LENGTH_SHORT).show();
                    makeSound ();
                    SendTextMessage stm = new SendTextMessage();
                    stm.sendMessage(getActivity(), false);
                }
                else{
                    SendTextMessage stm = new SendTextMessage();
                    stm.sendMessage(getActivity(), true);
                }



            }
        });



        //Access mainActivity
        FirebaseUser AreYouSignInAccount = ((MainActivity) getActivity()).getAreYouSignInAccount();

        if(AreYouSignInAccount != null) {
            test.setText("Welcome " + AreYouSignInAccount.getDisplayName());
            alertButton.setVisibility(view.VISIBLE);
            layout1.setVisibility(view.VISIBLE);
            layout2.setVisibility(view.VISIBLE);
            layout3.setVisibility(view.VISIBLE);
            layout4.setVisibility(view.VISIBLE);

            firebasedb = ((MainActivity) getActivity()).initFirestore();
            UserID = AreYouSignInAccount.getUid();

        }


        layout1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Code here executes on main thread after user presses button
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyContactFragment()).commit();

                // Replace
                Toast.makeText( getActivity(),"Emergency contact", Toast.LENGTH_SHORT).show();
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                // Replace
                Toast.makeText( getActivity(),"Cancel", Toast.LENGTH_SHORT).show();
            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                // Replace
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                Toast.makeText( getActivity(),"View Safe Zone", Toast.LENGTH_SHORT).show();
            }
        });

        layout4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                // Replace
                Toast.makeText( getActivity(),"View Unsafe Zone", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    //Used for the shaking mechanism in the Main activity
    public void ClickFunction(){

        click = !click;
        if(click) {
            SendTextMessage stm = new SendTextMessage();
            stm.sendMessage(getActivity(), false);

            alertButton.setText("Cancel");
            makeSound_shake ();

            //Toast.makeText( getActivity(),"Alert!", Toast.LENGTH_SHORT).show();
        }
        else{
            SendTextMessage stm = new SendTextMessage();
            stm.sendMessage(getActivity(), true);
        }

    }

    private void makeSound_shake () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    System.out.println("Shake: " + shaking);
//                    System.out.println("Wait: " + wait);
                    // we add 100 new entries
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                    r.play();
                    for (int i = 50; i > 0; i--) {
                        //Toast.makeText( getActivity(),"Cancel Alert in: " +i + " seconds", Toast.LENGTH_SHORT).show();
                        Thread.sleep(200);
                        if(!click){
                            r.stop();
                            ((MainActivity) getActivity()).wait = false;
                            break;

                        }

                    }
                    if(click) {
                        r.stop();
                        ((MainActivity) getActivity()).wait = false;
                        click = false;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            alertButton.setText("Alert!");
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void makeSound () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    System.out.println("Shake: " + shaking);
//                    System.out.println("Wait: " + wait);
                    // we add 100 new entries
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                    r.play();
                    for (int i = 50; i > 0; i--) {
                        //Toast.makeText( getActivity(),"Cancel Alert in: " +i + " seconds", Toast.LENGTH_SHORT).show();
                        Thread.sleep(200);
                        if(!click){
                            r.stop();
                            break;

                        }

                    }
                    if(click) {
                        r.stop();
                        click = false;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            alertButton.setText("Alert!");
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}

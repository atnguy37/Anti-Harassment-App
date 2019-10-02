package com.asu.cse535.project;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.asu.cse535.project.maps.MapFragment;
import com.asu.cse535.project.maps.MapsActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.asu.cse535.project.Constant.REQUEST_CODE_CURRENT_LOCATION;

/**
 * @author mario padilla, efren lopez
 */
public class HomeFragment extends Fragment {
    private boolean click = false;
    private Button alertButton;
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


    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    private Intent receive,send;

    private KeyguardManager mKeyguardManager;
    private String activity;


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

        mKeyguardManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);

        if (!mKeyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a lock screen.
            Toast.makeText(getContext(), "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
                    Toast.LENGTH_LONG).show();
//            purchaseButton.setEnabled(false);
            return null;
        }

        alertButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                // Replace
                System.out.println("Click on Listener: " + click);
                if(!click) {
                    click = !click;
                    MainActivity.updateAlert(click);
                    alertButton.setText("Cancel");
                    Toast.makeText( getActivity(),"Alert!", Toast.LENGTH_SHORT).show();
                    activity = "Button";
                    makeSound ();
                    SendTextMessage stm = new SendTextMessage();
                    stm.sendMessage(getActivity(), false);
                }
                else{
                    showAuthenticationScreen();
                    SendTextMessage stm = new SendTextMessage();
                    stm.sendMessage(getActivity(), true);
                }



            }
        });


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        requestAudioPermissions();

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
                speechRecognizer.startListening(speechRecognizerIntent);
                Toast.makeText( getActivity(),"Voice", Toast.LENGTH_SHORT).show();
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
                Toast.makeText( getActivity(),"Voice Recognition", Toast.LENGTH_SHORT).show();
            }
        });


        promptSpeechInput();
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(result != null) {
                    Toast.makeText(getContext(),"Result: " + result.get(0), Toast.LENGTH_SHORT).show();
                    if(result.get(0).toLowerCase().indexOf("help") >= 0) {
                        click = !click;
                        if(click) {
                            alertButton.setText("Cancel");
                            Toast.makeText( getActivity(),"Alert!", Toast.LENGTH_SHORT).show();
                            activity = "Voice";
                            makeSound ();
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        return view;
    }


    //Used for the shaking mechanism in the Main activity
    public void ClickFunction(){
        System.out.println("Click Function");
        alertButton.setText("Cancel");

//        click = !click;
//        if(click) {
//            SendTextMessage stm = new SendTextMessage();
//            stm.sendMessage(getActivity(), false);
//
//            alertButton.setText("Cancel");
//            makeSound_shake ();
//
//            //Toast.makeText( getActivity(),"Alert!", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            SendTextMessage stm = new SendTextMessage();
//            stm.sendMessage(getActivity(), true);
//        }

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
                        System.out.println("HomeFragment Click");
                        //get user location
                        Intent intent = new Intent(getContext(), MapsActivity.class);
                        intent.putExtra("Activity",  activity);
                        intent.putExtra("UserID",UserID);
                        //intent.putExtra("y",  etY.getText().toString());
                        startActivityForResult(intent,REQUEST_CODE_CURRENT_LOCATION);

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

    private void promptSpeechInput() {
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                getString(R.string.speech_prompt));
//        try {
//            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException a) {
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
//        }
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(getContext(), "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("onStart Home");

    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("onResume Home");

//        receive = getIntent();
//        click = receive.getBooleanExtra("Click",false);
        if(this.getArguments()!= null) {
            click = this.getArguments().getBoolean("Alert",false);
            System.out.println("receive something in Home");
            System.out.println("Click in resume: " + click);
            this.getArguments().remove("Alert");
        }

        if(click)
            alertButton.setText("Cancel");

    }

    @Override
    public void onPause() {
        super.onPause();

        System.out.println("onPause Home");

    }


    @Override
    public void onStop() {
        super.onStop();

        System.out.println("onStop Home");

    }

    private void showAuthenticationScreen() {
        // Create the Confirm Credentials screen. You can customize the title and description. Or
        // we will provide a generic one for you if you leave it null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mKeyguardManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);

            if (mKeyguardManager.isKeyguardSecure()) {
                Intent authIntent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
                startActivityForResult(authIntent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("resultCode: " + resultCode);
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            // Challenge completed, proceed with using cipher
            System.out.println("resultCode: " + requestCode);
            if (resultCode == RESULT_OK) {
                {   System.out.println("Click on authen in result: " + click);
                    if(click) {
                        click = !click;
                        alertButton.setText("Alert");
//                    send = new Intent(getActivity(),MainActivity.class);
//                    send.putExtra("Click",click);
//                    System.out.println("Click Home: " + click);
//                    startActivity(send);
                        MainActivity.updateAlert(click);
                    }
                }
            } else {
                // The user canceled or didn’t complete the lock screen
                // operation. Go to error/cancellation flow.
                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == REQUEST_CODE_CURRENT_LOCATION){
            if (resultCode == RESULT_OK) {
                System.out.println("HomeFragment");
                //Log.d(TAG, "getLastKnownLocation: called");
                double latitude = data.getDoubleExtra("latitude", 0.0);
                double longitude = data.getDoubleExtra("longitude", 0.0);
                System.out.println("Location HomeFragment: " + latitude + " " + longitude);
            }
        }



    }
}

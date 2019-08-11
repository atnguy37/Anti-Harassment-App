package com.asu.cse535.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceActivity extends AppCompatActivity {
    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private byte count;
    private long startMillis=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        System.out.println("Check OK");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        requestAudioPermissions();
//        lockScreenPermission();
        parentRelativeLayout = findViewById(R.id.parentRelativeLayout);

        // Tạo ra một đối tượng Intent cho một dịch vụ (PlaySongService).
//        Intent myIntent = new Intent(MainActivity.this, MyService.class);
//        // Gọi phương thức startService (Truyền vào đối tượng Intent)
//        this.startService(myIntent);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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
                    Toast.makeText(getApplicationContext(),"Result: " + result.get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        break;


                    case MotionEvent.ACTION_DOWN:
                        long time= System.currentTimeMillis();


                        //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
                        if (startMillis==0 || (time-startMillis> 3000) ) {
                            startMillis=time;
                            count=1;
                        }
                        //it is not the first, and it has been  less than 3 seconds since the first
                        else{ //  time-startMillis< 3000
                            count++;
                        }

                        if (count==3) {
                            //do whatever you need
                            speechRecognizer.startListening(speechRecognizerIntent);
                            count = 0;
                        }

                        break;
                }
                return false;
            }
        });
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
    }
    private void lockScreenPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }
    }

    protected void onResume() {
        super.onResume();
        startActivity(new Intent(VoiceActivity.this,MainActivity.class));
        System.out.println("onResume Voice called");
    }
}
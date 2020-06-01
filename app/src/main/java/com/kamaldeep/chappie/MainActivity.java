package com.kamaldeep.chappie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String IP_ADDRESS = "192.168.43.242";

    private TextToSpeech tts;

    private EditText txtSpeechInput;
    private ImageButton btnSpeak;
    private Button askButton;
    private TextView answerTextBox;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSpeechInput = findViewById(R.id.question_textbBox);
        btnSpeak =  findViewById(R.id.mic_button);
        askButton = findViewById(R.id.ask_button);
        answerTextBox = findViewById(R.id.answer_textView);

        tts = new TextToSpeech(this,this);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
    }


    private void promptSpeechInput()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }

    private void getData()
    {
        String query = "http://"+IP_ADDRESS +":5000/get_query?query=" + txtSpeechInput.getText();

        new AsyncTask<String, String, String>()
        {
            @Override
            protected void onPreExecute(){
                askButton.setEnabled(false);
            }

            @Override
            protected String doInBackground(String... partUrl)
            {
                HttpHandler sh = new HttpHandler();
                String url = partUrl[0];

                url.replace(" ","%20");
                Log.e(TAG,"newURL = " + url);

                String jsonStr = sh.makeServiceCall(url);
                Log.d(TAG, "Response from url: " + jsonStr);
                return jsonStr;
            }

            @Override
            protected void onPostExecute(String result)
            {
                askButton.setEnabled(true);
                answerTextBox.setText(result);
                speakOut();
                Log.e(TAG,"result = " + result);
            }
        }.execute( query );
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status)
    {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                btnSpeak.setEnabled(true);
                //speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut()
    {
        String text = answerTextBox.getText().toString();
        //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"");
    }
}
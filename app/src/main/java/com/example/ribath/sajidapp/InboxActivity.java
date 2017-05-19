package com.example.ribath.sajidapp;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class InboxActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    RecyclerView recyclerView;
    List<MessageData> messageDatas;
    RecyclerAdapter adapter;
    private TextToSpeech tts;
    int REQ_CODE_TEXT_INPUT = 100, REQ_CODE_SPEECH_INPUT = 200;
    int flag, subNo=0;
    boolean openFlag=false;
    String TAG = "InboxActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        messageDatas = new ArrayList<>();

        //// get message ////
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexDate = smsInboxCursor.getColumnIndex("date");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        do {
            String number = smsInboxCursor.getString(indexAddress);
            String message = smsInboxCursor.getString(indexBody);
            String date = smsInboxCursor.getString(indexDate);
            messageDatas.add(new MessageData(number, message, date));
        } while (smsInboxCursor.moveToNext());
        //// get message ////

        adapter = new RecyclerAdapter(messageDatas);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        flag = 1;
        startTextToSpeech();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            Log.i(TAG, "Try e dhukse");
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Log.i(TAG, "Catch e dhukse");
        }
    }

    private void startTextToSpeech() {
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, REQ_CODE_TEXT_INPUT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult e dhukse");
        if (requestCode == REQ_CODE_TEXT_INPUT)
        {
            Log.i(TAG, "onActivityResult when TEXT_INPUT");
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //tts_flag = 1;
                tts = new TextToSpeech(this, this);
            }
            else {
                Intent installVoice = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installVoice);
            }
        }
        if (requestCode == REQ_CODE_SPEECH_INPUT)
        {
            Log.i(TAG, "onActivityResult when SPEECH_INPUT");
            if (resultCode == RESULT_OK && null != data)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.i(TAG, "Speaker Output : " + result.get(0));
                if (result.get(0).equals("open"))
                {
                    openFlag = true;
                    tts = new TextToSpeech(this, this);
                }
                else if (result.get(0).equals("next"))
                {
                    openFlag = false;
                    subNo++;
                    if (subNo<messageDatas.size())
                    {
                        tts = new TextToSpeech(this, this);
                    }
                }
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setOnUtteranceCompletedListener(this);
            if (tts.isLanguageAvailable(Locale.ENGLISH) >= 0)
                tts.setLanguage(Locale.ENGLISH);
            //tts.setPitch(5.0f);
            tts.setSpeechRate(1.0f);

            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
            if (openFlag==false)
            {
                tts.speak(messageDatas.get(subNo).getNumber(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
            if (openFlag==true)
            {
                tts.speak(messageDatas.get(subNo).getMessage(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            }
        }
    }

    @Override
    public void onUtteranceCompleted(String s) {
        Log.i(TAG, "Welcome Speech Finished");
        if (flag==1)
        {
            flag++;
            Log.i(TAG, "1e dhukse");
            promptSpeechInput();
        } else if(openFlag==false)
        {
            promptSpeechInput();
        }

    }
}

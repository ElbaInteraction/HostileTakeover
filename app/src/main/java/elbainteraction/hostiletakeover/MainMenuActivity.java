package elbainteraction.hostiletakeover;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.os.Vibrator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainMenuActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {
    final static int VIBRATION_TIME = 50; //time for vibration in milliseconds.
    private static final int SPEECH_REQUEST_CODE = 0;
    private boolean voiceEnabled;
    private Vibrator vibrator;
    private TextToSpeech tts;
    private UtteranceProgressListener mProgressListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this,this);
        setContentView(R.layout.activity_main_menu);
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        mProgressListener =new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            } // Do nothing

            @Override
            public void onError(String utteranceId) {
            } // Do nothing.

            //Every time reading a string is finished, accept a new voice command from the user.
            @Override
            public void onDone(String utteranceId) {
                displaySpeechRecognizer();
            }
        };

    }

    @Override
    protected void onResume() {
        voiceEnabled = false;
        super.onResume();
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_always_voice),false)){
            say();
            voiceEnabled = true;
        }
    }

    @Override
    protected void onStop() {
        if(this.tts != null) {
            this.tts.stop();
        }
        super.onStop();
    }

    public void goToNewGame(View view){
        Intent intent = new Intent(view.getContext(), CreateGameActivity.class);
        intent.putExtra("voiceEnabled", voiceEnabled);
        vibrator.vibrate(VIBRATION_TIME);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    /**Start the intent to to go the continue menu*/
    public void goToContinueGame(View view){
        vibrator.vibrate(VIBRATION_TIME);
        startActivity(new Intent(view.getContext(), ContinueGameActivity.class));
        overridePendingTransition(0, 0);

    }

    /**Start the intent to to go the options menu*/
    public void goToOptions(View view){
        vibrator.vibrate(VIBRATION_TIME);
        startActivity(new Intent(view.getContext(), OptionsActivity.class));
        overridePendingTransition(0, 0);
    }

    /**Start the intent to to go the tutorial menu*/
    public void goToTutorial(View view){
        vibrator.vibrate(VIBRATION_TIME);
        startActivity(new Intent(view.getContext(), TutorialActivity.class));
        overridePendingTransition(0, 0);
    }

    /**If the enable voice button is pressed, start the vocie communication*/
    public void enableVoice(View view){
        vibrator.vibrate(VIBRATION_TIME);
        say();
    }

    /** Create an intent that can start the Speech Recognizer activity*/
    private void displaySpeechRecognizer() {
        voiceEnabled = true;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    /** This callback is invoked when the Speech Recognizer returns.
    * This is where you process the intent and extract the speech text from the intent.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if(spokenText.contains("continue")){
                goToContinueGame(findViewById(R.id.main_menu_continue_game));
            }
            else if(spokenText.contains("new")){
                goToNewGame(findViewById(R.id.main_menu_new_game));
            }
            else if(spokenText.contains("options")){
                goToOptions(findViewById(R.id.main_menu_options));
            }
            else if(spokenText.contains("tutorial")){
                goToTutorial(findViewById(R.id.main_menu_tutorial));
            }
            Toast.makeText(this.getBaseContext(),spokenText,Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onInit(int status) {
        this.tts.setLanguage(Locale.ENGLISH);
        this.tts.setSpeechRate(0.8f);
        this.tts.setOnUtteranceProgressListener(mProgressListener);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_always_voice),false)){
            say();
        }

    }

    private void say(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"1");
        tts.speak("Where do you want to navigate? Choices are New Game, Continue Game, Tutorial or Options.", TextToSpeech.QUEUE_FLUSH, map);

    }


}

package elbainteraction.hostiletakeover;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class CreateGameActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        this.tts = new TextToSpeech(this, this);
        this.tts.setLanguage(Locale.ENGLISH);


        this.tts.setSpeechRate(0.3f);
        say("Nu är vi igång och ska prata.");
        displaySpeechRecognizer();
    }

    @Override
    public void onInit(int status) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_game, container, false);
            return rootView;
        }
    }

    public void createGame(View view){
        Intent intent = new Intent(view.getContext(), MainMapActivity.class);
        intent.putExtra("gameType", "newGame");

        EditText editText = (EditText) findViewById(R.id.gameName);
        editText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        if(editText.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(view.getContext(),"The game must have a name!",Toast.LENGTH_LONG);
            toast.show();
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

            return;
        }

        intent.putExtra("gameName", editText.getText().toString());

        Spinner temp =  (Spinner) findViewById(R.id.numberOfTeamsSpinner);
        intent.putExtra("numberOfTeams", Integer.parseInt(temp.getSelectedItem().toString()));

        temp = (Spinner) findViewById(R.id.sizeOfMapSpinner);
        intent.putExtra("mapSize", Integer.parseInt(temp.getSelectedItem().toString()));

        temp = (Spinner) findViewById(R.id.gameDurationSpinner);
        intent.putExtra("gameTime", Integer.parseInt(temp.getSelectedItem().toString()));


        startActivity(intent);
    }
    private static final int SPEECH_REQUEST_CODE = 0;
    private int currentChoice = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
    // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);

    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            switch (currentChoice){
                case 0:
                    EditText gameName = (EditText) findViewById(R.id.gameName);
                    gameName.setText(spokenText);
                    say(spokenText);
                    displaySpeechRecognizer();
                    break;
                case 1:
                    Spinner numberOfTeams = (Spinner) findViewById(R.id.numberOfTeamsSpinner);
                    List<String> spinnerOptions = Arrays.asList(getResources().getStringArray(R.array.number_of_teams));
                    int tempIndex = spinnerOptions.indexOf(spokenText);
                    if(tempIndex > 0)
                        numberOfTeams.setSelection(tempIndex);
                    say(spokenText);
                    displaySpeechRecognizer();
                    break;
                case 2:
                    Spinner sizeOfMap = (Spinner) findViewById(R.id.sizeOfMapSpinner);
                    spinnerOptions = Arrays.asList(getResources().getStringArray(R.array.size_of_map));
                    tempIndex = spinnerOptions.indexOf(spokenText);
                    if(tempIndex > 0)
                        sizeOfMap.setSelection(tempIndex);
                    say(spokenText);
                    displaySpeechRecognizer();
                    break;
                case 3:
                    Spinner durationOfGame = (Spinner) findViewById(R.id.gameDurationSpinner);
                    spinnerOptions = Arrays.asList(getResources().getStringArray(R.array.time_of_game));
                    tempIndex = spinnerOptions.indexOf(spokenText);
                    if(tempIndex > 0)
                        durationOfGame.setSelection(tempIndex);
                    say(spokenText);
                    displaySpeechRecognizer();
                    break;
                case 4:
                   say("ARE THESE SETTINGS OK?");
                    displaySpeechRecognizer();
                    break;

            }
            currentChoice+=1;



        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void say(String say){
        tts.speak(say,TextToSpeech.QUEUE_FLUSH,null,say);
    }



}

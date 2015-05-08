package elbainteraction.hostiletakeover;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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


public class CreateGameActivity extends ActionBarActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    private Vibrator vibrator;
    final static int VIBRATION_TIME = 50; //time for vibration in milliseconds.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        if(getIntent().getBooleanExtra("voiceEnabled",false)){
            this.tts = new TextToSpeech(this, this);
        }
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onStop() {
        if(this.tts != null) {
            this.tts.stop();
        }
        super.onStop();
    }

    //Set up the reader to use a specific voice and speed.
    @Override
    public void onInit(int status) {
        this.tts.setLanguage(Locale.ENGLISH);
        this.tts.setSpeechRate(0.8f);
        this.tts.setOnUtteranceProgressListener(mProgressListener);
        say("Please specify the name of the game.");
    }

    private UtteranceProgressListener mProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        } // Do nothing

        @Override
        public void onError(String utteranceId) {
        } // Do nothing.

        //Every time reading a string is finished, accept a new voice command from the user.
        @Override
        public void onDone(String utteranceId) {
            if(currentChoice<5){
                displaySpeechRecognizer();
            }
        }
    };


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
        vibrator.vibrate(VIBRATION_TIME);

        Intent intent = new Intent(view.getContext(), MainMapActivity.class);
        intent.putExtra("gameType", "newGame");

        EditText editText = (EditText) findViewById(R.id.gameName);
        editText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        //If the game does not have a name, do not let the user continue to starting the game.
        if(editText.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(view.getContext(),"The game must have a name!",Toast.LENGTH_LONG);
            toast.show();
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            return;
        }
        //Get the value from the gameName field.
        intent.putExtra("gameName", editText.getText().toString());

        //Get the value from the numberOfTeams spinner
        Spinner temp =  (Spinner) findViewById(R.id.numberOfTeamsSpinner);
        intent.putExtra("numberOfTeams", Integer.parseInt(temp.getSelectedItem().toString()));

        //Get the value from the mapSize spinner
        temp = (Spinner) findViewById(R.id.sizeOfMapSpinner);
        intent.putExtra("mapSize", temp.getSelectedItem().toString());

        //Get the value from the duration spinner
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


    private final static String ERROR_UNDERSTANDING_INPUT = "Sorry i did not quite catch that. Could you please repeat?";
    private final static String TEAM_CHOICES = ". Please specify the number of teams. Choices are: 2, 3 or 4 teams. ";
    private final static String MAP_SIZE_CHOICES = ". Please specify the size of the map. Choices are: SMALL, MEDIUM or LARGE. ";
    private final static String DURATION_CHOICES = ". Please specify the duration of the game. Choices are: 2, 10 or 24 hours. ";
    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            StringBuilder sb = new StringBuilder();
            switch (currentChoice){
                case 0:
                    EditText gameName = (EditText) findViewById(R.id.gameName);
                    gameName.setText(spokenText);
                    //Build the string to say.
                    sb.append("The game name is: ");
                    sb.append(spokenText);
                    sb.append(TEAM_CHOICES);
                    //Say the string
                    say(sb.toString());
                    //Move to next parameter to set.
                    currentChoice+=1;
                    break;
                case 1:
                    //If the input is a correct value included in the array.
                    if(setSpinner(numberFilter(spokenText),R.array.number_of_teams,R.id.numberOfTeamsSpinner)){
                        //Build the string to say.
                        sb.append("The number of teams is: ");
                        sb.append(spokenText);
                        sb.append(MAP_SIZE_CHOICES);
                        //Say the string
                        say(sb.toString());
                        //Move to next parameter to set.
                        currentChoice+=1;
                        break;
                    }
                    sb.append(ERROR_UNDERSTANDING_INPUT);
                    sb.append(TEAM_CHOICES);
                    say(sb.toString());
                    break;
                case 2:
                    spokenText = spokenText.toUpperCase();
                    if(setSpinner(spokenText,R.array.size_of_map,R.id.sizeOfMapSpinner)){
                        //Build the string to say.
                        sb.append("The size of the map is: ");
                        sb.append(spokenText);
                        sb.append(DURATION_CHOICES);
                        //Say the string.
                        say(sb.toString());
                        //Move to next parameter to set.
                        currentChoice+=1;
                        break;
                    }
                    sb.append(ERROR_UNDERSTANDING_INPUT);
                    sb.append(MAP_SIZE_CHOICES);
                    say(sb.toString());
                    break;

                case 3:
                   if(setSpinner(numberFilter(spokenText),R.array.time_of_game,R.id.gameDurationSpinner)){
                       sb.append("The duration of the game is: " );
                       sb.append(spokenText);
                       sb.append(". The settings you have chosen are:");
                       //Add the name of the game to the speech string.
                       EditText textTemp =(EditText) findViewById(R.id.gameName);
                       sb.append("Name of game: ");
                       sb.append(textTemp.getText().toString());
                       //Add the number of teams to the speech string.
                       sb.append(". Number of teams: ");
                       Spinner temp =  (Spinner) findViewById(R.id.numberOfTeamsSpinner);
                       sb.append(temp.getSelectedItem().toString());
                       //Add the size of the map to the speech string.
                       sb.append(". Size of map: ");
                       temp =  (Spinner) findViewById(R.id.sizeOfMapSpinner);
                       sb.append(temp.getSelectedItem().toString());
                       //Add the duration of the game the to speech string.
                       sb.append(". Duration of game: ");
                       temp =  (Spinner) findViewById(R.id.gameDurationSpinner);
                       sb.append(temp.getSelectedItem().toString());

                       //Confirmation Question
                       sb.append(". Are these settings ok? Yes or No.");

                       //Start saying the specified parameters and questions.
                       say(sb.toString());

                       //Move to last parameter, start the game or restart the setup process.
                       currentChoice+=1;
                       break;
                   }
                    sb.append(ERROR_UNDERSTANDING_INPUT);
                    sb.append(DURATION_CHOICES);
                    say(sb.toString());
                    break;

                case 4:
                   //If the user says that the settings are ok, initiate the game.
                   if(spokenText.equals("yes")){
                       say("Enjoy the Game.");
                       createGame(findViewById(R.id.create_game_button));
                       currentChoice+=1;

                   }
                   //If the user does not say yes, restart the process.
                    else{
                       currentChoice= 0;
                       say("Please retry the process. Specify the name of the game again.");

                   }
                   break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

   /* //Method for setting a spinner to the value specified by use through voice recognition.
   If the spinner contains the value, otherwise the default value will be used.*/
    private boolean setSpinner(String spokenText, int arrayID, int spinnerID){
        List<String> spinnerOptions = Arrays.asList(getResources().getStringArray(arrayID));

        Spinner spinnerToSet = (Spinner) findViewById(spinnerID);
        if(spinnerOptions.indexOf(spokenText) >= 0){
            spinnerToSet.setSelection(spinnerOptions.indexOf(spokenText));
            Toast.makeText(getBaseContext(),spokenText,Toast.LENGTH_LONG).show();
            return true;
        }
        return false;



    }

    /*Method for saying (Voice synthesizing)the string specified.*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void say(String say){
        tts.speak(say,TextToSpeech.QUEUE_FLUSH,null,say);
    }
    //Method for filtering some of the common misheard numbers.
    private String numberFilter(String spokenText){
        switch (spokenText){
            case"too":
            case"two":
            case"to":
            case"tou":
            case"tow":
                return "2";
            case"for":
            case"four":
                return "4";
            case"tree":
            case"three":
            case"tre":
                return "3";
            case"ten":
            case"tent":
            case"tens":
                return "10";
            default:
                return spokenText;
        }

    }
}

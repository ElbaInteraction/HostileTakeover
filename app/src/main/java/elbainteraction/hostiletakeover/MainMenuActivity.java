package elbainteraction.hostiletakeover;

import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;


public class MainMenuActivity extends ActionBarActivity {
    private boolean voiceOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


    }
    public void goToNewGame(View view){
        startActivity(new Intent(view.getContext(), CreateGameActivity.class));
        overridePendingTransition(0, 0);

    }
    public void goToContinueGame(View view){
        startActivity(new Intent(view.getContext(), ContinueGameActivity.class));
        overridePendingTransition(0, 0);

    }
    public void goToOptions(View view){
        startActivity(new Intent(view.getContext(), OptionsActivity.class));
        overridePendingTransition(0, 0);
    }
    public void goToTutorial(View view){
        startActivity(new Intent(view.getContext(), TutorialActivity.class));
        overridePendingTransition(0, 0);
    }
    public void enableVoice(View view){
        displaySpeechRecognizer();
    }


    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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
            if(spokenText.equals("new game") || spokenText.equals("new games") ||spokenText.equals("new") ){
                goToNewGame(findViewById(R.id.main_menu_new_game));
            }
            else if(spokenText.equals("continue game") || spokenText.equals("continue") || spokenText.equals("continue games")){
                goToContinueGame(findViewById(R.id.main_menu_new_game));
            }
            Toast.makeText(this.getBaseContext(),spokenText,Toast.LENGTH_LONG).show();
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}

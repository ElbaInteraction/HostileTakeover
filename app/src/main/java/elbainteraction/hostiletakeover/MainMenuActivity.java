package elbainteraction.hostiletakeover;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;


public class MainMenuActivity extends ActionBarActivity {
    private boolean voiceEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    @Override
    protected void onResume() {
        voiceEnabled = false;
        super.onResume();
    }

    public void goToNewGame(View view){
        Intent intent = new Intent(view.getContext(), CreateGameActivity.class);
        intent.putExtra("voiceEnabled", voiceEnabled);
        startActivity(intent);
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
        voiceEnabled = true;
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



}

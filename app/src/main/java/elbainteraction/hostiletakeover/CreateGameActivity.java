package elbainteraction.hostiletakeover;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;


public class CreateGameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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
            View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
            return rootView;
        }


    }

    public void createGame(View view){
        Intent intent = new Intent(view.getContext(), MainMapActivity.class);
        intent.putExtra("gameType", "newGame");
        EditText editText = (EditText) findViewById(R.id.gameName);
        intent.putExtra("gameName", editText.getText().toString());

        Spinner temp =  (Spinner) findViewById(R.id.numberOfTeams);
        intent.putExtra("numberOfTeams", Integer.parseInt(temp.getSelectedItem().toString()));

        temp = (Spinner) findViewById(R.id.mapSize);
        intent.putExtra("mapSize", Integer.parseInt(temp.getSelectedItem().toString()));

        temp = (Spinner) findViewById(R.id.gameTime);
        intent.putExtra("gameTime", Integer.parseInt(temp.getSelectedItem().toString()));

        startActivity(intent);


    }


}

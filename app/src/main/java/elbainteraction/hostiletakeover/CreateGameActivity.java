package elbainteraction.hostiletakeover;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class CreateGameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
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

        Spinner temp =  (Spinner) findViewById(R.id.numberOfTeams);
        intent.putExtra("numberOfTeams", Integer.parseInt(temp.getSelectedItem().toString()));

        temp = (Spinner) findViewById(R.id.mapSize);
        intent.putExtra("mapSize", Integer.parseInt(temp.getSelectedItem().toString()));

        temp = (Spinner) findViewById(R.id.gameTime);
        intent.putExtra("gameTime", Integer.parseInt(temp.getSelectedItem().toString()));


        startActivity(intent);
    }


}

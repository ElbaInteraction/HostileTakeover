package elbainteraction.hostiletakeover;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class ContinueGameActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    //JSON handler
    private JSONhandler jsonHandler;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_game);
        listView = (ListView) findViewById(R.id.listOfActiveGames);
        jsonHandler = new JSONhandler(listView, this);
        jsonHandler.listActiveGames();



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String gameName = listView.getItemAtPosition(position).toString().trim();
                jsonHandler.getGame(gameName);


            }});
    }



    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return null;
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    public void startGame(ArrayList<String> gameParameters){
        Intent intent = new Intent(this, MainMapActivity.class);
        intent.putExtra("gameType", "continueGame");
        intent.putExtra("gameName", gameParameters.get(0));
        intent.putExtra("startLatitude", Double.parseDouble(gameParameters.get(1)));
        intent.putExtra("startLongitude", Double.parseDouble(gameParameters.get(2)));
        intent.putExtra("endTime", gameParameters.get(3));
        intent.putExtra("rowCount", Integer.parseInt(gameParameters.get(4)));
        Spinner temp =  (Spinner) findViewById(R.id.teamColorSpinner);
        intent.putExtra("teamColor",(temp.getSelectedItem().toString()));
        String debug = Integer.parseInt(gameParameters.get(4)) +"";
        Log.d("Rowcount debug: ", debug);
        startActivity(intent);
    }
}

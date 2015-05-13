package elbainteraction.hostiletakeover;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Filip on 2015-05-12.
 */
public class PushTiles extends AsyncTask<String,Void,String> {

    private int gameRow;
    private int gameColumn;
    private Team team;

    public PushTiles(int gameRow, int gameColumn, Team team){
        this.team = team;
        this.gameRow = gameRow;
        this.gameColumn = gameColumn;
    }

    // result JSONArray
    private JSONArray jsonArray = null;

    @Override
    protected String doInBackground(String... params) {
// Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        //PHP script
        String url = "http://elba.netai.net/putTiles.php";

        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("gameRow", gameRow+""));
        nameValuePair.add(new BasicNameValuePair("gameColumn", gameColumn+""));
        nameValuePair.add(new BasicNameValuePair("teamName", team.getTeamName()));

        Log.d("llllllll", nameValuePair.toString());
        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST, nameValuePair);
        Log.d("llllllll", jsonStr);

        return null;

    }


}

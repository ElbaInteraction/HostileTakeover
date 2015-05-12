package elbainteraction.hostiletakeover;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Filip on 2015-05-12.
 */
public class ZoneHandler extends AsyncTask<String,Void,String> {

    private static final long sleepTime = 20000;
    private ArrayList<String> resultList;
    private GameInstance gameInstance;

    public ZoneHandler(GameInstance gameInstance){
        resultList = new ArrayList<>();
        this.gameInstance = gameInstance;
    }

    // result JSONArray
    private JSONArray jsonArray = null;

    public ArrayList<String> getResultList() {
        return resultList;
    }

    @Override
    protected String doInBackground(String... params) {
        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        //PHP script
        String url = "http://elba.netai.net/getTiles.php";

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

        Log.d("Response: ", "> " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                jsonArray = jsonObj.getJSONArray("Tiles");

                // looping through All results
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);


                    resultList.add(c.getString("gameRow"));
                    resultList.add(c.getString("gameColumn"));
                    resultList.add(c.getString("teamName"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String o) {
        gameInstance.updateTiles(resultList);
        super.onPostExecute(o);
    }


}

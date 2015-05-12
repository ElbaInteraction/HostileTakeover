package elbainteraction.hostiletakeover;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Filip on 2015-05-12.
 */
public class ZoneHandler implements Runnable {

    private static final long sleepTime = 20000;
    private ArrayList<String> resultList;

    // result JSONArray
    private JSONArray jsonArray = null;

    public ArrayList<String> getResultList() {
        return resultList;
    }

    @Override
    public void run() {

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
                resultList = new ArrayList<>();

                // looping through All results
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    resultList.add(c.getString("teamName"));
                    resultList.add(c.getString("row"));
                    resultList.add(c.getString("column"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

        notify();
    }
}

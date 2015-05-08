package elbainteraction.hostiletakeover;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;

/**
 * Created by Alexander on 2015-04-27.
 */
public class JSONhandler {

    private ListView listView;
    private Context context;
    private JsonReadTask task;
    private String url;
    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_TABLE_TEAMS = "Teams";
    private static final String TAG_TEAMNAME = "teamName";
    private static final String TAG_TEAMCOLOR = "teamColor";



    // result JSONArray
    private JSONArray jsonArray = null;

    // result array
    private ArrayList resultList;


    public JSONhandler(ListView listView, Context context) {
        this.listView = listView;
        this.context = context;


        task = new JsonReadTask();
        // passes values for the urls string array

    }



    public void getTeams() {

        url = "http://elba.netai.net/getTeams.php";

        task.execute(new String[]{url});
    }

    public void getGame(String gameName){

        url = "http://elba.netai.net/getGame.php";

        task.execute(new String[]{url, "getGame", gameName});

    }

    public void listActiveGames() {

        url = "http://elba.netai.net/getGames.php";

        task.execute(new String[]{url, "listActiveGames"});
    }

    private class JsonReadTask extends AsyncTask<String, Void, String> {

        private String whatTodo;


        @Override
        protected String doInBackground(String... params) {

            this.whatTodo = params[1];
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

        String whatTodo = params[1];
        if(whatTodo.equals("getGame")) {
            getGame(sh, params[1]);
        } else if(whatTodo.equals("listActiveGames")){
            getGames(sh);
        }

            return null;
        }

        public void getTeams(ServiceHandler sh) {

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    jsonArray = jsonObj.getJSONArray(TAG_TABLE_TEAMS);
                    resultList = new ArrayList<String>();

                    // looping through All results
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        String teamName = c.getString(TAG_TEAMNAME);
                        String teamColor = c.getString(TAG_TEAMCOLOR);


                        resultList.add(teamName);
                        resultList.add(teamColor);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }

        public void getGames(ServiceHandler sh) {

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    jsonArray = jsonObj.getJSONArray("Games");
                    resultList = new ArrayList<>();

                    // looping through All results
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        resultList.add(c.getString("gameName"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }

        public void getGame(ServiceHandler sh, String gameName) {

            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("gameName", gameName));

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST, nameValuePair);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    jsonArray = jsonObj.getJSONArray("Games");
                    resultList = new ArrayList<>();

                    // looping through All results
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        resultList.add(c.getString("gameName"));
                        resultList.add(c.getString("startLatitude"));
                        resultList.add(c.getString("startLongitude"));
                        resultList.add(c.getString("endTime"));
                        resultList.add(c.getString("rowCount"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(String result) {


            if (pDialog.isShowing()) {
                pDialog.dismiss();
                /**
                 * Updating parsed JSON data into ListView
                 * */


            if(whatTodo.equals("listActiveGames")) {
                ArrayAdapter<ArrayList<String>> arrayAdapter = new ArrayAdapter<ArrayList<String>>(
                        context,
                        android.R.layout.simple_list_item_1,
                        resultList);

                listView.setAdapter(arrayAdapter);
            } else if(whatTodo.equals("getGame")){





            }

            }

        }


    }
    // end async task
}


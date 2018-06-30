package it.justdevelop.craftedbeer.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.justdevelop.craftedbeer.R;

public class BackendHelper {
    private static final String LOG = "BackendHelper :";
    private static final String APP_LOG_TAG = "CrafterBeer: ";


    public static class fetch_beers extends AsyncTask<Object, String, String>{
        Context context;
        @Override
        protected void onPostExecute(final String str) {
            Log.i(APP_LOG_TAG, LOG+"Fetching beers from web api complete");
            super.onPostExecute(str);
            if(str != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SQLiteDatabaseHelper myDBHelper = new SQLiteDatabaseHelper(context);
                            JSONArray beers_list = new JSONArray(str);
                            Log.i(APP_LOG_TAG, LOG+"total beers "+beers_list.length());

                            for(int i=0; i<beers_list.length(); i++){
                                JSONObject beer_object = beers_list.getJSONObject(i);
                                myDBHelper.insertIntoBeers(
                                        beer_object.getInt("id"),
                                        beer_object.getString("name"),
                                        beer_object.getString("style"),
                                        beer_object.optDouble("abv"),
                                        beer_object.optInt("ibu"),
                                        beer_object.optDouble("ounces")
                                        );
                            }
                            myDBHelper.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }else {
                Log.i(APP_LOG_TAG, LOG+"Got an empty response from web api");
            }

        }

        @Override
        protected String doInBackground(Object... objects) {
            context = (Context) objects[0];
            String web_api = (String) objects[1];

            try{
                URL url = new URL(web_api);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream stream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            }catch (Exception e){
                Log.i(APP_LOG_TAG, LOG+"error fetching from web : "+ e);
            }
            return null;
        }
    }
}

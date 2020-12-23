package com.example.jobscheduler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.masterandroid.backgroundservice.adapter.VisitedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VisitedPlaces extends AppCompatActivity {

    RecyclerView visitedRecycler;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<place> historyList;
    Button refresh_places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_places);
        visitedRecycler=findViewById(R.id.visitedRecycler);
        visitedRecycler.setLayoutManager(new LinearLayoutManager(this));
        historyList=new ArrayList<>();
        refresh_places=findViewById(R.id.get_places);
        refresh_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readHistory("GuzFS0EjtBSwuRXBuRfhFN8ZSfm1");
            }
        });
    }
    private void readHistory(String userId) {
        PerformNetworkRequest request = new PerformNetworkRequest(com.masterandroid.backgroundservice.Api.URL_READ_YES_LIST+userId, null, CODE_GET_REQUEST);
        request.execute();
    }
    private void refreshHistoryList(JSONArray heroes) throws JSONException {
        //clearing previous heroes
        historyList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < heroes.length(); i++) {
            //getting each hero object
            JSONObject obj = heroes.getJSONObject(i);

            //adding the hero to the list
            String type= obj.getString("placeType");
            String str[] = type.split(",");
            ArrayList<String> placeType = new ArrayList<String>(Arrays.asList(str));
            place p=new place(
                    obj.getInt("placeId"),
                    obj.getString("userId"),
                    obj.getDouble("placeLongitude"),
                    obj.getDouble("placeLatitude"),
                    obj.getString("placeAddress"),
                    obj.getString("placeName"),
                    placeType,
                    obj.getString("visitStatus"),
                    obj.getString("placeTime")
            );
            Log.d("place",p.toString());
            //creating the adapter and setting it to the recyclerview
            historyList.add(p);

        }
        Log.d("List Size ",Integer.toString(historyList.size()));
        VisitedAdapter adapter = new VisitedAdapter(historyList, com.masterandroid.backgroundservice.VisitedPlaces.this);
        visitedRecycler.setAdapter(adapter);

    }
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;
        //the parameters
        HashMap<String, String> params;
        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    //refreshing the herolist after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
                    refreshHistoryList(object.getJSONArray("lists"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            com.masterandroid.backgroundservice.RequestHandler requestHandler = new com.masterandroid.backgroundservice.RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}
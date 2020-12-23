package com.example.jobscheduler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.masterandroid.backgroundservice.adapter.nearbyAdapter;
import com.masterandroid.backgroundservice.nearbyResponse.Example;
import com.masterandroid.backgroundservice.retrofit.ApiClient;
import com.masterandroid.backgroundservice.retrofit.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class showPlaceNearby extends AppCompatActivity {
    RecyclerView nearbyRecycler;
    Button done_history;
    List<com.masterandroid.backgroundservice.nearbyPlace> nearbyPlaceList;
    ApiClient retrofit;
    String place_addr;
    int placeId;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place_nearby);
        nearbyPlaceList=new ArrayList<>();
        nearbyRecycler=findViewById(R.id.nearbyRecycler);
        done_history=findViewById(R.id.done_history);
        nearbyRecycler.setLayoutManager(new LinearLayoutManager(this));
        Intent getData=getIntent();

        Double place_latitude = getData.getDoubleExtra("placeLat",0.0);
        Double place_longitude = getData.getDoubleExtra("placeLon",0.0);
        place_addr= getData.getStringExtra("placeAddr");
        placeId=getData.getIntExtra("placeId",0);

        String mainLatlng=place_longitude.toString()+","+place_latitude.toString();
        Toast.makeText(this, mainLatlng, Toast.LENGTH_SHORT).show();
        getDetailsFromAPI(mainLatlng,"AIzaSyDazjxsJFdohTwZllHdMsacB4P9luVjqyE");

        done_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(com.masterandroid.backgroundservice.showPlaceNearby.this, Integer.toString(placeId), Toast.LENGTH_SHORT).show();
                PerformNetworkRequest request = new PerformNetworkRequest(com.masterandroid.backgroundservice.Api.URL_DELETE_LIST + placeId, null, CODE_GET_REQUEST);
                request.execute();
                Intent goBack=new Intent(com.masterandroid.backgroundservice.showPlaceNearby.this,ViewHistoryRecycler.class);
                finish();
                startActivity(goBack);
            }
        });
    }

    public void getDetailsFromAPI(String location, final String api_key){
        final ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<Example> call= apiInterface.getDetails(location,20,api_key);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.isSuccessful()){
                    try
                    {
                        for(int i=0;i<response.body().getResults().size();i++)
                        {
                            String placeName=response.body().getResults().get(i).getName();
                            Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                            Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                            List<String> placeType=response.body().getResults().get(i).getTypes();
                            com.masterandroid.backgroundservice.nearbyPlace nearby=new com.masterandroid.backgroundservice.nearbyPlace(placeName,lat,lng,placeType,place_addr);
                            nearbyPlaceList.add(nearby);
                            nearbyAdapter adapter=new nearbyAdapter(nearbyPlaceList, com.masterandroid.backgroundservice.showPlaceNearby.this);
                            nearbyRecycler.setAdapter(adapter);
                        }

                    }
                    catch (Exception er)
                    {
                        Log.d("showPLace err ",er.getMessage());

                    }
                    Log.d("showPLace success",response.body().toString());

                }
                else
                    {
                        Log.d("Else Response: " , response.message());

                    }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

            }
        });
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
                    Toast.makeText(com.masterandroid.backgroundservice.showPlaceNearby.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    //refreshing the herolist after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
                    // refreshHistoryList(object.getJSONArray("lists"));
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
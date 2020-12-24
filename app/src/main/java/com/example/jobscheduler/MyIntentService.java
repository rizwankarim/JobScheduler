package com.example.jobscheduler;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyIntentService extends JobService {
    private static final String TAG = MyIntentService.class.getSimpleName();

    private int mRandomNumber;
    private boolean mIsRandomGeneratorOn;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private final int MIN=0;
    private final int MAX=100;
    place details;


    private place getGeocodingDetails(double latitude, double longitude)
    {
        Geocoder geocoder;
        place completeDetails = null;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            Toast.makeText(this, "loc getting", Toast.LENGTH_SHORT).show();
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String time = formatter.format(date).toString();

            completeDetails = new place("GuzFS0EjtBSwuRXBuRfhFN8ZSfm1", latitude, longitude, address, "pending", time);
            Log.d("LOCATION_DETAILS", completeDetails.toString());
            Toast.makeText(this, "Got loc", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return completeDetails;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG,"onStartJob");
        doBackgroundWork();
        return true;
    }

    private void doBackgroundWork(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mIsRandomGeneratorOn =true;
                startRandomNumberGenerator();
            }
        }).start();
    }

    /**
     * This method gets called when job gets cancelled.
     * Return True if you want to restart the job automatically when a condition is met (WIFI - ON)
     * Return false if you want don't want to restart the job automatically even when the condition is met (WIFI - OFF)
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG,"onStopJob");
        return true;
    }

    private void startRandomNumberGenerator(){
        while (mIsRandomGeneratorOn){
            try{
                //Toast.makeText(this, "Got loc", Toast.LENGTH_SHORT).show();
                if(mIsRandomGeneratorOn){
                    // mRandomNumber =new Random().nextInt(MAX)+MIN;
                    // Log.i(getString(R.string.service_demo_tag),"Thread id: "+Thread.currentThread().getId()+", Random Number: "+ mRandomNumber);
                    getCurrentLocation();
                }
                Thread.sleep(60000);

            }catch (InterruptedException e){
                Log.i(TAG,"Thread Interrupted");
            }
        }
    }
    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(MyIntentService.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MyIntentService.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int locationindex = locationResult.getLocations().size() - 1;
                            double current_lat=locationResult.getLocations().get(locationindex).getLatitude();
                            double current_long=locationResult.getLocations().get(locationindex).getLongitude();
                            //double current_lat = 24.863473;
                            //double current_long = 67.072290;
                            Log.d("Location", String.valueOf(current_lat) + "," + String.valueOf(current_long));
                            details= getGeocodingDetails(current_lat,current_long);

                            //Log.d("Location", details.getPlaceLatitude()+","+details.getPlaceLongitude());
                            storeData(details.getUserId(),details.getPlaceName(),details.getPlaceAddress(),"typelist"
                            ,details.getPlaceLatitude(),details.getPlaceLongitude(),details.getStatus(),details.getPlaceTime());
                        }
                    }
                }, Looper.getMainLooper());
    }

    //Comment

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsRandomGeneratorOn=false;
        Log.i(TAG,"StopService"+ ", thread Id: "+ Thread.currentThread().getId());
    }

    public void storeData(String UserId, String name, String address,String type,double latitude, double longitude, String VisitStatus,String placeTime)
    {

        HashMap<String, String> params = new HashMap<>();

        params.put("userId",UserId);
        params.put("placeLatitude",String.valueOf(latitude));
        params.put("placeLongitude",String.valueOf(longitude));
        params.put("placeAddress",address);
        params.put("placeName","name");
        params.put("placeType","type");
        params.put("visitStatus",VisitStatus);
        params.put("placeTime",placeTime);

        Toast.makeText(this, "Inside storeData", Toast.LENGTH_SHORT).show();
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_LIST, params, CODE_POST_REQUEST);
        request.execute();
        Toast.makeText(this, "Inside storeData Executed", Toast.LENGTH_SHORT).show();

    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;
        // comment
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
                    //refreshList(object.getJSONArray("myLists"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}

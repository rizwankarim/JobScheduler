package com.example.jobscheduler;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyIntentService extends JobService {
    private static final String TAG = MyIntentService.class.getSimpleName();

    private int mRandomNumber;
    private boolean mIsRandomGeneratorOn;

    private final int MIN=0;
    private final int MAX=100;
    place details;
    /**
     * Return FALSE when this jpb is of short duration
     * and needs to be executed for very small time.
     * By default everything here runs on UI thread. If you don't
     * want to block the UI thread with long running work, then use thread.
     * Return TRUE whenever you are running long running tasks. So when you are
     * using a thread to do long running task, return true.
     * @param jobParameters
     * @return
     */

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            try
            {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    double longitude =  locationResult.getLastLocation().getLongitude();
                    double latitude = locationResult.getLastLocation().getLatitude();
                    Log.d ("LOCATION_UPDATE",latitude+","+longitude);
                    details= getGeocodingDetails(longitude,latitude);
                    // getDetailsFromAPI(latitude+","+longitude,"AIzaSyDazjxsJFdohTwZllHdMsacB4P9luVjqyE");
                    if(details.getPlaceAddress()!=null)
                    {
                        Toast.makeText(MyIntentService.this, "Address found", Toast.LENGTH_SHORT).show();
                        //storeDataInDatabase(details,latitude,longitude);

                    }else
                    {
                        Toast.makeText(MyIntentService.this, "Address not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception er)
            {
                Log.d("onLocationResult",er.getMessage());
            }

        }
    };

    private place getGeocodingDetails(double longitude, double latitude)
    {
        Geocoder geocoder;
        place completeDetails = null;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
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
        return false;
    }

    private void startRandomNumberGenerator(){
        while (mIsRandomGeneratorOn){
            try{
                Thread.sleep(1000);
                if(mIsRandomGeneratorOn){
                   // mRandomNumber =new Random().nextInt(MAX)+MIN;
                   // Log.i(getString(R.string.service_demo_tag),"Thread id: "+Thread.currentThread().getId()+", Random Number: "+ mRandomNumber);
                    getCurrentLocation();

                }
            }catch (InterruptedException e){
                Log.i(TAG,"Thread Interrupted");
            }
        }
    }
    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
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
                            Log.d("Location", details.getPlaceLatitude()+","+details.getPlaceLongitude());
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
}

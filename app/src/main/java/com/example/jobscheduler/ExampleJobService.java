package com.example.jobscheduler;

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.CountDownTimer;
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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExampleJobService extends JobService {

    private static final String TAG="ExampleJobService";
    private boolean jobCancelled=false;
    static CountDownTimer countDownTimer = null;
    place details;

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
                        Toast.makeText(ExampleJobService.this, "Address found", Toast.LENGTH_SHORT).show();
                        //storeDataInDatabase(details,latitude,longitude);

                    }else
                    {
                        Toast.makeText(ExampleJobService.this, "Address not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception er)
            {
                Log.d("onLocationResult",er.getMessage());
            }

        }
    };

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Service has been started..");
        doBackgroundWork(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"Service has been cancelled..");
        jobCancelled=true;
        stopLocation();
        return true;
    }

    private void doBackgroundWork(JobParameters params) {

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                //Timer();
                for (int i = 0; i <3 ; i++) {
                    try {
                        Log.d(TAG, Integer.toString(i));
                        LocationRequest locationRequest = new LocationRequest();

                        // Try adjusting the location.setFastestInterval
                        /*
                        50*100 = 5,000 = 5 Seconds
                        100*100 = 10,000 = 10 Seconds
                        100*400 = 40,000 = 40 Seconds
                        100*600 = 60,000 = 60 Seconds
                        100*900 = 90,000 = 90 Seconds

                         */
                        locationRequest.setInterval(5000);
                        locationRequest.setFastestInterval(7000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        if (ActivityCompat.checkSelfPermission(ExampleJobService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ExampleJobService.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        LocationServices.getFusedLocationProviderClient(ExampleJobService.this)
                                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

                        if(jobCancelled){
                            return;
                        }
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//               for(int i=0; i  < 10;  i++){
//                    try{
//                        Log.d(TAG,"Service Running...");
//                        //geocding latlong latlng1
//                        //latlng
//                        //haversine method(latlng1 latlng2)
//                        //if(distance>5){
//                        // false}
//                        //else(count++){
//                        // if count 5 {
//                        // google wala
//                        // database wala}}
//
//                        //count
//                        if(jobCancelled){
//                            return;
//                        }
//                        Thread.sleep(5000);
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
                Log.d(TAG,"Job Finished");
                jobFinished(params,false);

            }
        }).start();
    }

    public void Timer(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // 60*30*1000 = 30 min
        // 60*10*1000 = 10 min
        // 50000 = 50 seconds
        // 10000 = 10 seconds
        // 60000 = 60 seconds

        // Try Increasing countDownInterval
        countDownTimer = new CountDownTimer( 60*10*1000 , 1000) {
            public void onTick(long millisUntilFinished)
            {
                String left=Long.toString(millisUntilFinished);
                Log.d("Service Time Interval ",left);

                LocationRequest locationRequest = new LocationRequest();

                // Try adjusting the location.setFastestInterval
                /*
                50*100 = 5,000 = 5 Seconds
                100*100 = 10,000 = 10 Seconds
                100*400 = 40,000 = 40 Seconds
                100*600 = 60,000 = 60 Seconds
                100*900 = 90,000 = 90 Seconds

                 */
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(100*600);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                if (ActivityCompat.checkSelfPermission(ExampleJobService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ExampleJobService.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.getFusedLocationProviderClient(ExampleJobService.this)
                        .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
            public void onFinish()
            {
                Toast.makeText(ExampleJobService.this, "Service Has Ended..", Toast.LENGTH_SHORT).show();
                Log.d("done!", "done!");
                stopLocation();
            }
        };
        countDownTimer.start();
    }

    public void stopLocation(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
        //countDownTimer.cancel();
        //dfes

    }

    private place getGeocodingDetails(double Longitude, double Latitude) {
        Geocoder geocoder;
        place completeDetails = null;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String time = formatter.format(date).toString();

            completeDetails = new place("GuzFS0EjtBSwuRXBuRfhFN8ZSfm1", Latitude, Longitude, address, "pending", time);
            Log.d("LOCATION_DETAILS", completeDetails.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return completeDetails;
    }
    public double haversineMethod(Double lat1, Double lon1,
                                  Double lat2, Double lon2)
    {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians comment
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));

        return rad * c;
    }
}

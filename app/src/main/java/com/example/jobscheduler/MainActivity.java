package com.example.jobscheduler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    Geocoder geocoder;
    place completeDetails=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void StartJob(View v){


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void StopJob(View v){

    }

    private place getGeocodingDetails(double Longitude, double Latitude){
        List<Address> addresses= new ArrayList<>();
        geocoder=new Geocoder(this, Locale.getDefault());

        try {
            addresses= geocoder.getFromLocation(Latitude,Longitude,1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String time=formatter.format(date).toString();

            completeDetails= new place("GuzFS0EjtBSwuRXBuRfhFN8ZSfm1",Latitude,Longitude,address,"pending",time);
            Log.d("LOCATION_DETAILS",completeDetails.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return completeDetails;
    }
}
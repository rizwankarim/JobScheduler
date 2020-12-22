package com.example.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExampleJobService extends JobService {

    private static final String TAG="ExampleJobService";
    private boolean jobCancelled=false;

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
        return true;
    }

    private void doBackgroundWork(JobParameters params) {

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                for(int i=0; i  < 10;  i++)
                {
                    try{
                        Log.d(TAG,"Count="+i);
                        //geocding latlong latlng1
                        //latlng
                        Double lat1=0.2;
                        Double lon1=0.1;

                        Double lat2=0.2;
                        Double lon2=0.1;

                       // Double distance=haversineMethod(lat1,lon1,lat2,lon2);

                        //haversine method(latlng1 latlng2)

                        //if(distance>5){
                        // false
                        // }
                        //else
                        // {
                        // count++
                        // if count 5 {
                        // google wala
                        // database wala
                        // }
                        // }

                        //count

                        if(jobCancelled){
                            return;
                        }
                        Thread.sleep(5000);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                Log.d(TAG,"Job Finished");
                jobFinished(params,false);

            }
        }).start();
    }
    public double haversineMethod(Double lat1, Double lon1,
                                  Double lat2, Double lon2)
    {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
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

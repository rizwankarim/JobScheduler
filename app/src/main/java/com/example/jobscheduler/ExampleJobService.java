package com.example.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


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
                for(int i=0; i  < 10;  i++){
                    try{
                        Log.d(TAG,"Count="+i);
                        //geocding latlong latlng1
                        //latlng
                        //haversine method(latlng1 latlng2)
                        //if(distance>5){
                        // false}
                        //else(count++){
                        // if count 5 {
                        // google wala
                        // database wala}}

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
}

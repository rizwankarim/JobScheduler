package com.example.jobscheduler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button buttonStart, buttonStop;
    private TextView textViewthreadCount;
    int count = 0;

    private MyIntentService myService;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;

    JobScheduler jobScheduler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        }

        Log.i(TAG, "MainActivity thread id: " + Thread.currentThread().getId());

        buttonStart = (Button) findViewById(R.id.buttonThreadStarter);
        buttonStop = (Button) findViewById(R.id.buttonStopthread);

        textViewthreadCount = (TextView) findViewById(R.id.textViewthreadCount);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void StartJob(){
        ComponentName componentName = new ComponentName(this, MyIntentService.class);
        JobInfo jobInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobInfo = new JobInfo.Builder(101,componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    //.setRequiresCharging(false)
                    .setPeriodic(15*60*1000)
                    .setPersisted(true)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(jobScheduler.schedule(jobInfo)==JobScheduler.RESULT_SUCCESS){
                Log.i(TAG, "MainActivity thread id: " + Thread.currentThread().getId()+", job successfully scheduled");
            }else {
                Log.i(TAG, "MainActivity thread id: " + Thread.currentThread().getId()+", job could not be scheduled");
            }
        }

    }

    public void StopJob(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler.cancel(101);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonThreadStarter:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    StartJob();
                }
                break;
            case R.id.buttonStopthread:
                StopJob();
                break;
        }
    }
}
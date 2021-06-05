package com.intellis.pushnotifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String serv="https://endophytic-crew.000webhostapp.com";
    MaterialTextView tv;


    long prev;
AppCompatActivity ac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.txt);
        ac=this;

    //    new createtask().execute();
        dothis();



    }


    public class createtask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            dothis();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }



    void dothis()
    {
        Handler hh=new Handler();

        Runnable r=new Runnable() {
            @Override
            public void run() {
                task();
                hh.postDelayed(this,2500);

            }
        };

        hh.postDelayed(r,400);
    }



     void task() {

         try {
             String t=stringaturl(serv+"/notify.txt");
             if((t.equals("")|| t.equals("null")))
             {
                 String s="No Notifications yet....\nWaiting for Notification....";
                // createNotification("Notification",s,ac);

                 ac.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         tv.setText(s);
                     }
                 });
             }
             else
             {
                 String a[]=t.split(",");
                 SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                 long tt= Long.parseLong(a[1]);
                 if(prev!=tt) {
                     Date d = new Date(tt*1000);

                     String s="Got Notification: " + a[0] + "\n At " + sd.format(d);
                    createNotification("Notification tester",a[0],ac);
                    prev=tt;
                     ac.runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             tv.setText(s);

                         }
                     });
                 }
             }
         } catch (IOException e) {
             e.printStackTrace();
         }


     }



    public static void createNotification(String title, String message,Context mContext)
    {

        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder;
        final String NOTIFICATION_CHANNEL_ID = "10001";

        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mContext , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(android.R.drawable.stat_notify_chat);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());


        System.out.println("created notificaton: "+message);
    }


    public static String stringaturl(String url) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        StrictMode.setThreadPolicy(policy);

        StringBuilder response = new StringBuilder();
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
            String line = null;
            while ((line = input.readLine()) != null) {
                response.append(line);
            }
            input.close();
            return response.toString();
        } else {
            return "null";
        }
    }





}
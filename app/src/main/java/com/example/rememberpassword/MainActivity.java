package com.example.rememberpassword;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseActivity{

    private String Tag = "404";
    private ForceOfflineReceiver mReceiver=new ForceOfflineReceiver();
    private Button mSendNotice;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.rememberpassword.FORCE_OFFLINE");
        registerReceiver(mReceiver,  filter);

        //发送广播按钮
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button forceOffline = (Button) findViewById(R.id.force_offline);
        forceOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.rememberpassword.FORCE_OFFLINE");
                sendBroadcast(intent);
                Log.i(Tag,"onclick");
            }
        });

        //发送通知按钮
        mSendNotice = (Button) findViewById(R.id.send_notice);
        mSendNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.send_notice) {
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification.Builder builder = new Notification.Builder(MainActivity.this);
                    builder.setWhen(System.currentTimeMillis())
                            .setContentTitle("This is title")
                            .setContentText("This is text")
                            .setSmallIcon(R.mipmap.watermelon)
                            .setDefaults(Notification.DEFAULT_ALL);
                    Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent, PendingIntent.FLAG_IMMUTABLE);
                    builder.setContentIntent(pendingIntent);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("1","my_channel",NotificationManager.IMPORTANCE_DEFAULT);
                            channel.enableLights(true);
                            channel.setLightColor(Color.GREEN);
                            channel.setShowBadge(true);
                            manager.createNotificationChannel(channel);
                            builder.setChannelId("1");
                        }
                    Notification notification = builder.build();
                    manager.notify(1, notification);
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

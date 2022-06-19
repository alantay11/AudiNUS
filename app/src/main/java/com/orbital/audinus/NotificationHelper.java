package com.orbital.audinus;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class NotificationHelper extends Application {
    public static final String CHANNEL_1_ID = "Music";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        NotificationChannel channel1 = new NotificationChannel(
                CHANNEL_1_ID,
                "Music",
                NotificationManager.IMPORTANCE_LOW);
        channel1.setDescription("This is for music");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel1);
    }
}

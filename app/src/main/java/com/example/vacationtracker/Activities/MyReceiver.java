package com.example.vacationtracker.Activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.vacationtracker.R;

public class MyReceiver extends BroadcastReceiver {
    String channel_id = "vacation_alert_channel";
    static int notificationID;

    @Override
    public void onReceive(Context context, Intent intent) {
        String vacationMessage = intent.getStringExtra("VacationKey");
        String excursionMessage = intent.getStringExtra("ExcursionKey");

        String message = null;
        String title = "Alert";

        if (vacationMessage != null && !vacationMessage.isEmpty()) {
            message = vacationMessage;
            title = "Vacation Alert";
        } else if (excursionMessage != null && !excursionMessage.isEmpty()) {
            message = excursionMessage;
            title = "Excursion Alert";
        }

        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            createNotificationChannel(context, channel_id);
            Notification notification = new NotificationCompat.Builder(context, channel_id)
                    .setSmallIcon(R.drawable.app_logo_foreground)
                    .setContentText(message)
                    .setContentTitle(title)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID++, notification);
        }
    }

    private void createNotificationChannel(Context context, String CHANNEL_ID) {
        CharSequence name = context.getString(R.string.app_name);
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
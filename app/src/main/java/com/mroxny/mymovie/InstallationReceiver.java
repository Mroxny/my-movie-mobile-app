package com.mroxny.mymovie;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class InstallationReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        showNotification();
    }


        private void showNotification() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelID = "notification_welcome";


                CharSequence name = context.getString(R.string.tag_add_movie);
                String description = context.getString(R.string.tag_add_movie);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(channelID, name, importance);
                channel.setDescription(description);

                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

                Intent intent = new Intent(context, LoginActivity.class);
                @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                        .setSmallIcon(R.drawable.ic_mymovieicon_notification)
                        .setContentTitle(context.getString(R.string.notification_title_1))
                        .setContentText(context.getString(R.string.notification_text_1))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.notification_big_text_1)))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                notificationManager.notify(0, builder.build());
            }
        }


}

package com.zawisza.guitar_app.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.activities.AdminActivity;

public class Notifications extends FirebaseMessagingService {

    NotificationManager notificationManager;

    private static final String TAG = "Rajd - Notifications";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d(TAG, "Message not null");
        if(message.getData().size() > 0){
            Log.d(TAG,"Have Data");
            String id = message.getData().get("documentID");
            String title = message.getData().get("title");
            String body = message.getData().get("body");
            String collection = message.getData().get("collection");
            Log.d(TAG,"ID = " + id);
            getFirebaseMessage(title,body, id, collection);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void getFirebaseMessage(String title, String msg, String id, String collection){
        Intent resultIntent = new Intent(this, AdminActivity.class);
        resultIntent.putExtra("documentID", id);
        resultIntent.putExtra("collection", collection);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d(TAG,"msg = " + msg);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                    PendingIntent.FLAG_IMMUTABLE);
        }else{
            resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG,"Step 1");
            NotificationChannel channel = new NotificationChannel("Rajd 2023",
                    "Fcm notifications",
                    NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setSound(notification, audioAttributes);
            channel.enableLights(true);
            channel.enableVibration(true);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.logo_rajd);
        Log.d(TAG,"Step 2");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Rajd 2023")
                .setSmallIcon(R.drawable.notification_alarm_buzzer_icon)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(msg)
                .setColor(Color.BLUE)
                .setAutoCancel(true)
                //.setSound(notification)
                //.setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                //.setStyle(new NotificationCompat.InboxStyle())
                //.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setContentIntent(resultPendingIntent);


        Log.d(TAG,"Step " + builder);

        Log.d(TAG,"Step 3");

        NotificationManagerCompat.from(this).notify(101,builder.build());

    }


}

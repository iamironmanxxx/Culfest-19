package com.culfest.culfest2k19;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String NOTIF_CODE_GENERAL = "notif_code_general";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        sendNotification(remoteMessage);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    private void sendNotification(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title") ;
        String messageBody = remoteMessage.getData().get("body") ;

        String activity = remoteMessage.getData().containsKey("activity")?remoteMessage.getData().get("activity"):null;
        int notif_code = remoteMessage.getData().containsKey("notif_code")?Integer.parseInt(remoteMessage.getData().get("notif_code")):0;

        SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);

        notif_code = sp.getInt(NOTIF_CODE_GENERAL,0);

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(NOTIF_CODE_GENERAL, notif_code+1);
        editor.apply();


        Intent intent;

      /*  if(activity.equals("view_order")) {
            intent = new Intent(this, ViewOrderActivity.class)
                    .putExtra(C.TEXT_FROM_NOTIFICATION,true)
                    .putExtra(C.TEXT_ORDER_ID,remoteMessage.getData().get(C.TEXT_ORDER_ID));
        }else if(activity.equals("view_orders")){
            intent = new Intent(this, ViewOrdersActivity.class);
        }else{
            intent = new Intent(this, StartupActivity.class);
        }*/
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

         intent = new Intent(this, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Admin Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify( notif_code /* ID of notification */, notificationBuilder.build());
    }
}

package com.ghn.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.UnsupportedEncodingException;

public class MyGcmListenerService extends GcmListenerService {

  private static final String TAG = "MyGcmListenerService";

  /**
   * Called when message is received.
   *
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   *             For Set of keys use data.keySet().
   */
  // [START receive_message]
  @Override
  public void onMessageReceived(String from, Bundle data) {
    String message = data.getString("description"), tittle = data.getString("news_title");
//    try {
//      message = new String(Base64.decode(Base64.encode(data.getString("description").getBytes("UTF-16"),
//          Base64.DEFAULT), Base64.DEFAULT));
//      tittle = new String(Base64.decode(Base64.encode(data.getString("news_title").getBytes("UTF-16"),
//          Base64.DEFAULT), Base64.DEFAULT));
//
//    } catch (UnsupportedEncodingException e) {
//      Log.e("utf8", "conversion", e);
//    }
//    String message = data.getString("description");
//    String tittle = data.getString("news_title");

    Log.d(TAG, "From: " + from);
    Log.d(TAG, "Message: " + message);

    if (from.startsWith("/topics/")) {
      // message received from some topic.
    } else {
      // normal downstream message.
    }

    // [START_EXCLUDE]
    /**
     * Production applications would usually process the message here.
     * Eg: - Syncing with server.
     *     - Store message in local database.
     *     - Update UI.
     */

    /**
     * In some cases it may be useful to show a notification indicating to the user
     * that a message was received.
     */
    sendNotification(message, tittle);
    // [END_EXCLUDE]
  }
  // [END receive_message]

  /**
   * Create and show a simple notification containing the received GCM message.
   *
   * @param message GCM message received.
   */
  private void sendNotification(String message, String tittle) {
    Intent intent = new Intent(this, com.ghn.android.ui.feeds.SplashScreen.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        PendingIntent.FLAG_ONE_SHOT);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(tittle)
        .setContentText(message)
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
  }
}
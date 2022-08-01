package org.planetaccounting.saleAgent.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.model.NotificationPost;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.transfere.transfereActivity;
import org.planetaccounting.saleAgent.utils.LocaleManager;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.Random;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;
    @Inject
    LocaleManager localeManager;


    @Override
    public void onCreate() {
        super.onCreate();
        ((Kontabiliteti) getApplication()).getKontabilitetiComponent().inject(this);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        int notificationiD = new Random().nextInt(60000);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = getString(R.string.message_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.message_notification_channel_name);
            String descriptionText = getString(R.string.message_notification_channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new  NotificationChannel(channelId, name, importance);
            channel.setDescription(descriptionText);
            channel.enableLights(true);
            channel. enableVibration(true);
            if (defaultSoundUri != null) {
                    AudioAttributes att = new  AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();
                channel.setSound(defaultSoundUri, att);
                }

                channel. setLightColor(Color.parseColor("#501450"));
            notificationManager.createNotificationChannel(channel);
        }


   Intent     notificationIntent = new Intent(this, MainActivity.class);
        Log.e("NotificationFidani","TEST");
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.setAction("actionstring" + System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)//a resource for your custom small icon
                .setColor(Color.parseColor("#ffffff"))
                .setContentTitle(remoteMessage.getNotification().getTitle())//the "title" value you sent in your notification
                .setContentText(remoteMessage.getNotification().getBody()) //ditto
                .setAutoCancel(true) //dismisses the notification on click
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationiD /* ID of notification */, notificationBuilder.build());

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        preferences.saveNotification(s);
        if (preferences.getToken()!=null){
            registerDevice(s);
        }
    }

    void registerDevice(String newToken){
        apiService.setNewDevice(new NotificationPost(preferences.getToken(), preferences.getUserId(),newToken,preferences.getDeviceid(),preferences.getDeviceName()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transferDetailRespose -> {
                    if (transferDetailRespose.getSuccess()) {

                    } else {
                    }
                });
    }
    }



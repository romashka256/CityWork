package com.producticity.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.producticity.App;
import com.producticity.Constants;
import com.producticity.R;
import com.producticity.ui.MainActivity;

import timber.log.Timber;

public class NotificationUtils {

    public static final String ALARM_NOTIFICATION_CHANNEL_ID = "alarm_channel_id";
    public static final String TIMER_NOTIFICATION_CHANNEL_ID = "timer_channel_id";
    public static final int ALARM_NOTIFICATION_ID = 8888;
    public static final int TIMER_NOTIFICATION_ID = 7777;

    private NotificationManager notificationManager;
    private Context context;

    private SharedPrefensecUtils sharedPrefensecUtils;
    private NotificationCompat.Builder mTimerNotificationBuilder;

    private String timerNotTitle;
    private Resources resources;

    public NotificationUtils(Context context) {
        this.context = context;
        timerNotTitle = context.getResources().getString(R.string.building_building);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        resources = context.getResources();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
    }

    public void showAlarmNotification() {
        notificationManager.notify(ALARM_NOTIFICATION_ID, buildAlarmNotification());
    }

    private Notification buildAlarmNotification() {
        // Create an Intent for the activity you want to start
        Intent intentNotif = new Intent(context, MainActivity.class);
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intentNotif, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(ALARM_NOTIFICATION_CHANNEL_ID, context.getResources().getString(R.string.alarm_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            builder = new Notification.Builder(context, ALARM_NOTIFICATION_CHANNEL_ID);
            notificationChannel.setDescription("My channel description");
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            builder = new Notification.Builder(context);
        }

        Intent smallRestIntent = new Intent(context, MainActivity.class);
        Bundle smallRestIntentbundle = new Bundle();
        smallRestIntentbundle.putInt(Constants.TIMER_NOT_INTENT_KEY, Constants.TIMER_NOT_INTENT_SMALLREST);
        smallRestIntent.putExtras(smallRestIntentbundle);

        Intent bigRestIntent = new Intent(context, MainActivity.class);
        Bundle taskIntentBundle = new Bundle();
        taskIntentBundle.putInt(Constants.TIMER_NOT_INTENT_KEY, Constants.TIMER_NOT_INTENT_BIGREST);
        bigRestIntent.putExtras(taskIntentBundle);

        PendingIntent smallRestIntentpendingIntent = PendingIntent.getActivity(context, 4, smallRestIntent, 0);
        PendingIntent bigRestIntentpendingIntent = PendingIntent.getActivity(context, 5, bigRestIntent, 0);

        builder.setContentTitle(resources.getString(R.string.success_not_title))
                .setSmallIcon(R.drawable.small_wh)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.icongreen))
                .addAction(R.drawable.ic_timer_icon_focused, sharedPrefensecUtils.getShortBreak() / 60 + " " + resources.getString(R.string.minute), smallRestIntentpendingIntent)
                .addAction(R.drawable.ic_timer_icon_focused, sharedPrefensecUtils.getLongBreak() / 60 + " " + resources.getString(R.string.minute), bigRestIntentpendingIntent)
                .setContentText(resources.getString(R.string.success_not_subtitle))
                .setContentIntent(pendIntent);

        return builder.build();
    }

    public void showTimerNotification(String time, String endTime) {
        Timber.i("showTimerNotification : %s", time);
        notificationManager.notify(TIMER_NOTIFICATION_ID, buildTimerNotification(time, endTime));
    }

    public void updateTimerNotification(String time, int percent, int imageId) {
        mTimerNotificationBuilder.setSubText(time);

        notificationManager.notify(TIMER_NOTIFICATION_ID, mTimerNotificationBuilder.build());
    }

    public Notification buildTimerNotification(String time, String endTime) {
        Timber.i("buildTimerNotification : %s", time);
        // Create an Intent for the activity you want to start
        Intent intentNotif = new Intent(context, MainActivity.class);
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intentNotif, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(TIMER_NOTIFICATION_CHANNEL_ID, context.getResources().getString(R.string.timer_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            mTimerNotificationBuilder = new NotificationCompat.Builder(context, TIMER_NOTIFICATION_CHANNEL_ID);
            mTimerNotificationBuilder.setChannelId(TIMER_NOTIFICATION_CHANNEL_ID)
                    .setOnlyAlertOnce(true);
            notificationChannel.setDescription("Timer channel description");
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            mTimerNotificationBuilder = new NotificationCompat.Builder(context);
            mTimerNotificationBuilder.setVibrate(null);
        }

        Intent stopIntent = new Intent(context, MainActivity.class);
        Bundle stopIntentbundle = new Bundle();
        stopIntentbundle.putInt(Constants.TIMER_NOT_INTENT_KEY, Constants.TIMER_NOT_INTENT_STOP);
        stopIntent.putExtras(stopIntentbundle);

        Intent taskIntent = new Intent(context, MainActivity.class);
        Bundle taskIntentBundle = new Bundle();
        taskIntentBundle.putInt(Constants.TIMER_NOT_INTENT_KEY, Constants.TIMER_NOT_INTENT_TASKS);
        taskIntent.putExtras(taskIntentBundle);

        PendingIntent taskpendingIntent = PendingIntent.getActivity(context, 2, taskIntent, 0);
        PendingIntent stoppendingIntent = PendingIntent.getActivity(context, 3, stopIntent, 0);

        return mTimerNotificationBuilder.setContentIntent(pendIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_timer_icon_focused, context.getResources().getString(R.string.stop), stoppendingIntent)
                .setContentText(context.getResources().getString(R.string.timer_not_subtitle) + " " +  endTime)
                .setContentTitle(context.getResources().getString(R.string.timer_not_title))
                .setShowWhen(false)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.small_wh)
                .build();
    }

    public void closeTimerNotification() {
        Timber.i("Closing Timer Notification");
        notificationManager.cancel(TIMER_NOTIFICATION_ID);
    }

    public void closeAlarmNotification() {
        Timber.i("Closing Alarm Notification");
        notificationManager.cancel(ALARM_NOTIFICATION_ID);
    }
}

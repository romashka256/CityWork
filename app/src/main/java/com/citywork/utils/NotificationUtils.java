package com.citywork.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.citywork.R;
import com.citywork.ui.MainActivity;
import timber.log.Timber;

public class NotificationUtils {

    public static final String ALARM_NOTIFICATION_CHANNEL_ID = "alarm_channel_id";
    public static final String TIMER_NOTIFICATION_CHANNEL_ID = "timer_channel_id";
    public static final int ALARM_NOTIFICATION_ID = 8888;
    public static final int TIMER_NOTIFICATION_ID = 7777;

    private NotificationManager notificationManager;
    private Context context;

    private RemoteViews mTimerCustomView;
    private Notification mTimerNotification;
    private Notification.Builder mTimerNotificationBuilder;

    public NotificationUtils(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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

        builder.setContentTitle("Здание построено")
                .setSubText("Пора отдохнуть")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Нажмите чтобы посмотреть")
                .setContentIntent(pendIntent);

        return builder.build();
    }

    public void showTimerNotification(String time) {
        Timber.i("showTimerNotification : %s", time);
        notificationManager.notify(TIMER_NOTIFICATION_ID, buildTimerNotification(time));
    }

    public void updateTimerNotification(String time) {
        mTimerCustomView.setTextViewText(R.id.timer_notification_time, time);
        notificationManager.notify(TIMER_NOTIFICATION_ID, mTimerNotificationBuilder.build());
    }

    public Notification buildTimerNotification(String time) {
        // Create an Intent for the activity you want to start
        Intent intentNotif = new Intent(context, MainActivity.class);
        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intentNotif, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(TIMER_NOTIFICATION_CHANNEL_ID, context.getResources().getString(R.string.timer_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            mTimerNotificationBuilder = new Notification.Builder(context, TIMER_NOTIFICATION_CHANNEL_ID);
            mTimerNotificationBuilder.setChannelId(TIMER_NOTIFICATION_CHANNEL_ID);
            notificationChannel.setDescription("Timer channel description");
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            mTimerNotificationBuilder = new Notification.Builder(context);
        }

        mTimerNotificationBuilder.setContentIntent(pendIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContent(createTimerNotifLayout(time));

        return mTimerNotificationBuilder.build();
    }

    private RemoteViews createTimerNotifLayout(String time) {
        mTimerCustomView = new RemoteViews(context.getPackageName(), R.layout.timer_notification);
        mTimerCustomView.setTextViewText(R.id.timer_notification_time, time);
        return mTimerCustomView;
    }

    public void closeTimerNotification() {
        notificationManager.cancel(TIMER_NOTIFICATION_ID);
    }

    public void closeAlarmNotification() {
        notificationManager.cancel(ALARM_NOTIFICATION_ID);
    }
}

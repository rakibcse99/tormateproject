package com.example.tourmate;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GeofenceingTrigerringService extends IntentService {
    private final String CHANNEL = "mychannel";

    private static final String TAG = GeofenceingTrigerringService.class.getSimpleName();

    public GeofenceingTrigerringService() {
        super("GeofenceingTrigerringService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        int transition = event.getGeofenceTransition();
        String transitionString = "";
        switch (transition){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transitionString = "entered";
            break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transitionString = "exited";
                break;
        }

        List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
        List<String> names = new ArrayList<>();
        for (Geofence g : triggeringGeofences){
            names.add(g.getRequestId());
        }

        String notificationString =
                "You have "+transitionString+" "+
                        TextUtils.join(", ",names);
        Log.e(TAG, notificationString);
        sendNotification(notificationString);
    }

    private void sendNotification(String notificationString) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL);
        builder.setSmallIcon(R.drawable.ic_notifications_paused_black_24dp);
        builder.setContentTitle("Geofence Detected");
        builder.setContentText(notificationString);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL, "some description",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        manager.notify(999, builder.build());
    }
}

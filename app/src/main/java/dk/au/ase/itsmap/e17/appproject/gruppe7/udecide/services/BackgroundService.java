package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.activities.MainActivity;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers.FirebaseHelper;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Notification;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_POLLS_COLLECTION;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_USER_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.NOTIFY_CHANNEL;

// ITSMAP L7 Services and Asynch Processing - DemoCode: ServicesDemo
// https://stackoverflow.com/questions/37751823/how-to-use-firebase-eventlistener-as-a-background-service-in-android
// https://developer.android.com/guide/topics/ui/notifiers/notifications.html
public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";
    FirebaseHelper firebaseHelper;
    private NotificationManager mNotificationManager;
    private CollectionReference pollsRef;
    private ListenerRegistration registration;
    private boolean mRunning;

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Background service onCreate");

        mRunning = false;

        firebaseHelper = new FirebaseHelper(getBaseContext());

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(NOTIFY_CHANNEL, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(getString(R.string.app_name));
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }

        pollsRef = FirebaseFirestore.getInstance().collection(DB_POLLS_COLLECTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Background service onStartCommand");

        if (!mRunning) {
            mRunning = true;
            Log.d(TAG, "onStartCommand: is Starting");
            String facebookId = intent.getStringExtra(FACEBOOK_ID);
            registration = pollsRef.whereEqualTo(DB_USER_ID, facebookId).addSnapshotListener(getEventListener());
            return START_STICKY;
        } else {
            Log.d(TAG, "onStartCommand: is Running");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Background service onDestroy");
        registration.remove();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Background service onBind");
        return null;
    }

    private EventListener<QuerySnapshot> getEventListener() {
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    Poll poll = documentSnapshot.toObject(Poll.class);
                    if (poll.getNotifyNumber() != 0 && (poll.getImage1Votes() + poll.getImage2Votes() == poll.getNotifyNumber())) {
                        int numericValueOfString = 0;
                        for (char ch : documentSnapshot.getId().toCharArray())
                            numericValueOfString += Character.getNumericValue(ch);
                        new SendNotification().execute(new Notification(numericValueOfString, poll.getQuestion(), poll.getImage1Votes() + "/" + poll.getImage2Votes()));
                        DocumentReference documentReference = documentSnapshot.getReference();
                        firebaseHelper.removePollNotification(documentReference);
                    }
                }
            }
        };
    }

    private class SendNotification extends AsyncTask<Notification, Void, Void> {
        protected Void doInBackground(Notification... notifications) {
            for (Notification notification : notifications) {
                Log.i(TAG, "Background service sendNotification: " + notification.getId() + " = " + notification.getTitle() + " " + notification.getText());

                Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, mainIntent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), NOTIFY_CHANNEL);
                builder.setSmallIcon(R.drawable.ic_compare_arrows_black_24dp)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getText())
                        .setContentIntent(pendingIntent);

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setColor(Color.TRANSPARENT)
                            .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                            .setLights(Color.RED, 500, 250)
                            .setAutoCancel(true);
                }

                mNotificationManager.notify(notification.getId(), builder.build());
            }
            return null;
        }
    }
}

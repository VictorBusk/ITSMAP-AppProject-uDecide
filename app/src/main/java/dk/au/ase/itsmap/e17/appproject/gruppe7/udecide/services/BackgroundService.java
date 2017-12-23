package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_POLLS_COLLECTION;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_USER_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.NOTIFY_CHANNEL;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.NOTIFY_ID;

// ITSMAP L7 Services and Asynch Processing - DemoCode: ServicesDemo
// https://stackoverflow.com/questions/37751823/how-to-use-firebase-eventlistener-as-a-background-service-in-android
public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";
    private String facebookId;
    private FirebaseFirestore db;
    private CollectionReference pollsRef;
    private Query myPoolsRef;
    private ListenerRegistration registration;
    private EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                Poll poll = documentSnapshot.toObject(Poll.class);
                if (poll.getNotifyNumber() != 0 && ((poll.getImage1Votes() + poll.getImage2Votes()) % poll.getNotifyNumber() == 0)) {
                    sendNotification(poll.getQuestion(), poll.getImage1Votes(), poll.getImage2Votes());
                }
            }
        }
    };

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Background service onCreate");
        db = FirebaseFirestore.getInstance();
        pollsRef = db.collection(DB_POLLS_COLLECTION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFY_CHANNEL, NOTIFY_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Background service onStartCommand");

        facebookId = intent.getStringExtra(FACEBOOK_ID);
        myPoolsRef = pollsRef.whereEqualTo(DB_USER_ID, facebookId);
        registration = myPoolsRef.addSnapshotListener(eventListener);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Background service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Background service onDestroy");
        registration.remove();
    }

    // ITSMAP L7 Services and Asynch Processing - DemoCode: ServicesDemo2
    // https://stackoverflow.com/questions/45395669/notifications-fail-to-display-in-android-oreo-api-26
    private void sendNotification(String question, int vote1, int vote2){
        Notification notification = new NotificationCompat.Builder(this, NOTIFY_CHANNEL)
                        .setContentTitle(getText(R.string.app_name))
                        .setContentText(getText(R.string.notification_description) + " " + question)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setChannelId(NOTIFY_CHANNEL)
                        .build();
        startForeground(NOTIFY_ID, notification);
    }
}

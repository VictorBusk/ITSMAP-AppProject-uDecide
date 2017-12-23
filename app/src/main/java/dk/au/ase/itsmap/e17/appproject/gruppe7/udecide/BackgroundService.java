package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.DB_POLLS_COLLECTION;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.DB_USER_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.FACEBOOK_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.NOTIFY_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.SHARED_PREFERENCES;

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
            Log.i(TAG, "onEvent");
            for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                Poll poll = documentSnapshot.toObject(Poll.class);
                if (poll.getNotifyNumber() != 0 && ((poll.getImage1Votes() + poll.getImage2Votes()) % poll.getNotifyNumber() == 0)) {
                    Log.i(TAG,"You got new votes! " + poll.getQuestion() + ": " + poll.getImage1Votes() + "/" + poll.getImage2Votes());
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

    private void sendNotification(String question, int vote1, int vote2){
        Notification notification =
                new Notification.Builder(this)
                        .setContentTitle("uDecide")
                        .setContentText("new votes: " + vote1 + "/" + vote2)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //        .setContentIntent(pendingIntent)
                        .setTicker(question)
                        .build();
        startForeground(NOTIFY_ID, notification);
    }
}

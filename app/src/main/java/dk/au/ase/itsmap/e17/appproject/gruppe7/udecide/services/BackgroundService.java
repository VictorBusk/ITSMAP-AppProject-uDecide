package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.activities.MainActivity;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_POLLS_COLLECTION;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_USER_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_ID;

// ITSMAP L7 Services and Asynch Processing - DemoCode: ServicesDemo
// https://stackoverflow.com/questions/37751823/how-to-use-firebase-eventlistener-as-a-background-service-in-android
public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";
    private String facebookId;
    private FirebaseFirestore db;
    private CollectionReference pollsRef;
    private Query myPoolsRef;
    private ListenerRegistration registration;

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Background service onCreate");
        db = FirebaseFirestore.getInstance();
        pollsRef = db.collection(DB_POLLS_COLLECTION);
        sendNotification("Eric Cartman Says:", "How would you like to... Suck my balls!?", 0, 0);
        new GetChuckNorrisJoke().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Background service onStartCommand");

        facebookId = intent.getStringExtra(FACEBOOK_ID);
        myPoolsRef = pollsRef.whereEqualTo(DB_USER_ID, facebookId);
        registration = myPoolsRef.addSnapshotListener(eventListener);

        return START_STICKY;
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

    private EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                Poll poll = documentSnapshot.toObject(Poll.class);
                if (poll.getNotifyNumber() != 0 && ((poll.getImage1Votes() + poll.getImage2Votes()) % poll.getNotifyNumber() == 0)) {
                    sendNotification(poll.getQuestion(), "New Votes", poll.getImage1Votes(), poll.getImage2Votes());
                }
            }
        }
    };

    private void sendNotification(String title, String text, int vote1, int vote2) {
        Log.i(TAG, "Background service sendNotification: " + title + " " + text);

        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null);
        builder.setContentTitle(title)
                .setSmallIcon(R.drawable.ic_compare_arrows_black_24dp)
                .setContentIntent(pendingIntent)
                .setContentText(text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(title, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(getString(R.string.app_name));
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            builder.setContentTitle(getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(Color.TRANSPARENT)
                    .setVibrate(new long[]{100, 250})
                    .setLights(Color.YELLOW, 500, 5000)
                    .setAutoCancel(true);
        }

        builder.setChannelId(title);
        mNotificationManager.notify(1, builder.build());
    }

    private class GetChuckNorrisJoke extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            String url = "https://api.chucknorris.io/jokes/random";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            String joke = "";
            try {
                Response response = client.newCall(request).execute();
                String body = response.body().string();
                Log.i(TAG, "okHttp: " + body);
                joke = new JSONObject(body).getString("value");
                Log.i(TAG, "joke: " + joke);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return joke;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            sendNotification("Chuck Norris Fact:", s, 0, 0);
        }
    }
}

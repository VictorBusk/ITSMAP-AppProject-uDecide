package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

public class BackgroundService extends AsyncTask<String, Void, String> {
    Context context;

    public BackgroundService() {
    }

    @Override
    protected String doInBackground(String... strings) {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getUid();
        getMyPolls(userID);

        return null;
    }

    private List<Poll> getMyPolls(String userID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference polls = db.collection("polls");

        final List<Poll> pollList = new ArrayList<Poll>();

        polls.whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                pollList.add(document.toObject(Poll.class));
                            }
                        } else {
                            Log.d(String.valueOf(this), "Error getting documents: ", task.getException());
                        }

                        sendPolls(pollList);
                    }
                });

        return pollList;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        double voteProcentes = (20 / (8 + 1)) * 100;
        //sendPolls("test", voteProcentes);
    }

    protected void onPostExecute(String questionText, double voteForImage1, double voteForImage2) {
        double voteProcentes = (voteForImage1 / (voteForImage1 + voteForImage2)) * 100;
        //sendPolls(questionText, voteProcentes);
    }

    private void sendPolls(List<Poll> polls) {
        Intent intent = new Intent(CONST.UPDATE_EVENT);

        ArrayList<Object> objects = new ArrayList<Object>();

        Bundle extra = new Bundle();
        extra.putSerializable("objects", objects);
        intent.putExtra("extra", (Serializable) polls);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
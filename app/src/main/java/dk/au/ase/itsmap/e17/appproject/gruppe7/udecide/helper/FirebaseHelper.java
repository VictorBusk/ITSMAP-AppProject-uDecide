package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adapters.MyQuestionsAdapter;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_POLLS_COLLECTION;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_USER_ID;

//Inspired by own Assignment 2 solution
public class FirebaseHelper {

    Context context;
    Poll currentPoll;
    DocumentReference pollsDocRef;
    private List<Poll> polls = new ArrayList<Poll>();


    public FirebaseHelper(Context context) {
        this.context = context;
    }

    public void getPollData(Query publicPolls, final Set<String> stringSet) {
        currentPoll = null;
        publicPolls.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(String.valueOf(this), "DocumentSnapshot data: " + document.getData());
                                currentPoll = document.toObject(Poll.class);
                                deciderBroadcast(currentPoll, stringSet);
                                pollsDocRef = document.getReference();
                            }
                        } else {
                            Log.d(this.toString(), "Error getting unfiltered documents: ", task.getException());
                        }
                    }
                });
    }

    //Inspired by: https://dzone.com/articles/cloud-firestore-read-write-update-and-delete
    public void incrementImageVotes(String imageVoteName) {
        int newVotes = 0;
        if (imageVoteName == CONST.IMAGE_1_VOTE_KEY) {
            newVotes = currentPoll.getImage1Votes() + 1;
        } else if (imageVoteName == CONST.IMAGE_2_VOTE_KEY) {
            newVotes = currentPoll.getImage2Votes() + 1;
        }
        pollsDocRef.update(imageVoteName, newVotes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("incrementer", "Image has been incremented");
                    }
                });
        sendMessagePollUpdate();
    }

    public void updateMyQuestionPolls(CollectionReference pollsRef, String facebookId) {
        pollsRef.whereEqualTo(DB_USER_ID, facebookId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            polls.add(documentSnapshot.toObject(Poll.class));
                            sendMessageMyQuestion();
                        }
                    }
                });
    }

    private Poll deciderBroadcast(Poll currentPoll, final Set<String> stringSet){
        if (currentPoll == null) {
            sendMessageNoMorePolls();
        } else if(currentPoll.showForPublic ||  stringSet.contains(currentPoll.getUserID())) {
            sendMessagePollAcquired(currentPoll);
        } else {
            sendMessagePollUpdate();
        }
        return currentPoll;
    }

    private void sendMessageNoMorePolls() {
        Log.d("No more polls", "Broadcasting message");
        Intent intent = new Intent(CONST.NO_MORE_POLLS);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendMessagePollAcquired(Poll currentPoll) {
        Log.d("Poll acquired", "Broadcasting message");
        Intent intent = new Intent(CONST.UPDATE_EVENT);
        // You can also include some extra data.
        intent.putExtra(CONST.IMAGE_1, currentPoll.getImage1ID());
        intent.putExtra(CONST.IMAGE_2, currentPoll.getImage2ID());
        intent.putExtra(CONST.CURRENT_POLL, currentPoll);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendMessagePollUpdate() {
        Log.d("Update poll", "Broadcasting message");
        Intent intent = new Intent(CONST.UPDATE_POLL);
        intent.putExtra(CONST.CURRENT_POLL, currentPoll);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendMessageMyQuestion() {
        Log.d("My questions loaded", "Broadcasting message");
        Intent intent = new Intent(CONST.MYQUESTION_POLL);
        intent.putParcelableArrayListExtra(CONST.MY_POLLS, (ArrayList<? extends Parcelable>) polls);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

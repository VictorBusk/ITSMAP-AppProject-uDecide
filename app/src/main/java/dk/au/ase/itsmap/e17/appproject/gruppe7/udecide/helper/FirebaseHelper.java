package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Set;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST;

public class FirebaseHelper {

    Context context;
    Poll currentPoll;
    DocumentReference pollsDocRef;

    public FirebaseHelper(Context context) {
        this.context = context;
    }

    public void getPollData(Query publicPolls, final Set<String> stringSet) {
        publicPolls.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(String.valueOf(this), "DocumentSnapshot data: " + document.getData());
                                currentPoll = document.toObject(Poll.class);
                                broadcast(currentPoll, stringSet);
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

    private Poll broadcast(Poll currentPoll, final Set<String> stringSet){
        if(currentPoll.showForPublic ||  stringSet.contains(currentPoll.getUserID())) {
            sendMessagePollAcquired(currentPoll);
        } else if (currentPoll == null) {
            sendMessageNoMorePolls();
        } else {
            sendMessagePollUpdate();
        }
        return currentPoll;
    }

    // Send out a local broadcast with the newly obtained data
    private void sendMessagePollAcquired(Poll currentPoll) {
        Log.d("Poll acquired", "Broadcasting message");
        Intent intent = new Intent(CONST.UPDATE_EVENT);
        // You can also include some extra data.
        intent.putExtra(CONST.IMAGE_1, currentPoll.getImage1ID());
        intent.putExtra(CONST.IMAGE_2, currentPoll.getImage2ID());
        intent.putExtra(CONST.CURRENT_POLL, currentPoll);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // Send out a local broadcast with the newly obtained data
    private void sendMessageNoMorePolls() {
        Log.d("No more polls", "Broadcasting message");
        Intent intent = new Intent(CONST.NO_MORE_POLLS);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // Send out a local broadcast with the newly obtained data
    private void sendMessagePollUpdate() {
        Log.d("Update poll", "Broadcasting message");
        Intent intent = new Intent(CONST.UPDATE_POLL);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}

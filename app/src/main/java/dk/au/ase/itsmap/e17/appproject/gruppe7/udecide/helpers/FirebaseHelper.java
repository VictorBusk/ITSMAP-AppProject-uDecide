package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_USER_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.NOTIFY_NUMBER;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.STORAGE_IMAGES_PATH;

//Inspired by own Assignment 2 solution
public class FirebaseHelper {

    Context context;
    Poll currentPoll;
    DocumentReference pollsDocRef;
    private List<Poll> polls = new ArrayList<Poll>();
    String TAG = "FirebaseHelper";

    public FirebaseHelper(Context context) {
        this.context = context;
    }

    public FirebaseUser extractUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void signInFirebase(AccessToken token, Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener((Activity) context, signInWithCredentialOnCompleteListener());
    }

    private OnCompleteListener<AuthResult> signInWithCredentialOnCompleteListener() {

        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Boolean signInSucceeded;
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    signInSucceeded = true;
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    signInSucceeded = false;
                }
                sendMessageSignInAttempted(signInSucceeded);
            }
        };
    }

    public void getPollData(Query publicPolls, final Set<String> facebookFriends) {
        final Task<QuerySnapshot> dataFetch = publicPolls.get();
        dataFetch.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.d(String.valueOf(this), "DocumentSnapshot data: " + document.getData());
                            currentPoll = document.toObject(Poll.class);
                            deciderBroadcast(currentPoll, facebookFriends);
                            pollsDocRef = document.getReference();
                        }
                    } else {
                        sendMessageNoMorePolls();
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
        if (currentPoll != null) {
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

    public void removePollNotification(DocumentReference pollsRef) {
        pollsRef.update(NOTIFY_NUMBER, 0).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "NotifyNumber successfully updated!");
                    }
                });
    }


    private Poll deciderBroadcast(Poll currentPoll, final Set<String> facebookFriends) {
        if (currentPoll.showForPublic || facebookFriends.contains(currentPoll.getUserID())) {
            sendMessagePollAcquired(currentPoll);
        } else {
            sendMessagePollUpdate();
        }
        return currentPoll;
    }

    public void savePollToFirebase(CollectionReference pollsCollection, Poll poll, final Context context) {
        pollsCollection.add(poll)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context.getApplicationContext(),
                                "Your poll is created", Toast.LENGTH_LONG).show();
                        sendMessagePollSaved();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context.getApplicationContext(),
                                "Something went wrong", Toast.LENGTH_LONG).show();

                        Log.w(String.valueOf(this), "Error adding document", e);
                    }
                });

        Toast.makeText(context.getApplicationContext(),
                "Your poll is created", Toast.LENGTH_LONG).show();
    }

    public String uploadImageToFirebase(Bitmap bitmap) {
        UUID uuid = UUID.randomUUID();
        final String imageID = uuid.toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child(STORAGE_IMAGES_PATH + imageID);

        byte[] data = convertBitmap(bitmap);

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(String.valueOf(this), "Unable to upload image to Firebase", exception);
            }
        });

        return imageID;
    }

    private byte[] convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return data;
    }

    private void sendMessageSignInAttempted(Boolean signInResult) {
        Log.d("Sign in attempted", "Broadcasting message");
        Intent intent = new Intent(CONST.SIGN_IN_EVENT);
        intent.putExtra(CONST.SIGN_IN_RESULT, signInResult);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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

    private void sendMessagePollSaved() {
        Log.d("My questions loaded", "Broadcasting message");
        Intent intent = new Intent(CONST.POLL_SAVED);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

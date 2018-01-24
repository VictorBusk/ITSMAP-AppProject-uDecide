package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Set;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_DATE;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.DB_USER_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.FACEBOOK_FRIENDS_IDS;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.LAST_POLL_TIMESTAMP;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.SHARED_PREFERENCES;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.utils.CONST.STORAGE_IMAGES_PATH;


public class DeciderFragment extends Fragment {

    public static final String TAG = "DeciderFragment";
    public static SharedPreferences preferences; //Shared preferences inspired by: https://stackoverflow.com/questions/23024831/android-shared-preferences-example
    double image1Votes, image2Votes;
    Poll currentPoll;
    String questionText, imageId1, imageId2;
    Bitmap bmp;
    CollectionReference pollsCollection;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    View view;
    private DocumentReference pollsDocRef;
    private ImageView firstImg, secondImg;
    private TextView questionTextTV, myProgressTextTv;
    private ProgressBar lastQuestionResult;
    private FirebaseFirestore db;


    public DeciderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_decider, container, false);

        intitializeUIElements();
        preferences = getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementImageVotes(CONST.IMAGE_1_VOTE_KEY);
                getPollData();
            }
        });

        secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementImageVotes(CONST.IMAGE_2_VOTE_KEY);
                getPollData();
            }
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        pollsCollection = db.collection(CONST.DB_POLLS_COLLECTION);
        getPollData();

        return view;
    }

    private void intitializeUIElements() {
        myProgressTextTv = view.findViewById(R.id.myTextProgress);
        questionTextTV = view.findViewById(R.id.questionTV);

        lastQuestionResult = view.findViewById(R.id.progressBar);
        lastQuestionResult.setMax(100);
        lastQuestionResult.setProgress(0);

        firstImg = view.findViewById(R.id.firstQuestionImg);
        secondImg = view.findViewById(R.id.secondQuestionImg);
    }

    private void updateProgessBar() {
        image1Votes = currentPoll.getImage1Votes();
        image2Votes = currentPoll.getImage2Votes();

        double votePercentage = (image1Votes / (image1Votes + image2Votes)) * 100;
        lastQuestionResult.setProgress((int) votePercentage);

        //to set text
        myProgressTextTv.setText((int) image1Votes + "/" + (int) image2Votes);
    }

    private void updateQuestionText(Poll currentPoll) {
        questionText = currentPoll.getQuestion();
        questionTextTV.setText(questionText);
    }


    //Inspired by: https://firebase.google.com/docs/firestore/query-data/get-data
    public void getPollData() {
        Long lastTimestamp = preferences.getLong(LAST_POLL_TIMESTAMP, 0);
        Date lastDate = new Date(lastTimestamp);
        final Query publicPolls;
        if (lastTimestamp != 0) {
            publicPolls = pollsCollection.whereGreaterThan(DB_DATE, lastDate).orderBy(DB_DATE, Query.Direction.ASCENDING).limit(1);
        } else {
            publicPolls = pollsCollection.orderBy(DB_DATE, Query.Direction.ASCENDING).limit(1);
        }
        final Set<String> stringSet = preferences.getStringSet(FACEBOOK_FRIENDS_IDS, null);
        for (String facebookFriendId : stringSet) {
            publicPolls.whereEqualTo(DB_USER_ID, facebookFriendId);
        }
        publicPolls.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(String.valueOf(this), "DocumentSnapshot data: " + document.getData());
                                currentPoll = document.toObject(Poll.class);
                                if (currentPoll.showForPublic == true || (currentPoll.showForPublic == false && stringSet.contains(currentPoll.getUserID()))) {
                                    updateQuestionText(currentPoll);
                                    imageId1 = currentPoll.getImage1ID();
                                    imageId2 = currentPoll.getImage2ID();
                                    getImage(imageId1, firstImg);
                                    getImage(imageId2, secondImg);
                                    pollsDocRef = document.getReference();
                                    updateProgessBar();
                                } else if (currentPoll == null) {
                                    firstImg.setImageResource(0);
                                    secondImg.setImageResource(0);
                                    questionTextTV.setText(getString(R.string.no_more_polls));
                                } else {
                                    saveLastPollTimestamp(currentPoll.getDate().getTime());
                                    getPollData();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting unfiltered documents: ", task.getException());
                        }
                    }
                });
    }

    // https://firebase.google.com/docs/storage/android/download-files#downloading_images_with_firebaseui
    public void getImage(String imageId, final ImageView imageView) {
        storageRef.child(STORAGE_IMAGES_PATH + imageId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri).fit().centerInside().into(imageView);
            }
        });
    }

    //Inspired by: https://dzone.com/articles/cloud-firestore-read-write-update-and-delete
    private void incrementImageVotes(String imageVoteName) {
        saveLastPollTimestamp(currentPoll.getDate().getTime());
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
                    }
                });
    }

    //Shared preferences inspired by: https://stackoverflow.com/questions/23024831/android-shared-preferences-example
    protected void saveLastPollTimestamp(Long timestamp) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LAST_POLL_TIMESTAMP, timestamp).apply();
    }
}

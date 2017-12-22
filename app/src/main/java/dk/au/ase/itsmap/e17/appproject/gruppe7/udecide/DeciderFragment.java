package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.tasks.OnFailureListener;
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

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.STORAGE_IMAGES_PATH;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeciderFragment extends Fragment {

    public static final String TAG = "Deciderfragment: ";
    private DocumentReference pollsDocRef;
    private ImageView firstImg, secondImg;
    private TextView questionTextTV, myProgressTextTv;
    private ProgressBar lastQuestionResult;
    private FirebaseFirestore db;
    double formerImage1Votes, formerImage2Votes;
    Poll currentPoll;
    String questionText, imageId1, imageId2;
    Bitmap bmp;
    CollectionReference pollsCollection;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    View view;

    public DeciderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_decider, container, false);
        intitializeUIElements();

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

//        final String currentPolls = "ZFxMhPLpNYfgcQUVHjcL"; //Jeppe jeg skal have poll fra user id filtering! :D
        pollsCollection = db.collection(CONST.DB_POLLS_COLLECTION);
//        pollsDocRef = pollsCollection.document(currentPolls).;
        pollsDocRef = pollsCollection.document();
        getUnfilteredPollData();

        return view;
    }

    private void intitializeUIElements() {
        myProgressTextTv = view.findViewById(R.id.myTextProgress);
        questionTextTV = view.findViewById(R.id.questionTV);

        lastQuestionResult = view.findViewById(R.id.progressBar);
        lastQuestionResult.setMax(100);
        lastQuestionResult.setProgress(50);

        firstImg = view.findViewById(R.id.firstQuestionImg);
        secondImg = view.findViewById(R.id.secondQuestionImg);
    }

    private void updateProgessBar() {
        formerImage1Votes = currentPoll.getImage1Votes();
        formerImage2Votes = currentPoll.getImage2Votes();

        double votePercentage = (formerImage1Votes / (formerImage1Votes + formerImage2Votes)) * 100;
        lastQuestionResult.setProgress((int) votePercentage);

        //to set text
        myProgressTextTv.setText((int) formerImage1Votes + "/" + (int) formerImage2Votes);
    }

    private void updateQuestionText(Poll currentPoll) {
        questionText = currentPoll.getQuestion();
        questionTextTV.setText(questionText);
    }

    public void getPollData() {
        pollsDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            Log.d(String.valueOf(this), "DocumentSnapshot data: " + documentSnapshot.getData());
                            currentPoll = documentSnapshot.toObject(Poll.class);
                            updateQuestionText(currentPoll);
                            imageId1 = currentPoll.getImage1ID();
                            imageId2 = currentPoll.getImage2ID();
                            getImage(imageId1, firstImg);
                            getImage(imageId2, secondImg);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(String.valueOf(this), "Unable to extract poll from Firebase", e);
                    }
                });
    }

    //Inspired by: https://firebase.google.com/docs/firestore/query-data/get-data
    public void getUnfilteredPollData() {
        Query publicPolls = pollsCollection.whereEqualTo(CONST.DB_SHOW_FOR_PUBLIC, true).limit(1);
        publicPolls.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(String.valueOf(this), "DocumentSnapshot data: " + document.getData());
                                currentPoll = document.toObject(Poll.class);
                                updateQuestionText(currentPoll);
                                imageId1 = currentPoll.getImage1ID();
                                imageId2 = currentPoll.getImage2ID();
                                getImage(imageId1, firstImg);
                                getImage(imageId2, secondImg);
                            }
                        } else {
                            Log.d(TAG, "Error getting unfiltered documents: ", task.getException());
                        }
                    }
                });
    }

//    public void getFriendsOnlyPollData() {
//        CollectionReference friendsCollection = db.collection(CONST.DB_USERS_COLLECTION);
//        Query friendsPolls = friendsCollection.whereEqualTo(CONST.DB_USER_ID, friendUserID);
//        Query publicPolls = pollsCollection.whereEqualTo(CONST.DB_SHOW_FOR_PUBLIC, true).limit(1);
//        publicPolls.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                Log.d(String.valueOf(this), "DocumentSnapshot data: " + document.getData());
//                                currentPoll = document.toObject(Poll.class);
//                                updateQuestionText(currentPoll);
//                                imageId1 = currentPoll.getImage1ID();
//                                imageId2 = currentPoll.getImage2ID();
//                                getImage(imageId1, firstImg);
//                                getImage(imageId2, secondImg);
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }

    public void getImage(String imageId, final ImageView imageView) {
        storageRef.child(STORAGE_IMAGES_PATH + imageId).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println(getResources().getString(R.string.GenericImageError) + exception.toString());
            }
        });
    }

    //Todo: Consider race condition
    //Inspired by: https://dzone.com/articles/cloud-firestore-read-write-update-and-delete
    private void incrementImageVotes(String imageVoteName) {
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
                        updateProgessBar();
                    }
                });
    }
}

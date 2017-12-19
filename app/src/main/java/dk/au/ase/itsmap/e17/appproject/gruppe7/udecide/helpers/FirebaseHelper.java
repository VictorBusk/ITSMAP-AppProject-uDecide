package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

public class FirebaseHelper {

    Poll poll;

    // Inspired by: https://firebase.google.com/docs/database/android/start/, https://www.youtube.com/watch?v=kDZYIhNkQoM
    // and https://firebase.google.com/docs/firestore/query-data/get-data
    // Attach a listener to read the data at our posts reference
    public Poll getPollData(DocumentReference pollsDocRef) {
        pollsDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                poll = documentSnapshot.toObject(Poll.class);
            }
        });
        return poll;
    }

}

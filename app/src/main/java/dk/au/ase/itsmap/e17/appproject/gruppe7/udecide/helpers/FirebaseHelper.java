package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.STORAGE_IMAGES_PATH;

public class FirebaseHelper extends AppCompatActivity {

    Poll poll;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Bitmap bmp;

    // Inspired by: https://firebase.google.com/docs/database/android/start/, https://www.youtube.com/watch?v=kDZYIhNkQoM
    // and https://firebase.google.com/docs/firestore/query-data/get-data
    // Attach a listener to read the data at our posts reference
    public Poll getPollData(DocumentReference pollsDocRef) {
        pollsDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null)
                {
                    Log.d(String.valueOf(this), "DocumentSnapshot data: " + documentSnapshot.getData());
                    poll = documentSnapshot.toObject(Poll.class);
                }
            }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(String.valueOf(this), "Unable to extract poll from Firebase", e);
                    }
                });
        return poll;
    }

    public Bitmap getImage(String imageId) {
        storageRef.child(STORAGE_IMAGES_PATH + "random.jpeg").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                System.out.println("Success1234");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
//                System.out.println(getResources().getString(R.string.GenericImageError) + exception.toString());
                System.out.println("Fejl1234");
            }
        });
        return bmp;
    }
}

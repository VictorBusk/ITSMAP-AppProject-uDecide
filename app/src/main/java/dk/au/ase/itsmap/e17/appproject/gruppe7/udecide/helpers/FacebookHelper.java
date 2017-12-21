package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.DB_USERS_COLLECTION;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.FACEBOOK_ID;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.FACEBOOK_NAME;
import static dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.CONST.FACEBOOK_PICTURE;

// https://developers.facebook.com/docs/android/graph
// https://firebase.google.com/docs/firestore/manage-data/add-data?authuser=0
public class FacebookHelper {

    private static final String TAG = "FacebookHelper";

    public static void getUserData(final FirebaseUser user) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String, Object> dbUser = new HashMap<>();

        GraphRequest meRequest = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, "meRequest:JSONObject" + object);
                        Log.i(TAG, "meRequest:GraphResponse" + response);
                        // TODO JS Application code for user
                        try {
                            dbUser.put(FACEBOOK_ID, object.getString("id"));
                            dbUser.put(FACEBOOK_NAME, object.getString("name"));
                            dbUser.put(FACEBOOK_PICTURE, object.getJSONObject("picture").getJSONObject("data").getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle meParameters = new Bundle();
        meParameters.putString("fields", "id,name,picture.type(large)");
        meRequest.setParameters(meParameters);

        GraphRequest myFriendsRequest = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.i(TAG, "newMyFriendsRequest:JSONArray" + objects);
                        Log.i(TAG, "newMyFriendsRequest:GraphResponse" + response);
                        // TODO JS Application code for users friends
                        List<String> facebookFriendsIds = new ArrayList<>();
                        for (int i = 0; i < objects.length(); i++) {
                            try {
                                facebookFriendsIds.add(objects.getJSONObject(i).getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dbUser.put("facebookFriends", facebookFriendsIds);
                        }

                    }
                });

        GraphRequestBatch batch = new GraphRequestBatch(meRequest, myFriendsRequest);
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch batch) {
                Log.i(TAG, "newMyFriendsRequest:GraphRequestBatch" + batch);
                // TODO JS Application code for when the batch finishes
                db.collection(DB_USERS_COLLECTION).document(user.getUid())
                        .set(dbUser)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        });
        batch.executeAsync();
    }
}

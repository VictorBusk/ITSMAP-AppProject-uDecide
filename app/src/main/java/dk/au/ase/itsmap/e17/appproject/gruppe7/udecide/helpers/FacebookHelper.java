package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.helpers;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FacebookHelper {

    private static final String TAG = "FacebookHelper";


    // https://developers.facebook.com/docs/android/graph
    public static void getUserData() {
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);




        GraphRequest meRequest = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, "meRequest:JSONObject" + object);
                        Log.i(TAG, "meRequest:GraphResponse" + response);
                        // TODO JS Application code for user
                    }
                });
        Bundle meParameters = new Bundle();
        meParameters.putString("fields", "id,name,link");
        meRequest.setParameters(meParameters);

        GraphRequest myFriendsRequest = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.i(TAG, "newMyFriendsRequest:JSONArray" + objects);
                        Log.i(TAG, "newMyFriendsRequest:GraphResponse" + response);
                        // TODO JS Application code for users friends
                    }
                });

        GraphRequestBatch batch = new GraphRequestBatch(meRequest, myFriendsRequest);
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch batch) {
                Log.i(TAG, "newMyFriendsRequest:GraphRequestBatch" + batch);
                // TODO JS Application code for when the batch finishes
            }
        });
        batch.executeAsync();

    }

    // https://developers.facebook.com/docs/android/graph
    public static void fetchFriendslistFromFB() {
        final List<String> friendsIds = new ArrayList<>();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/taggable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i(TAG, "fetchFriendslistFromFB:response " + response);
                        try {
                            JSONObject jsonObject = response.getJSONObject();
                            JSONArray jsonArrayData = jsonObject.getJSONArray("data");
                            if (jsonArrayData != null && jsonArrayData.length() > 0) {
                                for (int i = 0; i < jsonArrayData.length(); i++) {
                                    JSONObject jsonObjectFriend = jsonArrayData.optJSONObject(i);
                                    friendsIds.add(jsonObjectFriend.getString("name"));
                                    Log.i(TAG, "fetchFriendslistFromFB:response:data:name " + jsonObjectFriend.getString("name"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}

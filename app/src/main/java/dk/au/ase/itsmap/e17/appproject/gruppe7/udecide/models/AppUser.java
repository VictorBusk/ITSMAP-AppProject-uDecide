package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeppestaerk on 22/12/2017.
 */

public class AppUser {

    private String firebaseId;
    private String facebookId;
    private String displayName;
    private String photoUrl;
    private List<String> facebookFriendsIds = new ArrayList<>();

    public AppUser() {
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getFacebookFriendsIds() {
        return facebookFriendsIds;
    }

    public void setFacebookFriendsIds(List<String> facebookFriendsIds) {
        this.facebookFriendsIds = facebookFriendsIds;
    }
}

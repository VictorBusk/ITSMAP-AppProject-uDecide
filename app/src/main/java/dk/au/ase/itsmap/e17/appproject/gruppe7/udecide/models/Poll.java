package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models;
import android.graphics.Bitmap;

/**
 * Created by Andreas on 19/12/2017.
 */

public class Poll {
    private String question;
    private int notifyNumber;
    private boolean showForPublic;
    private boolean isArchived;
    private String image1ID;
    private String image2ID;
    private int image1Votes;
    private int image2Votes;
    private String userID;

    public Poll(String question, int notifyNumber, boolean showForPublic,
                 String image1, String image2, String fbUserId) {
        this.question = question;
        this.notifyNumber = notifyNumber;
        this.showForPublic = showForPublic;
        this.image1ID = image1;
        this.image2ID = image2;
        this.userID = fbUserId;
        this.image1Votes = 0;
        this.image2Votes = 0;
        this.isArchived = false;
    }

    public String getQuestion() {
        return question;
    }

    public int getNotifyNumber() {
        return notifyNumber;
    }

    public boolean getShowForPublic() {
        return showForPublic;
    }

    public String getImage1ID() {
        return image1ID;
    }

    public String getImage2ID() {
        return image2ID;
    }

    public int getImage1Votes() {
        return image1Votes;
    }

    public int getImage2Votes() {
        return image2Votes;
    }

    public String getUserID() {
        return userID;
    }

    public boolean getIsArchived() {
        return isArchived;
    }
}
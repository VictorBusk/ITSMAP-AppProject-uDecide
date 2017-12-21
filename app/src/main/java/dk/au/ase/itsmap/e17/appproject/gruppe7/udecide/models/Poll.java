package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models;
import android.graphics.Bitmap;

/**
 * Created by Andreas on 19/12/2017.
 */

public class Poll {
    private String question;
    private int notificationNumber;
    private boolean showForPublic;
    private boolean isArchived;
    private String image1Id;
    private String image2Id;
    private int image1Votes;
    private int image2Votes;
    private String fbUserId;

    public Poll(String question, int notificationNumber, boolean showForPublic,
                 String image1id, String image2Id, String fbUserId) {
        this.question = question;
        this.notificationNumber = notificationNumber;
        this.showForPublic = showForPublic;
        this.image1Id = image1Id;
        this.image2Id = image2Id;
        this.fbUserId = fbUserId;
        this.image1Votes = 0;
        this.image2Votes = 0;
        this.isArchived = false;
    }

    public String getQuestion() {
        return question;
    }

    public int getNotificationNumber() {
        return notificationNumber;
    }

    public boolean showForPublic() {
        return showForPublic;
    }

    public String getImage1Id() {
        return image1Id;
    }

    public String getImage2() {
        return image2Id;
    }

    public int getImage1Votes() {
        return image1Votes;
    }

    public int getImage2Votes() {
        return image2Votes;
    }

    public String getFBUserId() {
        return fbUserId;
    }

    public boolean isArchived() {
        return isArchived;
    }
}
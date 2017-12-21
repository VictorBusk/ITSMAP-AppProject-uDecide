package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models;
import android.graphics.Bitmap;

/**
 * Created by Andreas on 19/12/2017.
 */

public class Poll {
    private String question;
    private int notificationNumber;
    private boolean showForPublic;
    private boolean archived;
    private Bitmap image1;
    private Bitmap image2;
    private int image1Votes;
    private int image2Votes;
    private int userId;

    public Poll(String question, int notificationNumber, boolean showForPublic,
                 Bitmap image1, Bitmap image2, int image1Votes, int image2Votes, int userId) {
        this.question = question;
        this.notificationNumber = notificationNumber;
        this.showForPublic = showForPublic;
        this.image1 = image1;
        this.image2 = image2;
        this.image1Votes = image1Votes;
        this.image2Votes = image2Votes;
        this.userId = userId;
        this.archived = false;
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

    public Bitmap getImage1() {
        return image1;
    }

    public Bitmap getImage2() {
        return image2;
    }

    public int getImage1Votes() {
        return image1Votes;
    }

    public int getImage2Votes() {
        return image2Votes;
    }

    public int getUserId() {
        return userId;
    }
}
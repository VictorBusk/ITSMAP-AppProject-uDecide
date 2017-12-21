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
    private String image1id;
    private String image2id;
    private int image1Votes;
    private int image2Votes;
    private String fbUserId;

    public Poll(String question, int notificationNumber, boolean showForPublic,
                 String image1id, String image2id, String fbUserId) {
        this.question = question;
        this.notificationNumber = notificationNumber;
        this.showForPublic = showForPublic;
        this.image1id = image1id;
        this.image2id = image2id;
        this.fbUserId = fbUserId;
        this.image1Votes = 0;
        this.image2Votes = 0;
        this.archived = false;
    }
}
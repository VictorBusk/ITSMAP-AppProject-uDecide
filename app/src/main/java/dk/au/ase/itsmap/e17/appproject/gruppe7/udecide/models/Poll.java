package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models;

import java.io.Serializable;
import java.util.Date;

public class Poll implements Serializable {

    public String question;
    public int notifyNumber;
    public boolean showForPublic;
    public boolean archived;
    public String image1ID;
    public String image2ID;
    public int image1Votes;
    public int image2Votes;
    public String userID;
    public Date date;

    public Poll() {}

    public Poll(String question, int notifyNumber, boolean showForPublic,
                String image1ID, String image2ID, String fbUserId, Date date)
    {
        this.question = question;
        this.notifyNumber = notifyNumber;
        this.showForPublic = showForPublic;
        this.image1ID = image1ID;
        this.image2ID = image2ID;
        this.userID = fbUserId;
        this.image1Votes = 0;
        this.image2Votes = 0;
        this.archived = false;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getNotifyNumber() { return notifyNumber; }

    public void setNotifyNumber(int notifyNumber) {
        this.notifyNumber = notifyNumber;
    }

    public boolean isShowForPublic() {
        return showForPublic;
    }

    public void setShowForPublic(boolean showForPublic) {
        this.showForPublic = showForPublic;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getImage1ID() {
        return image1ID;
    }

    public void setImage1ID(String image1ID) {
        this.image1ID = image1ID;
    }

    public String getImage2ID() {
        return image2ID;
    }

    public void setImage2ID(String image2ID) {
        this.image2ID = image2ID;
    }

    public int getImage1Votes() {
        return image1Votes;
    }

    public void setImage1Votes(int image1Votes) {
        this.image1Votes = image1Votes;
    }

    public int getImage2Votes() {
        return image2Votes;
    }

    public void setImage2Votes(int image2Votes) {
        this.image2Votes = image2Votes;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
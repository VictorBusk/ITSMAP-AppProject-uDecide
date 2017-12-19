package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.Models;

import android.media.Image;

import java.util.HashMap;
import java.util.Map;

//Inspired by https://firebase.google.com/docs/database/android/read-and-write
public class Polls {
    public String question;
    public Image image1;
    public Image image2;
    public int image1Votes;
    public int image2Votes;
    public int userId;

    public Polls() {
        // Default constructor required for calls to DataSnapshot.getValue(Polls.class)
    }

    public Polls(String question, Image image1, Image image2, int image1Votes, int image2Votes, int userId) {
        this.question = question;
        this.image1 = image1;
        this.image2 = image2;
        this.image1Votes = image1Votes;
        this.image2Votes = image2Votes;
        this.userId = userId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("image1", image1);
        result.put("image2", image2);
        result.put("image1Votes", image1Votes);
        result.put("image2Votes", image2Votes);
        result.put("userID", userId);

        return result;
    }
}
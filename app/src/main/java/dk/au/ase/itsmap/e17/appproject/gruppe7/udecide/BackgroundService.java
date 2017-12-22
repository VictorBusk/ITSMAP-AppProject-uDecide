package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

public class BackgroundService extends AsyncTask<String, Void, String> {
    public String UserId;
    Context context;

    public BackgroundService() {
    }

    @Override
    protected String doInBackground(String... strings) {
        //Vi er for træt til at lave noget..

        UserId = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getUid();
        //Mangeler atl ave


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        double voteProcentes = (20 / (8 + 1)) * 100;
        sendPoll("test", voteProcentes);
    }

    protected void onPostExecute(String questionText, double voteForImage1, double voteForImage2) {
        double voteProcentes = (voteForImage1 / (voteForImage1 + voteForImage2)) * 100;
        sendPoll(questionText, voteProcentes);
    }

    private void sendPoll(String question, double vote) {
        Intent intent = new Intent(CONST.UPDATE_EVENT);
        intent.putExtra(CONST.QUESTION_TEXT, question);
        intent.putExtra(CONST.VOTE, vote);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}


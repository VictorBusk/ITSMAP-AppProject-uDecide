package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

public class MyQuestionsAdapter extends BaseAdapter {

    Context context;
    List<Poll> polls;
    Poll poll = null;

    public MyQuestionsAdapter(Context context, List<Poll> polls) {
        this.context = context;
        this.polls = polls;
    }

    @Override
    public int getCount() {
        if (polls != null) {
            return polls.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (polls != null) {
            return polls.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.question_list_item, null);
        }

        poll = polls.get(i);

        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        tvQuestion.setText(poll.getQuestion());
        ProgressBar progressBar = view.findViewById(R.id.QLWprogressBar);
        TextView tvVotes = view.findViewById(R.id.tvVotes);
        tvVotes.setText(poll.getImage1Votes() + "/" + poll.getImage2Votes());

        if (poll.getImage1Votes() == 0 && poll.getImage2Votes() == 0) {
            progressBar.setProgress(50);
        } else {
            double votePercentage = ((double) poll.getImage1Votes() /
                    ((double) poll.getImage1Votes() + (double) poll.getImage2Votes())) * 100;

            progressBar.setProgress((int) votePercentage);
        }

        return view;
    }
}

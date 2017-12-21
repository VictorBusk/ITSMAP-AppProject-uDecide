package dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.R;
import dk.au.ase.itsmap.e17.appproject.gruppe7.udecide.models.Poll;

//dette kode er inspirerert fra vores weather app der blev afleverert i handin 2.
/*
public class questionAdaptor extends BaseAdapter{

    private final Context context;
    private Poll pollModel;
    private ArrayList<Poll> Question;
    public questionAdaptor(context context, ArrayList<Poll> pollItemModels){
    this.context = context;
    this.
    }

    @Override
    public int getCount() {
        if (Question != null) {
            return Question.size();
        } else
            return 0;
    }

    @Override
    public View getView(int position, View customView, ViewGroup viewGroup) {
        if (customView == null){
            LayoutInflater Inflator = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            customView = Inflator.inflate(R.layout.question_list_item, null);

            pollModel = Question.get(position);
            String tempQuestion = pollModel.getQuestion();
            if(pollModel != null){
                TextView txtQuestion = customView.findViewById(R.id.tvQuestion);
                txtQuestion.setText(tempQuestion);
            }

        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        if(Question != null) {
            return Question.get(i);
        }
    }
}
*/

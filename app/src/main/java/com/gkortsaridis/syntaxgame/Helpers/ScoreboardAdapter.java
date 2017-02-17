package com.gkortsaridis.syntaxgame.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gkortsaridis.syntaxgame.R;

import java.util.ArrayList;

/**
 * Created by yoko on 14/02/2017.
 */

public class ScoreboardAdapter extends BaseAdapter {

    private static LayoutInflater inflater=null;
    Context context;
    ArrayList<String> usernames, scores;

    public ScoreboardAdapter(Context context, ArrayList<String> usernames, ArrayList<String> scores){
        this.context = context;
        this.usernames = usernames;
        this.scores = scores;

        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return usernames.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ListItem{
        TextView username,score;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ListItem listItem = new ListItem();
        View rowView = inflater.inflate(R.layout.listview_item_scoreboard, null);

        listItem.username = (TextView) rowView.findViewById(R.id.usernameTV);
        listItem.score = (TextView) rowView.findViewById(R.id.scoreTV);

        listItem.username.setText(usernames.get(i));
        listItem.score.setText(scores.get(i));

        return rowView;
    }
}

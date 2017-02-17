package com.gkortsaridis.braintag.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gkortsaridis.braintag.R;

import java.util.ArrayList;

/**
 * Created by yoko on 14/02/2017.
 */

public class PennPosTagAdapter extends BaseAdapter {

    private static LayoutInflater inflater=null;
    Context context;
    ArrayList<String> shortNames, longNames;

    public PennPosTagAdapter(Context context, ArrayList<String> shortNames, ArrayList<String> longNames){
        this.context = context;
        this.shortNames = shortNames;
        this.longNames = longNames;

        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return shortNames.size();
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
        TextView tag,description;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ListItem listItem = new ListItem();
        View rowView = inflater.inflate(R.layout.listview_item_tag_description, null);

        listItem.tag = (TextView) rowView.findViewById(R.id.tagTV);
        listItem.description = (TextView) rowView.findViewById(R.id.descriptionTV);

        listItem.description.setText(longNames.get(i));
        listItem.tag.setText(shortNames.get(i));

        return rowView;
    }
}

package com.gkortsaridis.braintag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gkortsaridis.braintag.Helpers.Helper;
import com.gkortsaridis.braintag.Helpers.PennPosTagAdapter;

public class PennPosTagListFragment extends Fragment {

    ListView listView;

    public PennPosTagListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_penn_pos_tag_list, container, false);

        listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(new PennPosTagAdapter(getActivity(), Helper.getShortTags(),Helper.getLongTags()));

        return view;
    }
}

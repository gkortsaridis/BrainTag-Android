package com.gkortsaridis.braintag;


import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gkortsaridis.braintag.Helpers.Helper;
import com.gkortsaridis.braintag.Helpers.ScoreboardAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ScoreboardFragment extends Fragment {

    ListView listView;
    ArrayList<String> usernames,scores;

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scoreboard, container, false);
        usernames = new ArrayList<>();
        scores = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.listview);
        getData();
        return view;
    }

    public void getData(){

        usernames.clear();
        scores.clear();

        JSONObject jsonHttpBody = new JSONObject();
        try {
            jsonHttpBody.put("ID","2");

            String entityBody = jsonHttpBody.toString();

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Accept", "application/json");
            client.addHeader("Content-Type", "application/json");
            getActivity().setRequestedOrientation(getActivity().getRequestedOrientation());

            StringEntity entity = new StringEntity(entityBody);
            client.post(getContext(), getResources().getString(R.string.server_url)+"scoreboard/", entity, "application/json", new AsyncHttpResponseHandler() {
                ProgressDialog pd;

                @Override
                public void onStart() {
                    // called before request is started
                    pd = new ProgressDialog(getActivity());
                    pd.setTitle("Please Wait...");
                    pd.setMessage("We are retrieving the scoreboard!");
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                    pd.cancel();
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    String resp = new String(response);
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        JSONArray scoreboard = jsonObject.getJSONArray("scoreboard");

                        for(int i=0; i<scoreboard.length(); i++){
                            usernames.add(scoreboard.getJSONObject(i).getString("Username"));
                            scores.add(scoreboard.getJSONObject(i).getString("Score"));
                        }

                        listView.setAdapter(new ScoreboardAdapter(getContext(), usernames,scores));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                    pd.cancel();
                    Log.i(Helper.getTag(),"FAIL "+statusCode);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}

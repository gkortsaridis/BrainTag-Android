package com.gkortsaridis.syntaxgame;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gkortsaridis.syntaxgame.Helpers.Helper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainMenuFragment extends Fragment {

    SharedPreferences sharedpreferences;
    String user;
    TextView myScoreTV,myRankTV;
    Button randomGame, chooseCategory;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        myScoreTV = (TextView) view.findViewById(R.id.myscore);
        myRankTV = (TextView) view.findViewById(R.id.myrank);
        randomGame = (Button) view.findViewById(R.id.playRandomGame);
        chooseCategory = (Button) view.findViewById(R.id.chooseCategory);

        randomGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ParagraphReviewActivity.class);
                intent.putExtra("fromCategory",false);
                intent.putExtra("category","");
                startActivity(intent);
            }
        });

        chooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CategoriesActivity.class);
                startActivity(intent);
            }
        });

        sharedpreferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        user = sharedpreferences.getString("user","");

        if(user.equals("")){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else{
            try {
                JSONObject userJson = new JSONObject(user);
                getScoreboard(userJson.getString("ID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return view;
    }

    public void getScoreboard(String id){

        JSONObject jsonHttpBody = new JSONObject();
        try {
            jsonHttpBody.put("ID",id);

            String entityBody = jsonHttpBody.toString();

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Accept", "application/json");
            client.addHeader("Content-Type", "application/json");
            getActivity().setRequestedOrientation(getActivity().getRequestedOrientation());

            StringEntity entity = new StringEntity(entityBody);
            client.post(getContext(), "http://83.212.118.131:3000/scoreboard/", entity, "application/json", new AsyncHttpResponseHandler() {
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
                        JSONObject respJson = new JSONObject(resp);

                        myScoreTV.setText(respJson.getInt("score")+"");
                        myRankTV.setText(Helper.ordinal(respJson.getInt("rank")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                    pd.cancel();
                    Log.i("RESPONSE","FAIL "+statusCode);
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

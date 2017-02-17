package com.gkortsaridis.braintag;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.gkortsaridis.braintag.Helpers.Helper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class GameResultActivity extends AppCompatActivity {

    TextView result;
    int wordCount,correctCount;
    String wrongs;
    ArrayList<String> wrongs_list;
    JSONObject wrongs_json;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            toolbar.setTitleTextColor(Color.WHITE);

            final Drawable upArrow = ContextCompat.getDrawable(getBaseContext(), R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);

            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        result = (TextView) findViewById(R.id.result);

        wordCount = getIntent().getExtras().getInt("wordCount");
        correctCount = getIntent().getExtras().getInt("correctCount");
        wrongs = getIntent().getExtras().getString("wrongs");

        result.setText("Correct : "+correctCount+" out of "+wordCount+" words");

        createWrongsJson(wrongs);
        postGameData();
    }

    public void postGameData(){

        JSONObject jsonHttpBody = new JSONObject();
        try {
            jsonHttpBody.put("user_id",new JSONObject(sharedPreferences.getString("user",null)).getString("ID"));
            jsonHttpBody.put("score",correctCount);
            jsonHttpBody.put("wrong_answers",wrongs_json);

            String entityBody = jsonHttpBody.toString();

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Accept", "application/json");
            client.addHeader("Content-Type", "application/json");
            setRequestedOrientation(getRequestedOrientation());

            StringEntity entity = new StringEntity(entityBody);
            client.post(getBaseContext(), getResources().getString(R.string.server_url)+"game_end/", entity, "application/json", new AsyncHttpResponseHandler() {
                ProgressDialog pd;

                @Override
                public void onStart() {
                        // called before request is started
                        pd = new ProgressDialog(GameResultActivity.this);
                        pd.setTitle("Please Wait...");
                        pd.setMessage("We are working our magic right now");
                        pd.setCancelable(false);
                        pd.show();
                    }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                    pd.cancel();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    String resp = new String(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                    pd.cancel();
                    Log.i(Helper.getTag(),"FAIL "+statusCode);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createWrongsJson(String wrongs){

        wrongs = wrongs.replace("[","");
        wrongs = wrongs.replace("]","");
        String[] strValues = wrongs.split(",");

        wrongs_list = new ArrayList<String>(Arrays.asList(strValues));
        wrongs_json = new JSONObject();

        try {
            ArrayList<String> shortTags = Helper.getShortTags();
            for(int i=0; i<shortTags.size(); i++){
                wrongs_json.put(shortTags.get(i),0);
            }


            for(int i=0; i<wrongs_list.size(); i++){
                String cur_error = wrongs_list.get(i).trim();
                if(wrongs_json.has(cur_error)) {
                    int new_val = wrongs_json.getInt(cur_error) + 1;

                    wrongs_json.remove(cur_error);
                    wrongs_json.put(cur_error, new_val);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

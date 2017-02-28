package com.gkortsaridis.braintag;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.View;
import android.widget.TextView;

import com.gkortsaridis.braintag.Helpers.Helper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.R.attr.category;

public class ParagraphReviewActivity extends AppCompatActivity {

    TextView paragraphTV,difficultyTV,categoryTV;
    JSONArray sentenceJsonArray;
    Toolbar toolbar;

    boolean fromCategories;
    String preConfiguredCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paragraph_review);

        fromCategories = getIntent().getExtras().getBoolean("fromCategory");
        preConfiguredCategory = getIntent().getExtras().getString("category");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            toolbar.setTitleTextColor(Color.WHITE);

            final Drawable upArrow = ContextCompat.getDrawable(getBaseContext(), R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);

            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        categoryTV = (TextView) findViewById(R.id.categoryTV);
        paragraphTV = (TextView) findViewById(R.id.paragraph);
        difficultyTV = (TextView) findViewById(R.id.difficultyTV);

        getData();
    }

    public void getData(){
        if(preConfiguredCategory.equals("_news_")){
            String news = Helper.getRandomNewsSource();
            getNews(news);
        }else if(preConfiguredCategory.equals("_input_")){
            String input = getIntent().getExtras().getString("input");
            tagData(input);
        }else{
            getWikiParagraph();
        }
    }

    public void getNews(final String source){
        final AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        setRequestedOrientation(getRequestedOrientation());

        String url = getResources().getString(R.string.newsapi_url)+"?source="+source+"&apiKey="+getResources().getString(R.string.newsapi_apiKey);
        client.get(getBaseContext(), url, new AsyncHttpResponseHandler() {
            ProgressDialog pd;

            @Override
            public void onStart() {
                // called before request is started
                pd = new ProgressDialog(ParagraphReviewActivity.this);
                pd.setTitle("Please Wait...");
                pd.setMessage("We are retrieving the lastest news");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {

                try {
                    JSONObject resp = new JSONObject(new String(response));
                    JSONArray articles = resp.getJSONArray("articles");
                    JSONObject article = articles.getJSONObject(0);
                    String title = article.getString("title");

                    categoryTV.setText("News from "+beautify(source));
                    tagData(title);
                    pd.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                pd.cancel();
                Log.i(Helper.getTag(),"FAIL "+statusCode);
                paragraphTV.setText("We are sorry\nWe could not retrieve a paragraph for you.\nPlease try again\n:(");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        });
    }

    public String beautify(String text){
        return text.replace('_',' ');
    }

    public void tagData(final String parData){
        final AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        setRequestedOrientation(getRequestedOrientation());

        client.get(getBaseContext(), getResources().getString(R.string.server_url)+"wrongs/", new AsyncHttpResponseHandler() {
            ProgressDialog pd;

            @Override
            public void onStart() {
                // called before request is started
                pd = new ProgressDialog(ParagraphReviewActivity.this);
                pd.setTitle("Please Wait...");
                pd.setMessage("We are working our magic right now");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                Log.i(Helper.getTag(),parData);
                try {
                    JSONObject wrongs = new JSONObject(new String(response));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("paragraph",parData);
                    jsonObject.put("wrongs",wrongs);

                    StringEntity entity = new StringEntity(jsonObject.toString());

                    client.post(getBaseContext(), "http://83.212.118.131:3000/tag", entity, "application/json", new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                            pd.cancel();
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            String resp = new String(response);

                            try {
                                JSONObject jsonResp = new JSONObject(resp);
                                String paragraph = jsonResp.getString("paragraph");
                                double difficulty = jsonResp.getDouble("difficulty");
                                JSONArray sentences = jsonResp.getJSONArray("sentences");

                                DecimalFormat df2 = new DecimalFormat(".##");

                                difficultyTV.setText("Difficulty "+df2.format(difficulty*100)+"%");
                                paragraphTV.setText(paragraph);

                                sentenceJsonArray = sentences;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            pd.cancel();
                            Log.i(Helper.getTag(),"FAIL "+statusCode);
                            Log.i(Helper.getTag(),new String(errorResponse));
                            paragraphTV.setText("We are sorry\nWe could not retrieve a paragraph for you.\nPlease try again\n:(");
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                pd.cancel();
                Log.i(Helper.getTag(),"FAIL "+statusCode);
                paragraphTV.setText("We are sorry\nWe could not retrieve a paragraph for you.\nPlease try again\n:(");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        });

    }

    public void getWikiParagraph(){

        final String category;
        if(!fromCategories) {
            category = Helper.getRandomCategory();
        }else{
            category = preConfiguredCategory;
        }

        categoryTV.setText(category);


        final AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        setRequestedOrientation(getRequestedOrientation());

        client.get(getBaseContext(), getResources().getString(R.string.server_url)+"wrongs/", new AsyncHttpResponseHandler() {
            ProgressDialog pd;

            @Override
            public void onStart() {
                // called before request is started
                pd = new ProgressDialog(ParagraphReviewActivity.this);
                pd.setTitle("Please Wait...");
                pd.setMessage("We are working our magic right now");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {

                try {
                    JSONObject wrongs = new JSONObject(new String(response));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("wiki_category",category);
                    jsonObject.put("wiki_language","en");
                    jsonObject.put("wrongs",wrongs);

                    StringEntity entity = new StringEntity(jsonObject.toString());

                    client.post(getBaseContext(), "http://83.212.118.131:3000", entity, "application/json", new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                            pd.cancel();
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            String resp = new String(response);

                            try {
                                JSONObject jsonResp = new JSONObject(resp);
                                String paragraph = jsonResp.getString("paragraph");
                                double difficulty = jsonResp.getDouble("difficulty");
                                JSONArray sentences = jsonResp.getJSONArray("sentences");

                                DecimalFormat df2 = new DecimalFormat(".##");

                                difficultyTV.setText("Difficulty "+df2.format(difficulty*100)+"%");
                                paragraphTV.setText(paragraph);

                                sentenceJsonArray = sentences;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            pd.cancel();
                            Log.i(Helper.getTag(),"FAIL "+statusCode);
                            paragraphTV.setText("We are sorry\nWe could not retrieve a paragraph for you.\nPlease try again\n:(");
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                pd.cancel();
                Log.i(Helper.getTag(),"FAIL "+statusCode);
                paragraphTV.setText("We are sorry\nWe could not retrieve a paragraph for you.\nPlease try again\n:(");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        });

    }

    public void refreshData(View view){
        getData();
    }

    public void startGame(View view){
        Intent intent = new Intent(ParagraphReviewActivity.this, GameActivity.class);
        intent.putExtra("paragraph",sentenceJsonArray.toString());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

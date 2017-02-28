package com.gkortsaridis.braintag;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.gkortsaridis.braintag.Helpers.CategoriesExpandableListViewAdapter;
import com.gkortsaridis.braintag.Helpers.Helper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CategoriesActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

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



        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        getCategories(getBaseContext());

    }

    public void getCategories(final Context context) {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");

        expandableListDetail = new HashMap<>();

        String url = context.getResources().getString(R.string.server_url)+"categories";
        client.get(context, url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, final byte[] response) {

                try {
                    JSONObject resp = new JSONObject(new String(response));
                    JSONArray categories = resp.getJSONArray("categories");
                    for(int i=0; i<categories.length(); i++){
                        JSONObject parentCategory = categories.getJSONObject(i);
                        JSONArray childrenCategories = parentCategory.getJSONArray("categories");
                        String parentCategoryName = parentCategory.getString("name");

                        List<String> childrenCategoriesList = new ArrayList<String>();
                        for(int j=0; j<childrenCategories.length(); j++){
                            childrenCategoriesList.add(childrenCategories.get(j).toString());
                        }
                        expandableListDetail.put(parentCategoryName, childrenCategoriesList);
                    }

                    displayCategories();

                    Log.i("RESPO",resp.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.i(Helper.getTag(),"FAIL "+statusCode);
            }
        });

    }

    public void displayCategories(){
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CategoriesExpandableListViewAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(CategoriesActivity.this, ParagraphReviewActivity.class);
                intent.putExtra("fromCategory",true);
                intent.putExtra("category",expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));
                startActivity(intent);

                return false;
            }
        });

        for(int i=0; i<expandableListDetail.size(); i++) {
            expandableListView.expandGroup(i);
        }

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

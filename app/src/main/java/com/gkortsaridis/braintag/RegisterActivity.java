package com.gkortsaridis.braintag;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gkortsaridis.braintag.Helpers.Helper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegisterActivity extends AppCompatActivity {

    EditText username,password,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username_ET);
        password = (EditText) findViewById(R.id.password_ET);
        email = (EditText) findViewById(R.id.email_ET);
    }

    public void register(View view){

        JSONObject jsonHttpBody = new JSONObject();
        try {
            jsonHttpBody.put("username",username.getText().toString());
            jsonHttpBody.put("password",password.getText().toString());
            jsonHttpBody.put("email",email.getText().toString());

            String entityBody = jsonHttpBody.toString();

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Accept", "application/json");
            client.addHeader("Content-Type", "application/json");
            setRequestedOrientation(getRequestedOrientation());

            StringEntity entity = new StringEntity(entityBody);
            client.post(getBaseContext(), getResources().getString(R.string.server_url)+"register/", entity, "application/json", new AsyncHttpResponseHandler() {
                ProgressDialog pd;

                @Override
                public void onStart() {
                    // called before request is started
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setTitle("Please Wait...");
                    pd.setMessage("We are registering you");
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                    pd.cancel();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    Toast.makeText(getBaseContext(),"You were successfully registered.",Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404
                    pd.cancel();
                    Log.i(Helper.getTag(),"FAIL "+statusCode);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            });

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }


    }
}

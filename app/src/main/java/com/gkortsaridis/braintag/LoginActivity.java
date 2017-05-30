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
import android.widget.TextView;

import com.gkortsaridis.braintag.Helpers.Helper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username_ET);
        password = (EditText) findViewById(R.id.password_ET);

        TextView t2 = (TextView) findViewById(R.id.registerTV);
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void login(View view){

        JSONObject jsonHttpBody = new JSONObject();
        try {
            jsonHttpBody.put("username",username.getText().toString());
            jsonHttpBody.put("password",password.getText().toString());

            String entityBody = jsonHttpBody.toString();

            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Accept", "application/json");
            client.addHeader("Content-Type", "application/json");
            setRequestedOrientation(getRequestedOrientation());

            StringEntity entity = new StringEntity(entityBody);
            client.post(getBaseContext(), getResources().getString(R.string.server_url)+"login/", entity, "application/json", new AsyncHttpResponseHandler() {
                ProgressDialog pd;

                @Override
                public void onStart() {
                    // called before request is started
                    pd = new ProgressDialog(LoginActivity.this);
                    pd.setTitle("Please Wait...");
                    pd.setMessage("We are logging you in!");
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final byte[] response) {
                    pd.cancel();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    String resp = new String(response);
                    sharedpreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("user",resp);
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                    final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setTitle("Log In Error");
                    alertDialogBuilder.setMessage("No user found with these credentials");
                    alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

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
}

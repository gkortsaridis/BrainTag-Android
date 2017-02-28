package com.gkortsaridis.braintag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TypeActivity extends AppCompatActivity {

    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        text = (EditText) findViewById(R.id.inputText);
    }

    public void startGame(View view){
        Intent intent = new Intent(this, ParagraphReviewActivity.class);
        intent.putExtra("fromCategory",true);
        intent.putExtra("category","_input_");
        intent.putExtra("input",text.getText().toString());
        startActivity(intent);
    }
}

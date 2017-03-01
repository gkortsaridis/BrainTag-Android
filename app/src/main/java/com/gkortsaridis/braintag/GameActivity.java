package com.gkortsaridis.braintag;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gkortsaridis.braintag.Helpers.Helper;
import com.gkortsaridis.braintag.Helpers.MyDragListener;
import com.gkortsaridis.braintag.Helpers.MyTouchListener;
import com.gkortsaridis.braintag.Helpers.PredicateLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    PredicateLayout l,l2;
    ArrayList<String> answers;
    ScrollView myScrollView;
    TextView sentenceCount;
    JSONArray jsonArray;
    int curSentence =0;
    Handler h1 = new Handler();

    int wordCount = 0;
    int correctCount = 0;
    String paragraph;
    boolean isChecking;

    ArrayList<String> wrongs;
    boolean practice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        practice = getIntent().getExtras().getBoolean("practice");
        paragraph = getIntent().getExtras().getString("paragraph");

        answers = new ArrayList<>();
        wrongs = new ArrayList<>();

        l = (PredicateLayout) findViewById(R.id.predicate_layout);
        l2 = (PredicateLayout) findViewById(R.id.predicate_layout_answers);
        myScrollView = (ScrollView) findViewById(R.id.myscrollview);
        sentenceCount = (TextView) findViewById(R.id.sentenceCount);

        try {
            jsonArray = new JSONArray(paragraph);
            jsonArray = Helper.reformatJson(jsonArray);
            displayData(jsonArray,curSentence);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void nextSentence(View view){

        isChecking = true;

        if(curSentence < jsonArray.length()-1){
            checkAnswers();

            h1.postDelayed(new Runnable(){
                public void run(){
                    //do something
                    curSentence++;
                    displayData(jsonArray,curSentence);
                }
            }, 3000);

        }else{
            checkAnswers();

            h1.postDelayed(new Runnable(){
                public void run(){
                    Intent intent = new Intent(GameActivity.this,GameResultActivity.class);
                    intent.putExtra("wordCount",wordCount);
                    intent.putExtra("correctCount",correctCount);
                    intent.putExtra("practice",practice);
                    intent.putExtra("wrongs",wrongs.toString());
                    startActivity(intent);
                }
            }, 3000);


        }


    }

    public void checkAnswers(){
        for(int i=0; i<l.getChildCount(); i++){
            View wordView = l.getChildAt(i);

            TextView theWordView = (TextView)wordView.findViewById(R.id.word);
            TextView posView = (TextView)wordView.findViewById(R.id.pos);
            TextView answerView = (TextView)wordView.findViewById(R.id.answer);
            if(posView != null){
                wordCount++;

                String pos = posView.getText().toString();
                String answer = answerView.getText().toString();

                if(!posView.getText().toString().equals(answerView.getText().toString())){
                    theWordView.setTextColor(Color.RED);
                    posView.setTextColor(Color.RED);
                    if(!pos.equals("--")){
                        wrongs.add(answer);
                    }
                }else{
                    correctCount++;
                    theWordView.setTextColor(Color.GREEN);
                    posView.setTextColor(Color.GREEN);
                }

            }
        }
    }

    public void displayData(JSONArray posdata, int cnt){
        isChecking = false;
        l.removeAllViews();
        l2.removeAllViews();
        answers.clear();

        try {
            sentenceCount.setText("Sentence "+(cnt+1)+"/"+posdata.length());

            JSONObject sentenceObj = posdata.getJSONObject(cnt);
            Log.i(Helper.getTag(),"Sentence : "+sentenceObj.getString("sentence"));
            JSONArray sentencePosTagging = sentenceObj.getJSONArray("posTagging");
            for(int i=0; i<sentencePosTagging.length(); i++){
                JSONObject wordObj = sentencePosTagging.getJSONObject(i);
                String word = wordObj.getString("word");
                final String pos  = wordObj.getString("textpos");


                if(Helper.determineWords(word)){
                    View noClickWord = createNoClickWord(wordObj.getString("word"));
                    l.addView(noClickWord);
                }else{

                    final View sentenceWord = createSentenceWord(word,pos);
                    sentenceWord.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!isChecking) {
                                String tempPos = ((TextView) sentenceWord.findViewById(R.id.pos)).getText().toString();
                                ((TextView) sentenceWord.findViewById(R.id.pos)).setText("--");
                                if (!tempPos.equals("--")) {
                                    View possibleAnswer = createPossibleAnswer(tempPos);
                                    l2.addView(possibleAnswer);
                                }
                            }
                        }
                    });

                    l.addView(sentenceWord);

                    answers.add(pos);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        addAllPossibleAnswers();

    }

    public void addAllPossibleAnswers(){
        Collections.shuffle(answers);

        for(int i=0; i<answers.size(); i++) {
            //Create Possible Answer, and add it to second list
            View possibleAnswer = createPossibleAnswer(answers.get(i));
            l2.addView(possibleAnswer);
        }


    }

    public View createNoClickWord(String word){

        FrameLayout container = new FrameLayout(this);
        final View inflatedLayout;

        inflatedLayout= getLayoutInflater().inflate(R.layout.single_word_unclickable, null, false);
        ((TextView)inflatedLayout.findViewById(R.id.word)).setText(word);
        container.addView(inflatedLayout);

        return container;
    }

    public View createSentenceWord(String word, String pos){

        FrameLayout container = new FrameLayout(this);
        final View inflatedLayout;

        inflatedLayout= getLayoutInflater().inflate(R.layout.single_word_two, null, false);
        inflatedLayout.setOnDragListener(new MyDragListener(getBaseContext()));
        ((TextView)inflatedLayout.findViewById(R.id.word)).setText(word);
        ((TextView)inflatedLayout.findViewById(R.id.answer)).setText(pos);
        ((TextView)inflatedLayout.findViewById(R.id.pos)).setText("--");
        container.addView(inflatedLayout);

        return container;
    }

    public View createPossibleAnswer(String pos){

        View inflatedLayout= getLayoutInflater().inflate(R.layout.answer_item, null, false);
        FrameLayout container = new FrameLayout(getBaseContext());
        inflatedLayout.setOnTouchListener(new MyTouchListener());
        inflatedLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if(dragEvent.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    myScrollView.scrollBy(0, (int)getResources().getDimension(R.dimen.scroll_by));
                }
                return false;
            }
        });


        ((TextView)inflatedLayout.findViewById(R.id.answer)).setText(Helper.getFullPosName(pos));
        container.removeAllViews();
        container.addView(inflatedLayout);

        return container;
    }

}

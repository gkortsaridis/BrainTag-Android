package com.gkortsaridis.syntaxgame.Helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by yoko on 06/02/2017.
 */

public class Helper {

    private static ArrayList<String> shortTags;
    private static ArrayList<String> longTags;

    public static String getTag() {
        String tag = "";
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals("getTag")) {
                tag = "("+ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber()+")";
            }
        }
        return tag;
    }

    private static void getTags(){

        shortTags = new ArrayList<>();
        longTags = new ArrayList<>();

        shortTags.add("CC");
        shortTags.add("CD");
        shortTags.add("DT");
        shortTags.add("EX");
        shortTags.add("FW");
        shortTags.add("IN");
        shortTags.add("JJ");
        shortTags.add("JJR");
        shortTags.add("JJS");
        shortTags.add("LS");
        shortTags.add("MD");
        shortTags.add("NN");
        shortTags.add("NNS");
        shortTags.add("NNP");
        shortTags.add("NNPS");
        shortTags.add("PDT");
        shortTags.add("POS");
        shortTags.add("PRP");
        shortTags.add("PRP$");
        shortTags.add("RB");
        shortTags.add("RBR");
        shortTags.add("RBS");
        shortTags.add("RP");
        shortTags.add("SYM");
        shortTags.add("TO");
        shortTags.add("UH");
        shortTags.add("VB");
        shortTags.add("VBD");
        shortTags.add("VBG");
        shortTags.add("VBN");
        shortTags.add("VBP");
        shortTags.add("VBZ");
        shortTags.add("WDT");
        shortTags.add("WP");
        shortTags.add("WP$");
        shortTags.add("WRB");

        longTags.add("Coordinating conjunction");
        longTags.add("Cardinal number");
        longTags.add("Determiner");
        longTags.add("Existential there");
        longTags.add("Foreign word");
        longTags.add("Preposition or subordinating conjunction");
        longTags.add("Adjective");
        longTags.add("Adjective, comparative");
        longTags.add("Adjective, superlative");
        longTags.add("List item marker");
        longTags.add("Modal");
        longTags.add("Noun, singular or mass");
        longTags.add("Noun, plural");
        longTags.add("Proper noun, singular");
        longTags.add("Proper noun, plural");
        longTags.add("Predeterminer");
        longTags.add("Possessive ending");
        longTags.add("Personal pronoun");
        longTags.add("Possessive pronoun");
        longTags.add("Adverb");
        longTags.add("Adverb, comparative");
        longTags.add("Adverb, superlative");
        longTags.add("Particle");
        longTags.add("Symbol");
        longTags.add("to");
        longTags.add("Interjection");
        longTags.add("Verb, base form");
        longTags.add("Verb, past tense");
        longTags.add("Verb, gerund or present participle");
        longTags.add("Verb, past participle");
        longTags.add("Verb, non-3rd person singular present");
        longTags.add("Verb, 3rd person singular present");
        longTags.add("Wh-determiner");
        longTags.add("Wh-pronoun");
        longTags.add("Possessive wh-pronoun");
        longTags.add("Wh-adverb");
    }

    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> sports = new ArrayList<String>();
        sports.add("Sports");
        sports.add("Basketball");
        sports.add("Football");
        sports.add("Soccer");
        sports.add("Tennis");
        sports.add("Cricket");
        sports.add("Table Tennis");
        sports.add("Volleyball");


        List<String> science = new ArrayList<String>();
        science.add("Science");
        science.add("Galileo_Galilei");
        science.add("Isaac_Newton");
        science.add("Charles_Darwin");
        science.add("Marie_Curie");
        science.add("Nobel_Prize");
        science.add("Nobel_Prize_in_Chemistry");
        science.add("Nobel_Prize_in_Physics");

        List<String> history = new ArrayList<String>();
        history.add("History");
        history.add("Prehistory");
        history.add("Mesopotamia");
        history.add("Alexander_the_Great");
        history.add("Wars_of_Alexander_the_Great");

        expandableListDetail.put("Sports", sports);
        expandableListDetail.put("Science", science);
        expandableListDetail.put("History", history);
        return expandableListDetail;
    }

    public static ArrayList<String> getShortTags(){
        getTags();
        return shortTags;
    }

    public static ArrayList<String> getLongTags(){
        getTags();
        return longTags;
    }

    public static String getRandomCategory(){
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Technology");
        categories.add("Science");
        categories.add("History");
        categories.add("Biology");
        categories.add("Greece");
        categories.add("Basketball");
        categories.add("Sports");
        categories.add("Soccer");


        Random rand = new Random();
        int value = rand.nextInt(categories.size());
        return categories.get(value);
    }

    public static String ordinal(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    public static boolean determineWords(String word){
        ArrayList<String> unclickableWords = new ArrayList<>();
        unclickableWords.add("\"");
        unclickableWords.add(",");
        unclickableWords.add(".");
        unclickableWords.add("'");
        unclickableWords.add(":");
        unclickableWords.add("?");
        unclickableWords.add("!");
        unclickableWords.add("(");
        unclickableWords.add(")");
        unclickableWords.add("[");
        unclickableWords.add("]");
        unclickableWords.add("{");
        unclickableWords.add("}");
        unclickableWords.add("-");
        unclickableWords.add("/");

        for(int i=0; i<unclickableWords.size(); i++){
            if(unclickableWords.get(i).equals(word)) return true;
        }

        return false;
    }

    public static JSONArray reformatJson(JSONArray jsonArray){
        JSONArray jsonArray1 = jsonArray;
        try {
            JSONObject sentenceObj = jsonArray1.getJSONObject(0);
            //Log.i("Sentence",sentenceObj.getString("sentence"));

            JSONArray sentencePosTagging = sentenceObj.getJSONArray("posTagging");
            sentencePosTagging.remove(0);
            sentencePosTagging.remove(sentencePosTagging.length()-1);

            for(int i=0; i<sentencePosTagging.length(); i++) {
                String word = sentencePosTagging.getJSONObject(i).getString("word");
                String pos = sentencePosTagging.getJSONObject(i).getString("textpos");
                //Log.i("!","Word : "+word+" pos : "+pos);
                if(pos.equals("''")){
                    sentencePosTagging.remove(i);
                }else if(word.equals("/")){
                    sentencePosTagging.remove(i);
                }else if(word.equals("'")){
                    sentencePosTagging.remove(i);
                }else if(word.equals("\\")){
                    sentencePosTagging.remove(i);
                }else if(word.equals("\"")){
                    sentencePosTagging.remove(i);
                }else if(word.equals('"')){
                    sentencePosTagging.remove(i);
                }else if(word.equals(".")){
                    sentencePosTagging.remove(i);
                }else if(word.equals(";")){
                    sentencePosTagging.remove(i);
                }else if(word.equals(":")){
                    sentencePosTagging.remove(i);
                }else if(word.equals("\\\"")){
                    sentencePosTagging.remove(i);
                }else if(word.equals("-")){
                    sentencePosTagging.remove(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray1;

    }

    public static String getFullPosName(String shortName){
        getTags();

        for(int i=0; i<shortTags.size(); i++){
            if(shortTags.get(i).equals(shortName)){
                return longTags.get(i);
            }
        }

        return null;
    }

    public static String getShortPosName(String longName){
        getTags();

        for(int i=0; i<longTags.size(); i++){
            if(longTags.get(i).equals(longName)){
                return shortTags.get(i);
            }
        }

        return null;
    }
}

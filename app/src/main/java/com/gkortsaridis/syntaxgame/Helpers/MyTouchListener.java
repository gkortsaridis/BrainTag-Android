package com.gkortsaridis.syntaxgame.Helpers;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yoko on 11/02/2017.
 */

public class MyTouchListener implements View.OnTouchListener {
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            return true;
        }else {
            view.setVisibility(View.VISIBLE);
            return false;
        }
    }
}
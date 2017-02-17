package com.gkortsaridis.syntaxgame.Helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gkortsaridis.syntaxgame.R;

/**
 * Created by yoko on 11/02/2017.
 */

public class MyDragListener implements View.OnDragListener {

    Context context;
    Drawable enterShape;

    public MyDragListener(Context context){
        this.context = context;
        enterShape = context.getResources().getDrawable(R.drawable.shape_droptarget);
    }


    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundDrawable(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundDrawable(null);
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
                String pos = ((TextView)view.findViewById(R.id.answer)).getText().toString();

                if(((TextView)v.findViewById(R.id.pos)).getText().toString().equals("--")){
                    ((TextView)v.findViewById(R.id.pos)).setText(Helper.getShortPosName(pos));
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                }

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundDrawable(null);
            default:
                break;
        }
        return true;
    }
}

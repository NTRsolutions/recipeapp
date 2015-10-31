package com.foodie.recipe.utility;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;

/**
 * Created by root on 16/8/15.
 */
public abstract class SpringOnTouchListener  implements View.OnTouchListener {
    View mView;
    Spring mSpring;
    boolean mCanSendClickEvent = false;
    private static double TENSION = 800;
    private static double DAMPER = 20; //friction

    public SpringOnTouchListener(View view){
        mView = view;
        mSpring = Utility.getSpringSystem().createSpring();

        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        mSpring.setSpringConfig(config);
        springTest(mView);
    }

    public void springTest(final View myView) {
        mSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                Log.d("SPRING", "onSpringUpdate ");
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                myView.setScaleX(scale);
                myView.setScaleY(scale);
            }

            @Override
            public void onSpringAtRest(Spring spring) {
                Log.d("SPRING", "onSpringAtRest");
            }

            @Override
            public void onSpringActivate(Spring spring) {
                Log.d("SPRING", "onSpringActivate");
                // Send On Click when spring settle down;
                if (mCanSendClickEvent) {
                    onClick(myView);
                    mCanSendClickEvent = false;
                }
            }

            @Override
            public void onSpringEndStateChange(Spring spring) {
                Log.d("SPRING", "onSpringEndStateChange");
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSpring.setEndValue(1);
                mCanSendClickEvent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                mCanSendClickEvent = false;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mSpring.setEndValue(0);
                break;
        }
        return true;
    }

    protected abstract void onClick(View view);
}

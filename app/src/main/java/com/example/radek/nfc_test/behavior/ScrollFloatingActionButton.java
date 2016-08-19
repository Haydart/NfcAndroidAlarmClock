package com.example.radek.nfc_test.behavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.example.radek.nfc_test.Constants;
import com.example.radek.nfc_test.R;

/**
 * Created by Radek on 2016-08-15.
 */
public class ScrollFloatingActionButton extends FloatingActionButton {

    String direction;

    public ScrollFloatingActionButton(Context context) {
        super(context);
    }

    public ScrollFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context,attrs);
    }

    private void initialize(Context context, AttributeSet attrs){
        TypedArray at = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButtonBehavior);
        direction = at.getString(R.styleable.FloatingActionButtonBehavior_direction);
        at.recycle();
    }

    public void showModified() {
        if (direction.equals("right"))
            showRight();
        else
            super.show();
    }

    public void hideModified() {
        if (direction.equals("right"))
            hideRight();
        else
            super.hide();
    }

    private void hideRight() {
        Animation hideAnimation = new TranslateAnimation(0, Constants.OUT_OFF_SCREEN_PX, 0, 0);
        hideAnimation.setDuration(Constants.CUSTOM_HIDE_SHOW_FAB_DURATION);
        setAnimation(hideAnimation);
        setVisibility(INVISIBLE);
    }

    private void showRight() {
        Animation showAnimation = new TranslateAnimation(Constants.OUT_OFF_SCREEN_PX, 0, 0, 0);
        showAnimation.setDuration(Constants.CUSTOM_HIDE_SHOW_FAB_DURATION);
        setAnimation(showAnimation);
        setVisibility(VISIBLE);
    }
}

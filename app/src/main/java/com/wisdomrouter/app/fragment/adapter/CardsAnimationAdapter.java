package com.wisdomrouter.app.fragment.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;



public class CardsAnimationAdapter extends AnimationAdapter{
    private final float mTranslationY = 400;

    private final float mRotationX = 15;

    private final long mDuration = 400;

    public CardsAnimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    protected long getAnimationDelayMillis() {
        return 30;
    }

    @Override
    protected long getAnimationDurationMillis() {
        return mDuration;
    }

    @Override
    public com.nineoldandroids.animation.Animator[] getAnimators(ViewGroup parent, View view) {
        return new Animator[] {
                ObjectAnimator.ofFloat(view, "translationY", mTranslationY, 0),
                ObjectAnimator.ofFloat(view, "rotationX", mRotationX, 0)
        };
    }
}


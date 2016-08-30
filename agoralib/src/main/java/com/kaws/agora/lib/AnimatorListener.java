package com.kaws.agora.lib;

import android.animation.Animator;

/**
 * Created by yangcai on 16/8/30.
 */
public class AnimatorListener implements Animator.AnimatorListener {
    public boolean isFinshed = true;

    @Override
    public void onAnimationStart(Animator animator) {
        isFinshed = false;
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        isFinshed = true;
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}

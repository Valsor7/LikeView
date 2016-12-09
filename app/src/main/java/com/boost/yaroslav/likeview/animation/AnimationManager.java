package com.boost.yaroslav.likeview.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.boost.yaroslav.likeview.animation.evaluators.FlyEvaluator;
import com.boost.yaroslav.likeview.animation.evaluators.ScaleEvaluator;
import com.boost.yaroslav.likeview.animation.like_objects.FlyObject;

import java.util.Random;

/**
 * Created by yaroslav on 06.12.16.
 */

public class AnimationManager {
    private static final String TAG = "AnimationManager";
    private static final int BOTTOM_OFFSET = 50;
    private Random mRandom;
    private int mWidth;
    private int mHeight;

    public AnimationManager() {
        mRandom = new Random();
    }

    public void setDisplaySize(int width, int height) {
        mWidth = width;
        mHeight = height / 2;
    }

    public void animateFlyObject(FlyObject flyObject) {
        createAnimation(flyObject);
    }

    private void createAnimation(final FlyObject flyObject) {
        ValueAnimator appearanceAnimator = getScaleAnimator(flyObject);
        appearanceAnimator.start();
        final ValueAnimator flyAnimator = getFlyAnimator(flyObject);
        ObjectAnimator alphaAnimator = getAlphaAnimator(flyObject);

//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playSequentially(appearanceAnimator, flyAnimator);
//        animatorSet.playTogether(flyAnimator, alphaAnimator);
//        animatorSet.start();
        flyObject.isAnimating = true;
    }

    private ValueAnimator getFlyAnimator(FlyObject flyObject) {
        FlyEvaluator evaluator = new FlyEvaluator(getPointF(2), getPointF(1));

        ValueAnimator animator = ValueAnimator.ofObject(
                evaluator,
                new PointF(mWidth / 2, mHeight - BOTTOM_OFFSET),
                new PointF(mRandom.nextInt(mWidth), 0)
        );
        animator.addUpdateListener(new FlyListener(flyObject));
        // todo use constants
        animator.setDuration(8000);
        return animator;
    }

    private ValueAnimator getScaleAnimator(FlyObject flyObject) {
        PointF imageSize = new PointF(flyObject.getLikeSize().x, flyObject.getLikeSize().y);
        ValueAnimator animator = ValueAnimator.ofObject(new ScaleEvaluator(), imageSize);
        animator.setDuration(1300);
        animator.addUpdateListener(new ScaleListener(flyObject));
        return animator;
    }

    private PointF getPointF(int scale) {
        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt((mWidth));
        pointF.y = mRandom.nextInt((mHeight)) / scale;
        return pointF;
    }

    private ObjectAnimator getAlphaAnimator(FlyObject flyObject) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofObject(flyObject, FlyObject.ALPHA_FIELD, new IntEvaluator(), 0);
        alphaAnimator.setInterpolator(new LinearInterpolator());
        alphaAnimator.setDuration(600);
        alphaAnimator.setStartDelay(5000);
        return alphaAnimator;
    }


    public static Bitmap resizeBitmap(Bitmap currentBitmap, Point newSize) {
        Log.d(TAG, "resizeBitmap: w " + currentBitmap.getWidth() + "h " + currentBitmap.getHeight());
        Log.i(TAG, "new : w " + newSize.x + " h " + newSize.y);

        if (newSize.equals(currentBitmap.getWidth(), currentBitmap.getHeight()))
            return currentBitmap;

        Bitmap bitmap = Bitmap.createScaledBitmap(currentBitmap, newSize.x, newSize.y, false);
        currentBitmap.recycle();
        return bitmap;
    }

    private class FlyListener implements ValueAnimator.AnimatorUpdateListener {

        FlyObject mFlyObject;

        public FlyListener(FlyObject flyObject) {
            mFlyObject = flyObject;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointF = (PointF) animation.getAnimatedValue();
            Log.d(TAG, "onAnimationUpdate: position : " + pointF.toString());
            mFlyObject.updatePosition(pointF);
            mFlyObject.isAnimating = true;
        }
    }

    private class ScaleListener implements ValueAnimator.AnimatorUpdateListener {
        private FlyObject mFlyObject;

        public ScaleListener(FlyObject flyObject) {
            mFlyObject = flyObject;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            PointF scalePointF = (PointF) valueAnimator.getAnimatedValue();
            Log.d(TAG, "onAnimationUpdate: scale " + scalePointF.toString());
            mFlyObject.setSizeAndTransform(scalePointF);
        }
    }
}

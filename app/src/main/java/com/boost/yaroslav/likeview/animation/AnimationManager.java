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
    private static final long FLY_TIME_MILLIS = 8000;
    private static final long SCALE_TIME_MILLIS = 200;
    private static final long ALPHA_TIME_MILLIS = 600;
    private static final long ALPHA_DELAY_MILLIS = 5000;
    private Random mRandom;
    private int mWidth;
    private int mHeight;

    public AnimationManager() {
        mRandom = new Random();
    }

    public void setDisplaySize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void animateFlyObject(FlyObject flyObject) {
        createAnimation(flyObject);
    }

    private void createAnimation(final FlyObject flyObject) {
        ValueAnimator appearanceAnimator = getScaleAnimator(flyObject);
        final ValueAnimator flyAnimator = getFlyAnimator(flyObject);
        ObjectAnimator alphaAnimator = getAlphaAnimator(flyObject);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(appearanceAnimator, flyAnimator, alphaAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                flyObject.isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                flyObject.isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    private ValueAnimator getFlyAnimator(FlyObject flyObject) {
        FlyEvaluator evaluator = new FlyEvaluator(getPointF(2), getPointF(1));

        ValueAnimator animator = ValueAnimator.ofObject(
                evaluator,
                new PointF(mWidth / 2, mHeight - BOTTOM_OFFSET),
                new PointF(mRandom.nextInt(mWidth), 0)
        );
        animator.addUpdateListener(new FlyListener(flyObject));
        animator.setDuration(FLY_TIME_MILLIS);
        return animator;
    }

    private ValueAnimator getScaleAnimator(final FlyObject flyObject) {
        PointF imageSize = new PointF(flyObject.getLikeSize().x, flyObject.getLikeSize().y);
        ValueAnimator animator = ValueAnimator.ofObject(new ScaleEvaluator(), imageSize);
        animator.setDuration(SCALE_TIME_MILLIS);
        animator.addUpdateListener(new ScaleListener(flyObject));
        animator.addListener(new ScaleAnimationStateListener(flyObject));
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
        alphaAnimator.setDuration(ALPHA_TIME_MILLIS);
        alphaAnimator.setStartDelay(ALPHA_DELAY_MILLIS);
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

    private class ScaleAnimationStateListener implements Animator.AnimatorListener{
        FlyObject mFlyObject;
        ScaleAnimationStateListener(FlyObject flyObject){
            mFlyObject = flyObject;
        }

        @Override
        public void onAnimationStart(Animator animator) {
            mFlyObject.isScaling = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mFlyObject.isScaling = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    private class FlyListener implements ValueAnimator.AnimatorUpdateListener {

        FlyObject mFlyObject;

        public FlyListener(FlyObject flyObject) {
            mFlyObject = flyObject;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointF = (PointF) animation.getAnimatedValue();
            mFlyObject.updatePosition(pointF);
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
            mFlyObject.setSizeAndTransform(scalePointF);
        }
    }
}

package com.boost.yaroslav.likeview.animation;

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
        mHeight = height;
    }

    public void animateFlyObject(FlyObject flyObject) {
        createAnimation(flyObject);
    }

    private void createAnimation(FlyObject flyObject) {
        ValueAnimator appearanceAnimator = setScaleAnimator(flyObject);
        ValueAnimator flyAnimator = setFlyAnimator(flyObject);
        ObjectAnimator alphaAnimator = setAlphaAnimator(flyObject);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(flyAnimator, appearanceAnimator);
        animatorSet.playSequentially(alphaAnimator);
        animatorSet.start();
    }

    private ValueAnimator setFlyAnimator(FlyObject flyObject) {
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

    private ValueAnimator setScaleAnimator(FlyObject flyObject) {
        ValueAnimator animator = ValueAnimator.ofObject(
                new ScaleEvaluator(),
                new PointF(100, 100),
                new PointF(
                        flyObject.getLikeSize().x,
                        flyObject.getLikeSize().y
                )
        );

        animator.setDuration(200);

        animator.addUpdateListener(new ScaleListener(flyObject));
        return animator;
    }

    private PointF getPointF(int scale) {
        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt((mWidth));
        pointF.y = mRandom.nextInt((mHeight)) / scale;
        return pointF;
    }

    private ObjectAnimator setAlphaAnimator(FlyObject flyObject) {
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
            Log.d("ScaleEvaluator", "onAnimationUpdate: " + scalePointF.toString());
            mFlyObject.setLikeSize(scalePointF);
        }
    }
}

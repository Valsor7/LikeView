package com.boost.yaroslav.likeview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by yaroslav on 06.12.16.
 */

public class AnimationManager {
    private static final String TAG = "AnimationManager";
    private Random mRandom;
    private List<FlyObject> mFlyObjectsList = new LinkedList<>();
    private int mWidth = 300;
    private int mHeight = 500;

    public AnimationManager() {
        mRandom = new Random();
    }

    public void setDisplaySize(int width, int height){
        mWidth = width;
        mHeight = height;
    }

    private void createAnimation(FlyObject flyObject){
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator flyAnimator = setFlyAnimator(flyObject);

        animatorSet.play(flyAnimator)
                .after(setStartAnimator(flyObject));
        animatorSet.start();
    }

    private ValueAnimator setFlyAnimator(FlyObject flyObject){
        FlyEvaluator evaluator = new FlyEvaluator(getPointF(2), getPointF(1));

        ValueAnimator animator = ValueAnimator.ofObject(
                evaluator,
                new PointF(mWidth / 2, mHeight - 100),
                new PointF(mRandom.nextInt(mWidth), 0)
        );
        animator.addUpdateListener(new FlyListener(flyObject));
        animator.setDuration(3000);
        return animator;
    }

    private ValueAnimator setStartAnimator(final FlyObject flyObject){
        ValueAnimator animator =  ValueAnimator.ofObject(
                new ScaleEvaluator(),
                new PointF(200, 200)
        );
        animator.setDuration(1500);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                flyObject.isFlying = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return animator;
    }

    private PointF getPointF(int scale) {
        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt((mWidth - 100));
        pointF.y = mRandom.nextInt((mHeight - 100)) / scale;
        return pointF;
    }

    public void animateFlyObject(FlyObject flyObject) {
        createAnimation(flyObject);
    }

    public static Bitmap resizeBitmap(Bitmap currentBitmap, PointF newSize){
        int width = currentBitmap.getWidth();
        int height = currentBitmap.getHeight();
        Log.d(TAG, "resizeBitmap: w " + width + "h " + height);
        Log.d(TAG, "new : w " + newSize.x + "h " + newSize.y);
//        float scaledW = newSize.x / width;
//        float scaledH = newSize.y / height;
//        Log.d(TAG, "scaled: w" + scaledW + "h " + scaledH);
//        Matrix scaleMatrix = new Matrix();
        //scaleMatrix.postScale(scaledW, scaledH);
        Bitmap bitmap = Bitmap.createScaledBitmap(currentBitmap, (int) newSize.x, (int) newSize.y, false);
//        Bitmap bitmap = Bitmap.createBitmap(currentBitmap, 0, 0, width, height, scaleMatrix, false);
        // TODO: 07.12.16 clear bitmap to prevent out of memory error
//        currentBitmap.recycle();
        return bitmap;
    }

    private class FlyListener implements ValueAnimator.AnimatorUpdateListener {

        FlyObject mFlyObject;

        public FlyListener(FlyObject flyObject){
            mFlyObject = flyObject;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointF = (PointF) animation.getAnimatedValue();
            mFlyObject.changeState(pointF.x, pointF.y);
//            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }


//    private class AnimEndListener extends AnimatorListenerAdapter {
//        private View target;
//
//        public AnimEndListener(View target) {
//            this.target = target;
//        }
//
//        @Override
//        public void onAnimationEnd(Animator animation) {
//            super.onAnimationEnd(animation);
//            removeView((target));
//        }
//    }
}

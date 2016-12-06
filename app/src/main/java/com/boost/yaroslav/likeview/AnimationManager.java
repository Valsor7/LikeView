package com.boost.yaroslav.likeview;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.PointF;

import java.util.Random;

/**
 * Created by yaroslav on 06.12.16.
 */

public class AnimationManager {

    private Random mRandom;
    private FlyObject mFlyObject;
    private Float mWidth = 300.0f;
    private Float mHeight = 500.0f;

    public AnimationManager(FlyObject flyObject) {
        mFlyObject = flyObject;
    }

//    private ValueAnimator setFlyAnimator(){
//        FlyEvaluator evaluator = new FlyEvaluator(getPointF(2), getPointF(1));
//
//        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mWidth - 20) / 2, mHeight - 20), new PointF(mRandom.nextInt(mWidth.intValue()), 0));
//        animator.addUpdateListener(new BezierListener(target));
//        animator.setTarget(target);
//        animator.setDuration(3000);
//        return animator;
//    }
//
//    private PointF getPointF(int i) {
//        PointF pointF = new PointF();
//        pointF.x = mRandom.nextInt((mWidth - 100));
//        pointF.y = mRandom.nextInt((mHeight - 100)) / scale;
//        return pointF;
//    }
//
//    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {
//
//        private FlyObject mFlyObject;
//
//        public BezierListener(FlyObject object) {
//             mFlyObject = object;
//        }
//
//        @Override
//        public void onAnimationUpdate(ValueAnimator animation) {
//            PointF pointF = (PointF) animation.getAnimatedValue();
//            target.setX(pointF.x);
//            target.setY(pointF.y);
//            target.setAlpha(1 - animation.getAnimatedFraction());
//        }
//    }
//
//
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

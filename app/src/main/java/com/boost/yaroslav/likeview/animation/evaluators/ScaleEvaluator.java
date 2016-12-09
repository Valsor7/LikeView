package com.boost.yaroslav.likeview.animation.evaluators;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by yaroslav on 07.12.16.
 */

public class ScaleEvaluator implements TypeEvaluator<PointF> {
    private static final String TAG = "ScaleEvaluator";
    @Override
    public PointF evaluate(float fraction, PointF startPointF, PointF endPointF) {
//        Log.d(TAG, "evaluate: fraction" + fraction);
//        Log.d(TAG, "evaluate: end " + endPointF.toString());
//        if (fraction == 0)
//            return new PointF(1, 1);
//
//        PointF pointF = new PointF();
//        pointF.x = (endPointF.x * fraction) / startPointF.x;
//        pointF.y = (endPointF.y * fraction) / startPointF.y;
//        Log.d(TAG, "evaluate: res " + pointF.toString());
        return new PointF(fraction, fraction);
    }
}

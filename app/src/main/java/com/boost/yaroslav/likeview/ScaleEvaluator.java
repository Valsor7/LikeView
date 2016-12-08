package com.boost.yaroslav.likeview;

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
        endPointF.x = startPointF.x + (endPointF.x * fraction);
        endPointF.y = startPointF.y + (endPointF.y * fraction);
        return endPointF;
    }
}

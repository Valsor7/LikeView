package com.boost.yaroslav.likeview;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by yaroslav on 07.12.16.
 */

public class ScaleEvaluator implements TypeEvaluator<PointF> {
    @Override
    public PointF evaluate(float fraction, PointF startPointF, PointF endPointF) {
        endPointF.x = startPointF.x + startPointF.x * fraction;
        endPointF.y = startPointF.y + startPointF.y * fraction;
        return endPointF;
    }
}

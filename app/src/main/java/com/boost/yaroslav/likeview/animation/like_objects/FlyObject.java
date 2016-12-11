package com.boost.yaroslav.likeview.animation.like_objects;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by yaroslav on 06.12.16.
 */

public class FlyObject {
    private static final String TAG = "FlyObject";
    public static final String ALPHA_FIELD = "alpha";
    private int id;
    private int alpha = 200;
    private Matrix stateMatrix;
    private PointF likeSize;
    public boolean isScaling;
    public boolean isAnimating;

    public FlyObject(int id) {
        this.id = id;
        likeSize = new PointF(70, 70);
        stateMatrix = new Matrix();
    }

    public void updatePosition(PointF position) {
        if (isScaling) {
            stateMatrix.postTranslate(position.x, position.y);
        } else {
            stateMatrix.setTranslate(position.x - likeSize.x / 2, position.y - likeSize.y);
        }
    }

    public void setSizeAndTransform(PointF size) {
        PointF shiftPoints = new PointF();
        shiftPoints.x = (size.x * likeSize.x) / 2;
        shiftPoints.y = (size.y * likeSize.y);
        stateMatrix.setScale(size.x, size.y);
        stateMatrix.postTranslate(-shiftPoints.x, -shiftPoints.y);
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getId() {
        return id;
    }

    public Matrix getStateMatrix() {
        return stateMatrix;
    }

    public PointF getLikeSize() {
        Log.d(TAG, "getLikeSize: " + likeSize.toString());
        return likeSize;
    }

    public int getAlpha() {
        return alpha;
    }
}

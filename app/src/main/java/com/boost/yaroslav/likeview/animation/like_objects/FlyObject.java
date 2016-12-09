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
    public final float timeToDeath = 0;
    private int id;
    private float aliveTime;
    private int alpha = 200;
    private Matrix stateMatrix;
    private PointF likeSize;
    public boolean isAnimating = false;

    public FlyObject(int id) {
        this.id = id;
        likeSize = new PointF(70, 70);
        stateMatrix = new Matrix();
    }

    public void updatePosition(PointF position) {
        aliveTime = position.y;
        Log.d(TAG, "updatePosition: ");
        stateMatrix.setTranslate(position.x, position.y);
    }

    public void setSizeAndTransform(PointF size) {
        //flyObject.updatePosition(new PointF(mWidth / 2, mHeight - BOTTOM_OFFSET));
        PointF centerPoints = new PointF();
        centerPoints.x = size.x * likeSize.x;
        centerPoints.y = size.y * likeSize.y;
        Log.d("MY", "center: " + centerPoints.toString());
        Log.d("MY", "setSizeAndTransform: " + size.toString());
        stateMatrix.setScale(size.x, size.y, centerPoints.x / 2, centerPoints.y / 2);
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return aliveTime > timeToDeath;
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

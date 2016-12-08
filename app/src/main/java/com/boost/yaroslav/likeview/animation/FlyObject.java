package com.boost.yaroslav.likeview.animation;

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
    private PointF position;
    private PointF likeSize;
    public boolean isAnimating;

    public FlyObject(int id) {
        this.id = id;
        likeSize = new PointF(70, 70);
    }

    public void updatePosition(PointF position){
        position.x -= likeSize.x / 2;
        position.y -= likeSize.y;
        aliveTime = position.y;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return aliveTime > timeToDeath;
    }


    public void setLikeSize(PointF likeSize) {
        this.likeSize = likeSize;
    }

    public PointF getLikeSize() {
        Log.d(TAG, "getLikeSize: " + likeSize.toString());
        return likeSize;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public PointF getPosition() {
        return position;
    }
}

package com.boost.yaroslav.likeview;

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
    boolean isFlying = false;


    public FlyObject(int id) {
        this.id = id;
        stateMatrix = new Matrix();
        likeSize = new PointF(10,10);
    }

    public void changeState(float x, float y){
        aliveTime = y;
        stateMatrix.reset();
        stateMatrix.setTranslate(x, y);
    }

    public void updatePosition(PointF position){
        aliveTime = position.y;
        this.position = position;
    }

    Matrix getState(){
        return stateMatrix;
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

package com.boost.yaroslav.likeview;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * Created by yaroslav on 06.12.16.
 */

public class FlyObject {
    private static final String TAG = "FlyObject";
    private final float timeToDeath = 0;
    private int id;
    private float aliveTime;
    private Matrix stateMatrix;
    private PointF likeSize;
    boolean isFlying;

    public FlyObject(int id) {
        this.id = id;
        stateMatrix = new Matrix();
        likeSize = new PointF(50,50);
    }

    public void changeState(float x, float y){
        aliveTime = y;
        stateMatrix.reset();
        stateMatrix.setTranslate(x, y);
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
        return likeSize;
    }
}

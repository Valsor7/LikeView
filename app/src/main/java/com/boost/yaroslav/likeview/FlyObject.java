package com.boost.yaroslav.likeview;

import android.graphics.Matrix;

/**
 * Created by yaroslav on 06.12.16.
 */

public class FlyObject {
    private static final String TAG = "FlyObject";
    private long timeToDeath;
    private int id;
    private long aliveTime;
    private Matrix stateMatrix;

    public FlyObject(long timeToDeath, int id) {
        this.timeToDeath = timeToDeath;
        this.id = id;
        stateMatrix = new Matrix();
    }

    private void increaseTime(long time){
        aliveTime += time;
    }

    public void changeState(){
        changeState(5);

    }

    public void changeState(long rate){
        increaseTime(rate);
        stateMatrix.reset();
        stateMatrix.setTranslate(0, aliveTime);
    }

    Matrix getState(){
        return stateMatrix;
    }

    public int getId() {
        return id;
    }

    public boolean isAlive() {
        return aliveTime < timeToDeath;
    }

}

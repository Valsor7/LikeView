package com.boost.yaroslav.likeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by yaroslav on 06.12.16.
 */

public class LikesSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "LikesSurfaceView";
    ConcurrentLinkedQueue<FlyObject> mFlyObjects = new ConcurrentLinkedQueue<>();
    Map<Integer, Bitmap> mLikesBitmapsMap = new LinkedHashMap<>();
    ConcurrentHashMap<Integer, Integer> mUniqueResourcesCounter = new ConcurrentHashMap<>();


    private DrawThread mDrawThread;

    public LikesSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mDrawThread = new DrawThread(getHolder());
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    public void onLikeAdded(int resId) {
        addImage(resId);
        mDrawThread.getmHandler().LikeIsPressed();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        mDrawThread.setRunning(false);
        while (retry) {
            try {
                mDrawThread.join();
                mDrawThread.getmHandler().sendClose();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void addImage(int resId) {
        if (!mLikesBitmapsMap.containsKey(resId)) {
            Bitmap flyImage = createImageFromResId(resId);
            if (flyImage != null) {
                FlyObject flyObject = new FlyObject(300, resId);
                mFlyObjects.add(flyObject);
                mLikesBitmapsMap.put(resId, flyImage);
                mUniqueResourcesCounter.put(resId, 1);
            }
        } else {
            FlyObject flyObject = new FlyObject(300, resId);
            mFlyObjects.add(flyObject);
            mUniqueResourcesCounter.put(resId, mFlyObjects.size());
        }
    }


    private Bitmap createImageFromResId(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        return bitmap;
    }

    class DrawThread extends Thread {
        SurfaceHolder mSurfaceHolder;
        boolean mRunning;
        private LikeHandlerThread mHandler;

        public DrawThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        public void setRunning(boolean isRunning) {
            mRunning = isRunning;
        }

        @Override
        public void run() {
            Looper.prepare();
            mHandler = new LikeHandlerThread(this);
            Looper.loop();
        }

        public void draw() {
            Canvas canvas;

            while (mRunning) {
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;

                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);

                    Iterator iterator = mFlyObjects.iterator();
                    while (iterator.hasNext()){
                        FlyObject flyObject = (FlyObject) iterator.next();

                        if (flyObject.isAlive()) {
                            Bitmap bitmap = mLikesBitmapsMap.get(flyObject.getId());
                            canvas.drawBitmap(bitmap, flyObject.getState(), new Paint(Paint.ANTI_ALIAS_FLAG));
                            flyObject.changeState();
                        } else {
                            int count;
                            iterator.remove();
                            count = mUniqueResourcesCounter.get(flyObject.getId());
                            if (--count == 0) {
                                mLikesBitmapsMap.remove(flyObject.getId());
                                mUniqueResourcesCounter.remove(flyObject.getId());
                            } else
                                mUniqueResourcesCounter.put(flyObject.getId(), count);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        if (mFlyObjects.isEmpty())
                            mRunning = false;
                    }
                }
            }
        }

        public void quit() {
            Looper.myLooper().quit();
        }

        public LikeHandlerThread getmHandler() {
            return mHandler;
        }
    }
}

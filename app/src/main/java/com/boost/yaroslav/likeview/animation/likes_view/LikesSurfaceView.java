package com.boost.yaroslav.likeview.animation.likes_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.boost.yaroslav.likeview.animation.AnimationManager;
import com.boost.yaroslav.likeview.animation.like_objects.FlyObject;

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
    private static final long DELAY_FPS = 1000 / 60;
    ConcurrentLinkedQueue<FlyObject> mFlyObjects = new ConcurrentLinkedQueue<>();
    Map<Integer, Bitmap> mLikesBitmapsMap = new LinkedHashMap<>();
    ConcurrentHashMap<Integer, Integer> mUniqueResourcesCounter = new ConcurrentHashMap<>();

    private AnimationManager mAnimationManager;
    private DrawThread mDrawThread;
    private Paint mLikePaint = new Paint();

    public LikesSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mAnimationManager = new AnimationManager();
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
        mLikePaint.setAntiAlias(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated: ");
        mAnimationManager.setDisplaySize(getWidth(), getHeight());
        mDrawThread = new DrawThread(getHolder());
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    public void onLikeAdded(int resId) {
        addImage(resId);
        mDrawThread.invalidate();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroying...: ");
        boolean isAlive = true;
        while (isAlive) {
            try {
                mDrawThread.close();
                mDrawThread.join();
                isAlive = false;
            } catch (InterruptedException e) {
                Log.d(TAG, "surfaceDestroyed: " + e.getMessage());
            }
        }
        Log.d(TAG, "surfaceDestroyed");
    }

    public void addImage(int resId) {
        FlyObject flyObject = null;
        Point imageSize = new Point(dpToPx(50), dpToPx(50));
        if (!mLikesBitmapsMap.containsKey(resId)) {
            Bitmap flyImage = createImageFromResId(resId, imageSize);
            if (flyImage != null) {
                flyObject = new FlyObject(resId);
                flyObject.setLikeSize(imageSize);
                mFlyObjects.add(flyObject);
                mLikesBitmapsMap.put(resId, flyImage);
                mUniqueResourcesCounter.put(resId, 1);
            }
        } else {
            Log.d(TAG, "addImage: image already exist" );
            flyObject = new FlyObject(resId);
            flyObject.setLikeSize(imageSize);
            mFlyObjects.add(flyObject);
            mUniqueResourcesCounter.put(resId, mUniqueResourcesCounter.get(resId) + 1);
        }
        mAnimationManager.animateFlyObject(flyObject);
    }


    private Bitmap createImageFromResId(int resId, Point newSize) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        if (newSize.equals(bitmap.getWidth(), bitmap.getHeight()))
            return bitmap;

        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newSize.x, newSize.y, false);
        bitmap.recycle();
        return newBitmap;
    }

    private int dpToPx(int size){
        return (int) (size * getResources().getDisplayMetrics().density);
    }

    class DrawThread extends Thread {

        private final SurfaceHolder mSurfaceHolder;
        private LikeActionHandler mDrawHandler;


        DrawThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: ");
            Looper.prepare();
            mDrawHandler = new LikeActionHandler(this);
            invalidate();
            Looper.loop();
        }

        void invalidate() {
            if (mDrawHandler != null) {
                mDrawHandler.removeMessages(LikeActionHandler.MSG_INVALIDATE);
                mDrawHandler.sendEmptyMessageDelayed(LikeActionHandler.MSG_INVALIDATE, DELAY_FPS);
            }
        }

        void close() {
            Log.d(TAG, "ThreadHandler sendClose: ");
            mDrawHandler.removeMessages(LikeActionHandler.MSG_INVALIDATE);
            mDrawHandler.sendEmptyMessage(LikeActionHandler.MSG_CLOSE);
        }

        void draw() {

            if (mFlyObjects.isEmpty()) {
                Log.w(TAG, "draw: emplty list");
                return;
            }
            Canvas canvas = null;

            try {
                long startTime = System.currentTimeMillis();
                canvas = mSurfaceHolder.lockCanvas();
                if (canvas == null) return;
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                long endTime = System.currentTimeMillis();
                Log.d(TAG, "draw: take " + (endTime - startTime) + " ms");
                    Iterator iterator = mFlyObjects.iterator();
                    while (iterator.hasNext()) {
                        FlyObject flyObject = (FlyObject) iterator.next();
                        if (flyObject.isAnimating) {
                            drawFlyingLike(canvas, flyObject);
                        } else {
                            clearCachedLike(iterator, flyObject);
                        }
                    }
            } finally {
                if (canvas != null) {
                    long startTime = System.currentTimeMillis();
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                    long endTime = System.currentTimeMillis();
                    Log.d(TAG, "unlock take" + (endTime - startTime) + " ms");
                    if (!mFlyObjects.isEmpty())
                        invalidate();
                }
            }

        }

        private void drawFlyingLike(final Canvas canvas, final FlyObject flyObject) {
            //Log.d(TAG, "drawFlyingLike: start");
            Bitmap bitmap = mLikesBitmapsMap.get(flyObject.getId());
            mLikePaint.setAlpha(flyObject.getAlpha());
            canvas.drawBitmap(bitmap, flyObject.getStateMatrix(), mLikePaint);
        }

        private void clearCachedLike(Iterator iterator, FlyObject flyObject) {
            Log.d(TAG, "clearCachedLike: ");
            iterator.remove();
            int decrementCount = mUniqueResourcesCounter.get(flyObject.getId()) - 1;
            if (decrementCount == 0) {
                Bitmap bitmap = mLikesBitmapsMap.remove(flyObject.getId());
                bitmap.recycle();
                mUniqueResourcesCounter.remove(flyObject.getId());
            } else
                mUniqueResourcesCounter.put(flyObject.getId(), decrementCount);
        }


        void quit() {
            Log.d(TAG, "quit");
            Looper.myLooper().quitSafely();
        }
    }
}

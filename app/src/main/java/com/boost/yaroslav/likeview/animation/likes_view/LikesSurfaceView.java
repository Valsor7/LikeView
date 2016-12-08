package com.boost.yaroslav.likeview.animation.likes_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.boost.yaroslav.likeview.animation.AnimationManager;
import com.boost.yaroslav.likeview.animation.FlyObject;

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
    private static final long DELAY_NEXT_FRAME = 1000 / 30;
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

    private void init(){
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
        mDrawThread.getmHandler().LikeIsPressed();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroying...: ");
        boolean retry = true;
        mDrawThread.setRunning(false);
        while (retry) {
            try {
                mDrawThread.getmHandler().removeMessages(LikeHandlerThread.MSG_LIKE_PRESSED);
                if (!mDrawThread.getmHandler().hasMessages(LikeHandlerThread.MSG_LIKE_PRESSED)) {
                    mDrawThread.getmHandler().sendClose();
                }
                mDrawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.d(TAG, "surfaceDestroyed: " + e.getMessage());
            }
        }
        Log.d(TAG, "surfaceDestroyed");
    }

    public void addImage(int resId) {
        FlyObject flyObject = null;
        if (!mLikesBitmapsMap.containsKey(resId)) {
            Bitmap flyImage = createImageFromResId(resId);
            if (flyImage != null) {
                flyObject = new FlyObject(resId);
                mFlyObjects.add(flyObject);
                mLikesBitmapsMap.put(resId, flyImage);
                mUniqueResourcesCounter.put(resId, 1);
            }
        } else {
            flyObject = new FlyObject(resId);
            mFlyObjects.add(flyObject);
//            mUniqueResourcesCounter.put(resId, mFlyObjects.size());
            mUniqueResourcesCounter.put(resId, mUniqueResourcesCounter.get(resId) + 1);
        }
        mAnimationManager.animateFlyObject(flyObject);
    }


    private Bitmap createImageFromResId(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        // todo check if image the same size and use point
        Bitmap scaledInitImage = AnimationManager.resizeBitmap(bitmap, new PointF(70, 70), true);
        return scaledInitImage;
    }

    class DrawThread extends Thread {
        private static final int MSG_INVALIDATE = 0;
        private final SurfaceHolder mSurfaceHolder;
        boolean mRunning;
        private LikeHandlerThread mHandler;
        private long timeNow;
        private long timePrevFrame;
        private long timeDelta;
        // todo check leaks
        private Handler mInvalidateHandler  = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_INVALIDATE:
                        draw();
                        sendEmptyMessageDelayed(MSG_INVALIDATE, 1000/ 30);
                        break;
                }
            }
        };

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

        void startRedraw()  {
            mInvalidateHandler.removeMessages(MSG_INVALIDATE);
            mInvalidateHandler.sendEmptyMessageDelayed(MSG_INVALIDATE, 1000/ 30);
        }
        void stopRedraw() {
            mInvalidateHandler.removeMessages(MSG_INVALIDATE);
        }

        // todo change to draw one time
        public void draw() {
            Canvas canvas;
            while (mRunning) {
                canvas = null;
                timeNow = System.currentTimeMillis();
                timeDelta = timeNow - timePrevFrame;
                if (timeDelta < DELAY_NEXT_FRAME){
                    try {
                        Thread.sleep(DELAY_NEXT_FRAME - timeDelta);
                    } catch (InterruptedException e){
                        Log.e(TAG, e.getMessage());
                    }
                }
                timePrevFrame = System.currentTimeMillis();
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    synchronized (mSurfaceHolder) {

                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
                        Iterator iterator = mFlyObjects.iterator();

                        while (iterator.hasNext()) {
                            FlyObject flyObject = (FlyObject) iterator.next();

                            if (flyObject.isAlive()) {
                                drawFlyingLike(canvas, flyObject);
                            } else {
                                clearCachedLikes(iterator, flyObject);
                            }
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

        private void drawFlyingLike(final Canvas canvas, final FlyObject flyObject) {
            Bitmap bitmap = mLikesBitmapsMap.get(flyObject.getId());
            // todo use matrix to scale and translate and rotate
            bitmap = AnimationManager.resizeBitmap(bitmap, flyObject.getLikeSize(), false);
            Log.d(TAG, "drawFlyingLike: " + flyObject.getPosition() + " anim " + flyObject.isAnimating);
            mLikePaint.setAlpha(flyObject.getAlpha());
            canvas.drawBitmap(bitmap, flyObject.getPosition().x, flyObject.getPosition().y, mLikePaint);
        }

        private void clearCachedLikes(Iterator iterator, FlyObject flyObject) {
            iterator.remove();
            int count = mUniqueResourcesCounter.get(flyObject.getId()) - 1;
            if (count == 0) {
                Bitmap bitmap = mLikesBitmapsMap.remove(flyObject.getId());
                bitmap.recycle();
                mUniqueResourcesCounter.remove(flyObject.getId());
            } else
                mUniqueResourcesCounter.put(flyObject.getId(), count);
        }

        public void quit() {
            Log.d(TAG, "quit");
            Looper.myLooper().quitSafely();
        }

        public LikeHandlerThread getmHandler() {
            return mHandler;
        }
    }
}

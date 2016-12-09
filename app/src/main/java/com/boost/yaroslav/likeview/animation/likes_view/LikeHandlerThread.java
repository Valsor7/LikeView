package com.boost.yaroslav.likeview.animation.likes_view;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by yaroslav on 06.12.16.
 */

class LikeHandlerThread extends Handler {
    static final int MSG_INVALIDATE = 0;
    static final int MSG_CLOSE = 3;
    private static final String TAG = "LikeHandlerThread";

    private WeakReference<LikesSurfaceView.DrawThread> mWeakThread;

    LikeHandlerThread(LikesSurfaceView.DrawThread thread) {
        this.mWeakThread = new WeakReference<>(thread);
    }

    @Override
    public void handleMessage(Message msg) {
        int what = msg.what;
        LikesSurfaceView.DrawThread drawThread = mWeakThread.get();
        if (drawThread == null) {
            Log.w(TAG, "ThreadHandler.handleMessage: weak ref is null");
            return;
        }
        switch (what) {
            case MSG_INVALIDATE:
                drawThread.draw();
                break;
            case MSG_CLOSE:
                drawThread.quit();
                break;
            default:
                throw new RuntimeException("unknown message " + what);
        }
    }
}

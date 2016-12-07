package com.boost.yaroslav.likeview;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by yaroslav on 06.12.16.
 */

public class LikeHandlerThread extends Handler {

    private static final String TAG = "LikeHandlerThread";
    private static final int MSG_LIKE_PRESSED = 1;
    private static final int MSG_CLOSE = 2;
    private WeakReference<LikesSurfaceView.DrawThread> mWeakThread;

    public LikeHandlerThread(LikesSurfaceView.DrawThread thread) {
        this.mWeakThread = new WeakReference<>(thread);
    }

    public void LikeIsPressed() {
        Log.d(TAG, "LikeIsPressed: ");
        sendMessage(obtainMessage(MSG_LIKE_PRESSED));
    }

    public void sendClose() {
        Log.d(TAG, "GLThreadHandler sendClose: ");
        sendMessage(obtainMessage(MSG_CLOSE));
    }


    @Override
    public void handleMessage(Message msg) {
        int what = msg.what;
        Log.d(TAG, "handleMessage: "+what);
        LikesSurfaceView.DrawThread drawThread = mWeakThread.get();
        if (drawThread == null) {
            Log.w(TAG, "ThreadHandler.handleMessage: weak ref is null");
            return;
        }
        drawThread.setRunning(true);
        switch (what) {
            case MSG_LIKE_PRESSED:
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

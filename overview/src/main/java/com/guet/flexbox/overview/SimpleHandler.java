package com.guet.flexbox.overview;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.arch.core.util.Function;
import androidx.core.os.HandlerCompat;

import com.facebook.litho.LithoHandler;

public class SimpleHandler
        extends Handler
        implements LithoHandler {

    SimpleHandler() {
        super(((Function<Void, Looper>) input -> {
            HandlerThread handlerThread = new HandlerThread(
                    Thread.currentThread().getStackTrace()[2]
                            .getClassName()
            );
            handlerThread.start();
            return handlerThread.getLooper();
        }).apply(null));
    }

    @Override
    public boolean isTracing() {
        return false;
    }

    @Override
    public void post(Runnable runnable, String tag) {
        HandlerCompat.postDelayed(this, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, runnable, 0);
    }

    @Override
    public void remove(Runnable runnable) {
        removeCallbacksAndMessages(runnable);
    }
}

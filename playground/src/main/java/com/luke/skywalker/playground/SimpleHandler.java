package com.luke.skywalker.playground;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.arch.core.util.Function;

 class SimpleHandler extends Handler {
     SimpleHandler(String name) {
         super(((Function<Void, Looper>) input -> {
             HandlerThread handlerThread = new HandlerThread(name);
             handlerThread.start();
             return handlerThread.getLooper();
         }).apply(null));
     }
 }

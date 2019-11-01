package com.guet.flexbox.overview;

import android.app.Activity;

import androidx.core.os.HandlerCompat;

import com.facebook.litho.LithoHandler;

import java.lang.ref.WeakReference;

import es.dmoral.toasty.Toasty;

class SimpleLithoHandler
        extends SimpleHandler
        implements LithoHandler {

    private WeakReference<Activity> context;

    SimpleLithoHandler(Activity context) {
        super("layout");
        this.context = new WeakReference<>(context);
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
                Activity activity = context.get();
                if (activity != null && !activity.isFinishing()) {
                    activity.runOnUiThread(() -> Toasty.error(activity, "布局解析出错").show());
                }
            }
        }, runnable, 0);
    }

    @Override
    public void remove(Runnable runnable) {
        removeCallbacksAndMessages(runnable);
    }
}

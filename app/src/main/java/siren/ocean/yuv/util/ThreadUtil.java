package siren.ocean.yuv.util;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

/**
 * 线程工具
 * Created by Siren on 2021/6/17.
 */
public class ThreadUtil {

    private static Handler mHandler = null;

    /**
     * 主线程handler
     *
     * @return
     */
    public static Handler handler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        } else {
            if (mHandler.getLooper() != Looper.getMainLooper()) {
                mHandler = new Handler(Looper.getMainLooper());
            }
        }
        return mHandler;
    }

    /**
     * 主线程执行
     *
     * @param runnable
     */
    public static void runOnMainThread(Runnable runnable) {
        handler().post(runnable);
    }

    /**
     * 主线程延迟执行
     *
     * @param runnable
     * @param time
     */
    public static void runOnMainThreadDelayed(Runnable runnable, long time) {
        if (runnable == null || time < 0) return;
        handler().postDelayed(runnable, time);
    }


    /**
     * 将传入的Runnable在子线程中运行
     */
    public static void runOnChildThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static void runOnChildThreadDelayed(Runnable runnable, long time) {
        new Thread(() -> {
            SystemClock.sleep(time);
            runnable.run();
        }).start();
    }
}

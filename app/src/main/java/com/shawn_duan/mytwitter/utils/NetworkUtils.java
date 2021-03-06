package com.shawn_duan.mytwitter.utils;

import android.util.Log;

import java.io.IOException;

/**
 * Created by sduan on 10/29/16.
 */

public class NetworkUtils {
    private final static String TAG = NetworkUtils.class.getSimpleName();
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            Log.e(TAG, "isOnline()" + e.getMessage().toString());
        } catch (InterruptedException e) {
            Log.e(TAG, "isOnline()" + e.getMessage().toString());
        }
        return false;
    }
}

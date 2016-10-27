package com.example.adorilson.testjavasymbolresolv;

import android.content.Context;
import android.os.Build;

/**
 * Created by adorilson on 25/10/16.
 */
public abstract class SleepPolicyHelper {
    private static SleepPolicyHelper _selector;

    public abstract void vSetSleepPolicy(Context context, int policy);

    public abstract int vGetSleepPolicy(Context context);

    public static int getSleepPolicy(Context context) {
        cacheSelector();
        return _selector.vGetSleepPolicy(context);
    }

    public static void setSleepPolicy(Context context, int policy) {
        cacheSelector();
        _selector.vSetSleepPolicy(context, policy);
    }

    public static void cacheSelector() {
        if (_selector == null) {
            if (Build.VERSION.SDK_INT >= 17)
                _selector = new JellyBeanSleepPolicy();
            else
                _selector = new LegacySleepPolicy();
        }
    }
}

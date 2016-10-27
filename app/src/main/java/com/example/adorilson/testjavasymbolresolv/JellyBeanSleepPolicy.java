package com.example.adorilson.testjavasymbolresolv;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.example.adorilson.testjavasymbolresolv.utility.NotifUtil;

/**
 * Created by adorilson on 25/10/16.
 */
public class JellyBeanSleepPolicy extends SleepPolicyHelper {
    @Override
    public void vSetSleepPolicy(Context context, int policy) {
        NotifUtil.show(context, getSleepPolicyString(policy), "Tap to set", getPendingIntent(context));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public int vGetSleepPolicy(Context context) {
        ContentResolver cr = context.getContentResolver();
        int policy;
        try {
            policy = Settings.Global.getInt(cr, Settings.Global.WIFI_SLEEP_POLICY);
        } catch (Settings.SettingNotFoundException e) {
            policy = -1;
        }
        return policy;
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pending = PendingIntent.getActivity(context, NotifUtil.getPendingIntentCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pending;
    }

    private String getSleepPolicyString(int policy) {
        String out = "Set Sleep Policy in Advanced to: ";

        switch (policy) {
            case Settings.Global.WIFI_SLEEP_POLICY_DEFAULT:
                out += "Never";
                break;

            case Settings.Global.WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED:
                out += "Never when Plugged";
                break;

            case Settings.Global.WIFI_SLEEP_POLICY_NEVER:
                out += "Always";
                break;
        }
        return out;
    }
}

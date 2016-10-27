package com.example.adorilson.testjavasymbolresolv.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by adorilson on 25/10/16.
 */
public class NotifUtil {
    public static final int STATNOTIFID = 2392;
    public static final String ACTION_POP_NOTIFICATION = "org.wahtod.wififixer.ACTION_POP_NOTIFICATION";
    public static final String PENDINGPARCEL = "PENDING_PARCEL";
    public static final String VSHOW_TAG = "VSHOW";
    public static final String STAT_TAG = "STATNOTIF";
    /*
     * for SSID status in status notification
     */
    public static final int SSID_STATUS_UNMANAGED = 3;
    public static final int SSID_STATUS_MANAGED = 7;
    public static final String SEPARATOR = " : ";
    /*
     * Intent Keys for Toast
     */
    public static final String TOAST_RESID_KEY = "TOAST_ID";
    public static final String TOAST_STRING_KEY = "TOAST_STRING";
    /*
     * Icon type for getIconfromSignal()
     */
    public static final int ICON_SET_SMALL = 0;
    public static final int ICON_SET_LARGE = 1;
    public static int NOTIFID = 2494;
    private static int pendingIntentRequest = 0;
    private static ArrayList<NotificationHolder> _notifStack = new ArrayList<NotificationHolder>();
    private static NotificationCompat.Builder mStatusBuilder;

    public static int getPendingIntentCode() {
        pendingIntentRequest++;
        return pendingIntentRequest;
    }

    public static int getStackSize() {
        return _notifStack.size();
    }

    private static Notification build(Context ctxt, NotificationCompat.Builder builder, StatusMessage in) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            return builder.build();

        Notification out = builder.build();
        out.iconLevel = in.getSignal();
        /*out.setLatestEventInfo(ctxt,
                ctxt.getString(R.string.app_name), in.getSSID()
                        + NotifUtil.SEPARATOR + in.getStatus(),
                PendingIntent.getActivity(ctxt, getPendingIntentCode(),
                        intent, PendingIntent.FLAG_UPDATE_CURRENT)
        );*/

        return out;
    }



   public static void show(Context context, String message,
                            String tickerText, PendingIntent contentIntent) {
        NotificationHolder holder = new NotificationHolder(tickerText, message, contentIntent);
        _notifStack.add(0, holder);
        NotificationCompat.Builder builder = generateBuilder(context, holder);
   }

    private static NotificationCompat.Builder generateBuilder(Context context, NotificationHolder holder) {
        /*
         * If contentIntent != NULL, parcel existing contentIntent
		 */
        Intent intent = new Intent(ACTION_POP_NOTIFICATION);
        /*
         * Create the delete intent, which pops the notification stack
         */
        PendingIntent delete = PendingIntent.getBroadcast(context, getPendingIntentCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (holder.contentIntent != null)
            intent.putExtra(PENDINGPARCEL, holder.contentIntent);
        else {
            throw new NullPointerException("Null contentIntent in NotifUtil.show");
        }
        /*
         * Set content intent to the prior intent, but with contentIntent as a parcel
         */
        PendingIntent content = PendingIntent.getBroadcast(context, getPendingIntentCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setTicker(holder.tickerText)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(content)
                .setDeleteIntent(delete)
                .setContentText(holder.message)
                .setAutoCancel(true);

        if (getStackSize() > 1)
            builder = largeText(context, builder);
        return builder;
    }

    private static NotificationCompat.Builder largeText(Context context, NotificationCompat.Builder builder) {
        if (Build.VERSION.SDK_INT < 11)
            return builder;
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getNotificationsAsString()));
        builder.setNumber(getStackSize());
        return builder;
    }

    public static StringBuilder getNotificationsAsString() {
        StringBuilder out = new StringBuilder();
        for (NotificationHolder holder : _notifStack) {
            out.append(holder.tickerText);
            out.append(" - ");
            out.append(holder.message);
            out.append("\n");
        }
        return out;
    }

    public static void pop(Context context) {
        if (getStackSize() < 2) {
            clearStack();
            return;
        }
        NotificationHolder holder = _notifStack.get(1);
        _notifStack.remove(0);
    }

    public static void cancel(Context context, String tag, int id) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(tag, id);
    }

    public static void cancel(Context context, int id) {
        cancel(context, VSHOW_TAG, id);
    }

    public static StatusMessage validateStrings(StatusMessage in) {
        if (in.getSSID() == null)
            in.setSSID(StatusMessage.EMPTY);
        if (in.getStatus() == null)
            in.setStatus(StatusMessage.EMPTY);
        return in;
    }

    public static void showToast(Context context, int resID) {
        showToast(context, context.getString(resID), Toast.LENGTH_LONG);
    }

    public static void showToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showToast(Context context, int resID, int delay) {
        showToast(context, context.getString(resID), delay);
    }

    public static void showToast(Context context, String message, int delay) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.setDuration(delay);
        toast.show();

    }

    public static void clearStack() {
        _notifStack.clear();
    }

    private static class NotificationHolder {
        String message;
        String tickerText;
        PendingIntent contentIntent;

        public NotificationHolder(String t, String m, PendingIntent p) {
            message = m;
            tickerText = t;
            contentIntent = p;
        }
    }
}
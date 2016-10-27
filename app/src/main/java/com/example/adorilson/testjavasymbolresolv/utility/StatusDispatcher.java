package com.example.adorilson.testjavasymbolresolv.utility;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by adorilson on 25/10/16.
 */
public class StatusDispatcher {
    public static StatusMessage _statusMessage;
    public static final String STATUS_ACTION = "org.wahtod.wififixer.ACTION.STATUS_UPDATE";
    public static final String REFRESH_INTENT = "org.wahtod.wififixer.STATUS_REFRESH";
    private static final int WIDGET_REFRESH_DELAY = 5000;
    private static final int WIDGET_REFRESH = 115;
    private static final int REFRESH = 1233;
    public static final String ACTION_WIDGET_NOTIFICATION = "org.wahtod.wififixer.WNOTIF";
    public static final String STATUS_DATA_KEY = "WDATA";
    private static WeakReference<Context> c;
    private static WeakReference<Handler> host;

    public StatusDispatcher(Context context, Handler myhost) {
        _statusMessage = new StatusMessage();
        c = new WeakReference<Context>(context);
        host = new WeakReference<Handler>(myhost);
    }

    public StatusMessage getStatusMessage() {
        return _statusMessage;
    }

    private BroadcastReceiver messagereceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Message in = messagehandler.obtainMessage(REFRESH);
            in.setData(intent.getExtras());
            messagehandler.sendMessage(in);
        }
    };

    /*
     * Essentially, a Leaky Bucket that throttles Widget messages to one every
     * WIDGET_REFRESH_DELAY seconds
     */
    private static Handler messagehandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (_statusMessage != null)
                switch (message.what) {
                    case WIDGET_REFRESH:
                            if (message.peekData() == null)
                                host.get().post(new Widget(_statusMessage));
                            else
                                host.get().post(
                                        new Widget(StatusMessage
                                                .fromMessage(message)));
                        break;

                    case REFRESH:
                        StatusMessage.updateFromMessage(_statusMessage, message);
                        host.get().post(new FastStatus(_statusMessage));
                        host.get().post(new StatNotif(_statusMessage));
                        if (!this.hasMessages(WIDGET_REFRESH))
                            this.sendEmptyMessageDelayed(WIDGET_REFRESH,
                                    WIDGET_REFRESH_DELAY);
                        break;
                }
        }
    };

    public static class StatNotif implements Runnable {
        private final StatusMessage message;

        public StatNotif(StatusMessage message) {
            this.message = message;
        }

        public void run() {
           //NotifUtil.addStatNotif(c.get(), message);
        }
    }

    ;

    private static class FastStatus implements Runnable {
        private final StatusMessage message;

        FastStatus(StatusMessage message) {
            this.message = message;
        }

        public void run() {
            Intent i = new Intent(STATUS_ACTION);
            i.putExtras(message.status);
            //BroadcastHelper.sendBroadcast(c.get(), i, true);
        }
    }

    ;

    public static class Widget implements Runnable {
        private final StatusMessage message;

        public Widget(StatusMessage message) {
            this.message = message;
        }

        public void run() {
            Intent intent = new Intent(ACTION_WIDGET_NOTIFICATION);
            intent.putExtra(STATUS_DATA_KEY, message.status);
            c.get().sendBroadcast(intent);
        }
    }

    ;

    public void clearQueue() {
        if (messagehandler.hasMessages(REFRESH))
            messagehandler.removeMessages(REFRESH);
        if (messagehandler.hasMessages(WIDGET_REFRESH))
            messagehandler.removeMessages(WIDGET_REFRESH);
    }

    public void unregister() {
        //BroadcastHelper.unregisterReceiver(c.get(), messagereceiver);
        clearQueue();
    }

    public void refreshWidget(StatusMessage n) {
        clearQueue();
        if (n == null)
            messagehandler.sendEmptyMessage(WIDGET_REFRESH);
        else {
            Message send = messagehandler.obtainMessage(WIDGET_REFRESH);
            send.setData(n.status);
            messagehandler.sendMessage(send);
        }
    }
}

package com.oneliang.android.common.broadcast;

import android.os.Handler;

import com.oneliang.Constants;
import com.oneliang.frame.broadcast.Message;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public final class BroadcastManager extends com.oneliang.frame.broadcast.BroadcastManager {

    private static final Logger logger = LoggerManager.getLogger(BroadcastManager.class);

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message osMessage) {
            if (osMessage.obj != null && osMessage.obj instanceof Message) {
                Message message = (Message) osMessage.obj;
                BroadcastManager.this.handleMessage(message);
            }
        };
    };

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (!this.messageQueue.isEmpty()) {
                    Message message = this.messageQueue.poll();
                    android.os.Message osMessage = android.os.Message.obtain();
                    osMessage.obj = message;
                    handler.sendMessage(osMessage);
                } else {
                    synchronized (this) {
                        this.wait();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(Constants.Base.EXCEPTION, e);
            Thread.currentThread().interrupt();
        }
    }
}

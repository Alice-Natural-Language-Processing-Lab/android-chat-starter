package com.likemessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by wangkai on 2016/6/4.
 */
public abstract class BaseActivity extends Activity{

    public static BaseActivity CURRENT_ACTIVITY = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CURRENT_ACTIVITY = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CURRENT_ACTIVITY = this;
    }

    private Handler activityAdapter = new Handler(){
        public void handleMessage(Message msg) {
            MessageHandle messageHandle = (MessageHandle) msg.obj;
            if (messageHandle == null){
                return;
            }
            messageHandle.handle(BaseActivity.this);
            super.handleMessage(msg);
        }
    };

    public static void sendMessage(MessageHandle handle){
        Message msg = new Message();
        msg.obj = handle;
        CURRENT_ACTIVITY.activityAdapter.sendMessage(msg);
    }

    public interface MessageHandle {
        public abstract void handle(BaseActivity activity);
    }
}

package com.likemessage.network;

import android.widget.ListView;

import com.gifisan.nio.common.DebugUtil;
import com.gifisan.nio.common.ThreadUtil;
import com.gifisan.nio.jms.JMSException;
import com.gifisan.nio.jms.TextMessage;
import com.gifisan.nio.jms.client.MessageConsumer;
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import in.co.madhur.chatbubblesdemo.ChatListAdapter;
import in.co.madhur.chatbubblesdemo.MainActivity;
import in.co.madhur.chatbubblesdemo.model.ChatMessage;

/**
 * Created by wangkai on 2016/4/19.
 */
public class MessageReceiver extends  Thread{

    private static MessageReceiver instance = null;

    private static AtomicBoolean started = new AtomicBoolean();

    public static void startReceive(MainActivity mainActivity, ArrayList<ChatMessage> chatMessages, ChatListAdapter listAdapter, ListView chatListView){
        if (started.compareAndSet(false,true)){
            instance = new MessageReceiver(mainActivity,chatMessages,listAdapter,chatListView);
            instance.start();
        }else{
            instance.mainActivity = mainActivity;
            instance.chatMessages = chatMessages;
            instance.listAdapter = listAdapter;
            instance.chatListView = chatListView;
        }
    }

    private MainActivity mainActivity = null;

    private ArrayList<ChatMessage> chatMessages = null;

    private ChatListAdapter listAdapter = null;

    private MessageReceiver(MainActivity mainActivity, ArrayList<ChatMessage> chatMessages, ChatListAdapter listAdapter, ListView chatListView) {
        this.mainActivity = mainActivity;
        this.chatMessages = chatMessages;
        this.listAdapter = listAdapter;
        this.chatListView = chatListView;
    }

    private ListView chatListView = null;

    public void run() {
        DebugUtil.info("========================start init receiver");
        for (;!LConstants.initComplete;){
            ThreadUtil.sleep(3000);
        }

        DebugUtil.info("========================init complete");
        for(;;){
            try {
                MessageConsumer messageConsumer = LConstants.messageConsumer;
                TextMessage _message = (TextMessage) messageConsumer.revice();
                String messageText = _message.getText();
                final ChatMessage message = new ChatMessage();
                message.setMessageText(messageText);
                message.setSend(false);
                message.setMessageTime(new Date().getTime());
                chatMessages.add(message);

                DBUtil.getDbUtil().saveMsg(message,LConstants.FRIEND_PHONE,LConstants.THIS_PHONE);

                mainActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        listAdapter.notifyDataSetChanged();

                        chatListView.setSelection(listAdapter.getCount() - 1);
                    }
                });
            } catch (JMSException e) {
                //FIXME network exception
                e.printStackTrace();
            }
        }
    }
}

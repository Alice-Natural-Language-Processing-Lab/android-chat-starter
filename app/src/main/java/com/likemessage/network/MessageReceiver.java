package com.likemessage.network;

import android.widget.ListView;

import com.gifisan.nio.Encoding;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.ThreadUtil;
import com.gifisan.nio.plugin.jms.ByteMessage;
import com.gifisan.nio.plugin.jms.JMSException;
import com.gifisan.nio.plugin.jms.Message;
import com.gifisan.nio.plugin.jms.client.MessageConsumer;
import com.gifisan.nio.plugin.jms.client.OnMessage;
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
public class MessageReceiver extends Thread {

    private static MessageReceiver instance = null;

    private static AtomicBoolean started = new AtomicBoolean();

    private Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    public static void startReceive(MainActivity mainActivity, ArrayList<ChatMessage> chatMessages, ChatListAdapter listAdapter, ListView chatListView) {
        if (started.compareAndSet(false, true)) {
            instance = new MessageReceiver(mainActivity, chatMessages, listAdapter, chatListView);
            instance.start();
        } else {
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
        logger.info("========================start init receiver");
        for (; !LConstants.initComplete; ) {
            ThreadUtil.sleep(3000);
        }

        logger.info("========================init complete");


        try {
            MessageConsumer messageConsumer = LConstants.messageConsumer;
            messageConsumer.receive(new OnMessage() {
                public void onReceive(Message message) {
                    logger.info("========================MessageReceived:" + message.toString());

                    ByteMessage byteMessage = (ByteMessage)message;

                    String messageText = new String(byteMessage.getByteArray(), Encoding.UTF8);

                    final ChatMessage charMessage = new ChatMessage();
                    charMessage.setMessageText(messageText);
                    charMessage.setSend(false);
                    charMessage.setMessageTime(new Date().getTime());
                    chatMessages.add(charMessage);

                    DBUtil.getDbUtil().saveMsg(charMessage, LConstants.FRIEND_PHONE, LConstants.THIS_PHONE);

                    mainActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            listAdapter.notifyDataSetChanged();

                            chatListView.setSelection(listAdapter.getCount() - 1);
                        }
                    });
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

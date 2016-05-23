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
import com.likemessage.PhoneActivity;
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;
import com.likemessage.message.MessageBean;
import com.likemessage.message.MessageListAdpter;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import in.co.madhur.chatbubblesdemo.ChatListAdapter;
import in.co.madhur.chatbubblesdemo.model.ChatMessage;

/**
 * Created by wangkai on 2016/4/19.
 */
public class MessageReceiver extends Thread {

    private static MessageReceiver instance = null;

    private static AtomicBoolean started = new AtomicBoolean();

    private Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    public static MessageReceiver getInstance(){
        return instance;
    }

    public static void startReceive(PhoneActivity mainActivity) {
        if (started.compareAndSet(false, true)) {
            instance = new MessageReceiver();
            instance.start();
        }
    }

    private ListView chatListView = null;

    private ListView messageListView = null;

    private MessageListAdpter messageListAdpter = null;

    private ChatListAdapter chatListAdapter = null;

    private ArrayList<ChatMessage> chatList = null;

    private ArrayList<MessageBean> messageList = null;

    private MessageReceiver() {
    }

    public void setChatListView(ListView listView){
        this.chatListView = listView;
        this.chatListAdapter = (ChatListAdapter) chatListView.getAdapter();
        this.chatList = chatListAdapter.getChatList();
    }

    public void setMessageListView(ListView listView){
        this.messageListView = listView;
        this.messageListAdpter = (MessageListAdpter) listView.getAdapter();
        this.messageList = messageListAdpter.getMessageList();
    }

    public void run() {
        logger.info("========================start init receiver");
        for (; !LConstants.initComplete; ) {
            ThreadUtil.sleep(500);
        }

        logger.info("========================init complete");

        try {
            MessageConsumer messageConsumer = LConstants.messageConsumer;
            messageConsumer.receive(new OnMessage() {
                public void onReceive(Message message) {
                    logger.info("========================MessageReceived:" + message.toString());

                    ByteMessage byteMessage = (ByteMessage)message;

                    String messageText = new String(byteMessage.getByteArray(), Encoding.UTF8);

                    final ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessageText(messageText);
                    chatMessage.setSend(false);
                    chatMessage.setMessageTime(new Date().getTime());

                    String fromNO = byteMessage.getQueueName();

                    if (chatListAdapter != null){

                        String chatNO = chatListAdapter.getChatNO();

                        logger.info("___________________________chatNO,{}",chatNO);

                        if (fromNO.equals(chatListAdapter.getChatNO())){
                            chatListAdapter.addChat(chatMessage);
                        }
                    }

                    if (messageListAdpter != null){

                        //TODO add to messageList
                    }


                    DBUtil.getDbUtil().saveMsg(chatMessage,fromNO, LConstants.THIS_PHONE);
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }





}

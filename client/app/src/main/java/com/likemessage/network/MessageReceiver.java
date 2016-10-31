package com.likemessage.network;

import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.common.ThreadUtil;
import com.generallycloud.nio.extend.plugin.jms.MQException;
import com.generallycloud.nio.extend.plugin.jms.MapMessage;
import com.generallycloud.nio.extend.plugin.jms.client.impl.FixedMessageConsumer;
import com.generallycloud.nio.extend.plugin.jms.client.impl.OnMappedMessage;
import com.likemessage.BaseActivity;
import com.likemessage.PhoneActivity;
import com.likemessage.bean.B_Contact;
import com.likemessage.bean.T_MESSAGE;
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;
import com.likemessage.message.MessageListAdpter;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import in.co.madhur.chatbubblesdemo.ChatActivity;
import in.co.madhur.chatbubblesdemo.ChatListAdapter;

/**
 * Created by wangkai on 2016/4/19.
 */
public class MessageReceiver extends Thread {

    private static MessageReceiver instance = null;

    private static AtomicBoolean started = new AtomicBoolean();

    private Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    public static MessageReceiver getInstance() {
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

    private List<T_MESSAGE> chatList = null;

    private List<T_MESSAGE> messageList = null;

    private ChatActivity chatActivity = null;

    private PhoneActivity phoneActivity = null;

    private MessageReceiver() {

    }

    public void setChatListView(ListView listView) {
        this.chatListView = listView;
        this.chatListAdapter = (ChatListAdapter) chatListView.getAdapter();
        this.chatList = chatListAdapter.getChatList();
    }

    public void setMessageListView(ListView listView) {
        this.messageListView = listView;
        this.messageListAdpter = (MessageListAdpter) listView.getAdapter();
        this.messageList = messageListAdpter.getMessageList();
    }

    public void run() {
        logger.info("========================start init receiver");

        ConnectorManager manager = LConstants.getConnectorManager();

        for (; !manager.isConnected(); ) {
            ThreadUtil.sleep(500);
        }

        logger.info("========================init complete");

        FixedMessageConsumer messageConsumer = manager.MESSAGE_CONSUMER;

        messageConsumer.listen("lMessage", new OnMappedMessage() {
            public void onReceive(MapMessage message) {

                logger.info("========================MessageReceived:" + message.toString());

                Integer fromUserID = message.getIntegerParameter("fromUserID");

                B_Contact contact = LConstants.getBContactByUserID(fromUserID);

                logger.info("_______________________contact:{}", JSONObject.toJSON(contact));

                final T_MESSAGE tMessage = new T_MESSAGE();

                tMessage.setToUserID(LConstants.THIS_USER_ID);
                tMessage.setFromUserID(fromUserID);
                tMessage.setMsgDate(message.getTimestamp());
                tMessage.setMsgType(0);
                tMessage.setMessage(message.getParameter("message"));
                tMessage.setSend(false);
                tMessage.setDeleted(false);

                if (chatListAdapter != null) {

                    Integer toUserID = chatListAdapter.getChatUserID();

                    logger.info("___________________________chat_user,{}", contact.getBackupName());

                    if (fromUserID == toUserID) {
                        chatListAdapter.addChat(tMessage);

                        BaseActivity.sendMessage(new BaseActivity.MessageHandle() {
                            @Override
                            public void handle(BaseActivity activity) {
                                chatListAdapter.notifyDataSetChangedSelectTop();
                            }
                        });
                    }
                }

                if (messageListAdpter != null) {

                    //phoneActivity.notifyDataSetChanged();
                    //TODO add to messageList
                }

                DBUtil.getDbUtil().saveMsg(tMessage);
            }
        });

        try {
            messageConsumer.receive(null);
        } catch (MQException e) {
            e.printStackTrace();
        }

    }


    public ChatActivity getChatActivity() {
        return chatActivity;
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
    }

    public PhoneActivity getPhoneActivity() {
        return phoneActivity;
    }

    public void setPhoneActivity(PhoneActivity phoneActivity) {
        this.phoneActivity = phoneActivity;
    }
}

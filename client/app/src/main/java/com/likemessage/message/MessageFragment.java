package com.likemessage.message;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.likemessage.BaseActivity;
import com.likemessage.PhoneActivity;
import com.likemessage.bean.B_Contact;
import com.likemessage.bean.T_MESSAGE;
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;
import com.likemessage.network.MessageReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.co.madhur.chatbubblesdemo.ChatActivity;
import in.co.madhur.chatbubblesdemo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment {

    private Logger logger = LoggerFactory.getLogger(MessageFragment.class);

    private View view = null;

    private List<T_MESSAGE> messageList;

    private ListView messageListView;

    private MessageListAdpter messageListAdaptor;

    private PhoneActivity activity = null;

    public MessageListAdpter getMessageListAdaptor() {
        return messageListAdaptor;
    }

    public MessageFragment() {
    }

    public void setPhoneActivity(PhoneActivity activity) {
        this.activity = activity;
    }

    public List<T_MESSAGE> getMessageList() {
        return messageList;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        logger.info("_______________________________onActivityCreated");

        messageListView = (ListView) findViewById(R.id.messageListView);

        messageList = DBUtil.getDbUtil().findTop(LConstants.THIS_USER_ID);

        messageList = filterMessage(messageList);

        messageListAdaptor = new MessageListAdpter(activity, messageListView, messageList);

        messageListView.setAdapter(messageListAdaptor);

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessageListAdpter adapter = (MessageListAdpter) adapterView.getAdapter();
                T_MESSAGE messageBean = adapter.getItem(i);
                Intent intent = new Intent(activity, ChatActivity.class);
                Integer toUserID = messageBean.isSend() ? messageBean.getToUserID() : messageBean.getFromUserID();
                toUserID = messageBean.getToUserID();
                intent.putExtra("toUserID", toUserID);
                startActivityForResult(intent, 1);
            }
        });

        messageListAdaptor.notifyDataSetChanged();

        MessageReceiver.getInstance().setMessageListView(messageListView);
    }

    private View findViewById(int id) {

        return getActivity().findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        logger.info("______________________");

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_message, container, false);
        }

        logger.info("_______________________________onCreateView");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<T_MESSAGE> messageList = DBUtil.getDbUtil().findTop(LConstants.THIS_USER_ID);

        messageList = filterMessage(messageList);

        this.messageList.clear();

        this.messageList.addAll(messageList);

        BaseActivity.sendMessage(new BaseActivity.MessageHandle() {
            @Override
            public void handle(BaseActivity activity) {

                messageListAdaptor.notifyDataSetChanged();

                logger.info("___________________________message handle execute");
            }
        });

        logger.info("___________________________MessageFragment resume");
    }

    private List<T_MESSAGE> filterMessage(List<T_MESSAGE> messageList) {

        List<T_MESSAGE> ml = new ArrayList<T_MESSAGE>();

        HashMap<Integer, B_Contact> contactUserIDHashMap = LConstants.contactUserIDHashMap;

        for (int i = 0; i < messageList.size(); i++) {
            T_MESSAGE m = messageList.get(i);
            Integer userID = m.getToUserID();
            if (!contactUserIDHashMap.containsKey(userID)) {
                continue;
            }
            ml.add(m);
        }
        return ml;
    }
}

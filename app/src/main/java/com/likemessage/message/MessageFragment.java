package com.likemessage.message;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.PhoneActivity;
import com.likemessage.bean.T_MESSAGE;
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;
import com.likemessage.network.MessageReceiver;

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

    public MessageListAdpter getMessageListAdaptor(){
        return messageListAdaptor;
    }

    public MessageFragment(){}

    public void setPhoneActivity(PhoneActivity activity) {
        this.activity = activity;
    }

    public List<T_MESSAGE> getMessageList() {
        return messageList;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = this.getActivity();

        logger.info("_______________________________onActivityCreated");

        messageListView = (ListView) findViewById(R.id.messageListView);

        messageList = DBUtil.getDbUtil().findTop(LConstants.THIS_USER_ID);

        messageListAdaptor = new MessageListAdpter(activity,messageListView,messageList);

        messageListView.setAdapter(messageListAdaptor);

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessageListAdpter adapter = (MessageListAdpter) adapterView.getAdapter();
                T_MESSAGE messageBean = adapter.getItem(i);
                Intent intent = new Intent(activity,ChatActivity.class);
                Integer toUserID =  messageBean.isSend() ? messageBean.getToUserID() : messageBean.getFromUserID();
                toUserID = messageBean.getToUserID();
                intent.putExtra("toUserID",toUserID);
                startActivityForResult(intent, 1);
            }
        });

        messageListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView absListView, int i) {
                    logger.info("___________________________onScrollStateChanged:{}",i);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                logger.info("___________________________scroll:{},{},{}",new Object[]{i,i1,i2});
            }
        });

        messageListAdaptor.notifyDataSetChanged();

        MessageReceiver.getInstance().setMessageListView(messageListView);
    }

    private View findViewById(int id){

        return getActivity().findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        logger.info("______________________");

        if (view == null){
            view = inflater.inflate(R.layout.fragment_message, container, false);
        }

        logger.info("_______________________________onCreateView");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<T_MESSAGE> messageList = DBUtil.getDbUtil().findTop(LConstants.THIS_USER_ID);

        this.messageList.clear();

        this.messageList.addAll(messageList);

        this.activity.setBaseAdapter(messageListAdaptor);

        this.activity.notifyDataSetChanged();

        logger.info("___________________________MessageFragment resume");
    }


}

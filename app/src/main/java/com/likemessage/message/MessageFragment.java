package com.likemessage.message;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.bean.T_MESSAGE;
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

    private ListView messageListView;

    private MessageListAdpter messageListAdaptor;

    public MessageListAdpter getMessageListAdaptor(){
        return messageListAdaptor;
    }

    public MessageFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = this.getActivity();

        logger.info("_______________________________onActivityCreated");

        messageListView = (ListView) findViewById(R.id.messageListView);

        messageListAdaptor = new MessageListAdpter(activity,messageListView,mlist());

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

    private List<T_MESSAGE> mlist() {
        List<T_MESSAGE> list = DBUtil.getDbUtil().findTop();
        return list;
    };

    @Override
    public void onResume() {
        super.onResume();

        logger.info("___________________________MessageFragment resume");

    }
}

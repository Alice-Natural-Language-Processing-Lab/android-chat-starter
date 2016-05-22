package com.likemessage.layout;

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
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;
import com.likemessage.database.LMessage;

import java.util.ArrayList;
import java.util.List;

import in.co.madhur.chatbubblesdemo.ChatActivity;
import in.co.madhur.chatbubblesdemo.MessageAdpter;
import in.co.madhur.chatbubblesdemo.MessageList;
import in.co.madhur.chatbubblesdemo.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment {

    private Logger logger = LoggerFactory.getLogger(MessageFragment.class);

    private View view = null;

    private ListView mlv;

    private MessageAdpter madpter;

    public MessageFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = this.getActivity();

//        activity.setContentView(R.layout.fragment_message);

        LConstants.init(activity.getApplicationContext());

        logger.info("_______________________________onCreate");

        madpter = new MessageAdpter(activity);

        mlv = (ListView) findViewById(R.id.messageListView);
        mlv.setAdapter(madpter);
        mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity,ChatActivity.class);
                intent.putExtra("request_text_for_main", "从Main传递到SecondActivity");
                startActivityForResult(intent, 1);
            }
        });
        madpter.setMlist(mlist());
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

    private ArrayList<MessageList> mlist() {
        String name[] = { "妈妈", "爸爸", "姐姐", "哥哥", "弟弟" };
        String message[] = { "起床了么？", "今天准备做什么？", "快给我回个电话。。。", "你在干嘛？",
                "哥哥带我出去玩。。。" };
        ArrayList<MessageList> list = new ArrayList<MessageList>();
        for (int i = 0; i < name.length; i++) {
            MessageList messageList = new MessageList();
            messageList.setName(name[i]);
            messageList.setMessage(message[i]);
            list.add(messageList);
        }

        List<LMessage> lists1 = DBUtil.getDbUtil().findTop(1);

        if (lists1.size() > 0){
            LMessage lMessage = lists1.get(0);
            MessageList messageList = new MessageList();
            messageList.setName(lMessage.getToNo());
            messageList.setMessage(lMessage.getMessage());
            list.add(messageList);
        }
        return list;
    };

}

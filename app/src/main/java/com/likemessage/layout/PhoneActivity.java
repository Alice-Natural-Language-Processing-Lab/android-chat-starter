package com.likemessage.layout;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.common.LConstants;
import com.likemessage.database.DBUtil;
import com.likemessage.database.LMessage;

import java.util.ArrayList;
import java.util.List;

import in.co.madhur.chatbubblesdemo.MainActivity;
import in.co.madhur.chatbubblesdemo.MessageAdpter;
import in.co.madhur.chatbubblesdemo.MessageList;
import in.co.madhur.chatbubblesdemo.R;

public class PhoneActivity extends Activity {

    private ListView mlv;
    private MessageAdpter madpter;
    private Logger logger = LoggerFactory.getLogger(PhoneActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LConstants.init(this.getApplicationContext());

        setContentView(R.layout.fragment_message);

        madpter = new MessageAdpter(this);
        mlv = (ListView) findViewById(R.id.messageListView);
        mlv.setAdapter(madpter);
        mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PhoneActivity.this,MainActivity.class);
                intent.putExtra("request_text_for_main", "从Main传递到SecondActivity");
                startActivityForResult(intent, 1);
            }
        });
        madpter.setMlist(mlist());


        Button m_btn_message = (Button) findViewById(R.id.m_btn_message);
        Button m_btn_contact = (Button) findViewById(R.id.m_btn_contact);
        Button c_btn_message = (Button) findViewById(R.id.c_btn_message);
        Button c_btn_contact = (Button)  findViewById(R.id.c_btn_contact);

        logger.info("______________________m_btn_message:{}",m_btn_message);
        logger.info("______________________m_btn_contact:{}",m_btn_contact);
        logger.info("______________________c_btn_message:{}",c_btn_message);
        logger.info("______________________c_btn_contact:{}",c_btn_contact);

        m_btn_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction ft = fragmentManager.beginTransaction();

                Fragment fragment = new MessageFragment();

                ft.replace(R.id.m_btn_message,fragment);

                ft.commitAllowingStateLoss();

                logger.info("______________________,{}",view);
            }
        });

        m_btn_message.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logger.info("______________________,{}",view);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        logger.info("========================"+requestCode);
        logger.info("========================"+resultCode);
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

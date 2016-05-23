package com.likemessage;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.contact.ContactFragment;
import com.likemessage.message.MessageFragment;
import com.likemessage.network.MessageReceiver;

import in.co.madhur.chatbubblesdemo.R;

public class PhoneActivity extends Activity {

    private Logger logger = LoggerFactory.getLogger(PhoneActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        MessageFragment messageFragment = new MessageFragment();
        ContactFragment contactFragment = new ContactFragment();

        transaction.add(R.id.fragment_wrapper, messageFragment);

        transaction.show(messageFragment);

        transaction.commitAllowingStateLoss();

        LinearLayout btn_message = (LinearLayout) findViewById(R.id.btn_message);
        LinearLayout btn_contact = (LinearLayout) findViewById(R.id.btn_contact);

        MCOnClickListener mcOnClickListener = new MCOnClickListener(this,messageFragment,contactFragment);

        btn_message.setOnClickListener(mcOnClickListener);
        btn_contact.setOnClickListener(mcOnClickListener);


        receiveMsg();
    }

    private void receiveMsg(){
        /*-------------------- receive message ----------------- */
        MessageReceiver.startReceive(this);
    }
}

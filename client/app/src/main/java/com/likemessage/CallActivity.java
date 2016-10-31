package com.likemessage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.common.ThreadUtil;
import com.generallycloud.nio.extend.plugin.jms.MQException;
import com.generallycloud.nio.extend.plugin.jms.MapMessage;
import com.generallycloud.nio.extend.plugin.jms.client.MessageProducer;
import com.generallycloud.nio.extend.plugin.rtp.RTPException;
import com.generallycloud.nio.extend.plugin.rtp.client.RTPClient;
import com.likemessage.call.CallHandle;
import com.likemessage.common.LConstants;
import com.likemessage.network.ConnectorManager;

import in.co.madhur.chatbubblesdemo.R;

public class CallActivity extends BaseActivity {

    private Logger logger = LoggerFactory.getLogger(CallActivity.class);

    private boolean isCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        Intent intent = getIntent();

        final ConnectorManager manager = LConstants.getConnectorManager();

        final boolean isCall = intent.getBooleanExtra("isCall", false);
        final String uuid = intent.getStringExtra("uuid");
        final RTPClient client = manager.RTP_CLIENT;
        final Button btn_call = (Button) findViewById(R.id.btn_call);
        final Button btn_break_call = (Button) findViewById(R.id.btn_break_call);

        if (isCall) {
            btn_break_call.setEnabled(false);
            btn_call.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    try {
                        client.createRoom(uuid);
                    } catch (RTPException e) {
                        e.printStackTrace();
                    }
                    btn_call.setEnabled(false);
                    btn_break_call.setEnabled(true);
                }
            });

            btn_break_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        client.leaveRoom();
                    } catch (RTPException e) {
                        e.printStackTrace();
                    }
                    btn_break_call.setEnabled(false);
                }
            });
        }else{
            btn_call.setText("接听电话");
            btn_call.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final CallHandle handle =(CallHandle) client.getRTPHandle();

                    ThreadUtil.execute(new Runnable() {
                        @Override
                        public void run() {
                            handle.onInviteCall();
                        }
                    });
                    isCalled = true;
                    btn_call.setEnabled(false);
                    btn_break_call.setEnabled(true);
                }
            });
            btn_break_call.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (isCalled){
                        try {
                            client.leaveRoom();
                        } catch (RTPException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            MapMessage message = new MapMessage("mmm", uuid);
                            MessageProducer producer = manager.MESSAGE_PRODUCER;
                            producer.offer(message);
                        } catch (MQException e) {
                            e.printStackTrace();
                        }
                    }
                    btn_call.setEnabled(false);
                    btn_break_call.setEnabled(true);
                }
            });
        }
    }
}

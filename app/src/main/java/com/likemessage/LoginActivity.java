package com.likemessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.StringUtil;
import com.gifisan.nio.connector.TCPConnector;
import com.gifisan.nio.extend.FixedSession;
import com.gifisan.nio.extend.RESMessage;
import com.likemessage.bean.B_Contact;
import com.likemessage.bean.T_USER;
import com.likemessage.client.LMClient;
import com.likemessage.common.LConstants;
import com.likemessage.network.ConnectorManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.co.madhur.chatbubblesdemo.R;

public class LoginActivity extends BaseActivity {

    private Logger logger = LoggerFactory.getLogger(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConnectorManager manager = LConstants.getConnectorManager();

        TCPConnector connector = manager.getTCPConnector();
        final Activity activity = this;

        FixedSession session = manager.getFixedIOSession();

        if (connector != null && session.isLogined()){
                jump(activity);
                return;
        }



        final TextView txt_username = (TextView) findViewById(R.id.txt_username);
        final TextView txt_password = (TextView) findViewById(R.id.txt_password);

        LConstants.init(activity);

        Button btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

                if (StringUtil.isNullOrBlank(username)) {
                    Toast.makeText(activity.getApplicationContext(), "请输入Email/Username",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtil.isNullOrBlank(password)) {
                    Toast.makeText(activity.getApplicationContext(), "请输入Password",Toast.LENGTH_SHORT).show();
                    return;
                }

                ConnectorManager manager = LConstants.getConnectorManager();

                FixedSession session = manager.getFixedIOSession();

                logger.info("login : {},{}",username,password);

                RESMessage resMessage = session.login4RES(username,password);

                logger.info(resMessage.toString());

                if (resMessage.getCode() == 0) {
                    T_USER user = (T_USER) session.getAuthority();

                    LConstants.THIS_USER_ID = user.getUserID();
                    LConstants.THIS_USER_NAME = user.getUsername();
                    LConstants.THIS_NICK_NAME = user.getNickname();

                    LMClient client = LConstants.LM_CLIENT;

                    try {
                        List<B_Contact> contacts = client.getContactListByUserID(session);
                        if (contacts == null){
                            contacts = new ArrayList<B_Contact>();
                        }
                        LConstants.contacts = contacts;
                        LConstants.initizlizeContact();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    try {
//                        manager.RTP_CLIENT.bindTCPSession();
//                        manager.RTP_CLIENT.setRTPHandle(new CallHandle());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    jump(activity);

                } else {
                    logger.info("____________________________登录失败");
                    Toast.makeText(activity.getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void jump(Activity activity){

        Intent intent = new Intent(activity, PhoneActivity.class);
        intent.putExtra("test", "test");
        startActivityForResult(intent, 1);

        activity.finish();
    }
}

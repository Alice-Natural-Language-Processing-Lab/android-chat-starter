package com.likemessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gifisan.nio.client.ClientTCPConnector;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.StringUtil;
import com.likemessage.bean.T_USER;
import com.likemessage.common.LConstants;

import in.co.madhur.chatbubblesdemo.R;

public class LoginActivity extends Activity {

    private Logger logger = LoggerFactory.getLogger(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Activity activity = this;

        final TextView txt_username = (TextView) findViewById(R.id.txt_username);
        final TextView txt_password = (TextView) findViewById(R.id.txt_password);

        LConstants.init(activity);

        Button btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

                if (StringUtil.isNullOrBlank(username)){
                    Toast.makeText(activity.getApplicationContext(),"请输入Email/Username",Toast.LENGTH_LONG);
                    return;
                }

                if (StringUtil.isNullOrBlank(password)){
                    Toast.makeText(activity.getApplicationContext(),"请输入Password",Toast.LENGTH_LONG);
                    return;
                }

                ClientTCPConnector connector = LConstants.connector;

                if (connector.login(username,password)){
                    T_USER user = (T_USER)connector.getClientSession().getAuthority();

                    LConstants.THIS_USER_ID = user.getUserID();
                    LConstants.THIS_USER_NAME = user.getUsername();
                    LConstants.THIS_NICK_NAME = user.getNickname();

                    Intent intent = new Intent(activity,PhoneActivity.class);
                    intent.putExtra("test","test");
                    startActivityForResult(intent, 1);
                }else{
                    Toast.makeText(activity.getApplicationContext(),"请输入Password",Toast.LENGTH_LONG);
                }
            }
        });
    }
}

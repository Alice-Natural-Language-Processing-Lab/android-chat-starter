package com.likemessage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gifisan.nio.common.BeanUtil;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.PinyinUtil;
import com.gifisan.nio.common.StringUtil;
import com.gifisan.nio.extend.RESMessage;
import com.likemessage.bean.B_Contact;
import com.likemessage.bean.T_CONTACT;
import com.likemessage.bean.T_USER;
import com.likemessage.client.LMClient;
import com.likemessage.common.LConstants;
import com.likemessage.network.ConnectorManager;

import java.io.IOException;
import java.util.Map;

import in.co.madhur.chatbubblesdemo.R;

public class AddContactActivity extends BaseActivity {

    private Logger logger = LoggerFactory.getLogger(AddContactActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Intent intent = getIntent();


        final AddContactActivity activity = this;
        final Button btn_add_contact = (Button) findViewById(R.id.btn_add_contact);
        final TextView txt_username = (TextView) findViewById(R.id.txt_username);
        final TextView txt_backupName = (TextView) findViewById(R.id.txt_backupName);

        btn_add_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String username = txt_username.getText().toString();
                String backupName = txt_backupName.getText().toString();

                if (StringUtil.isNullOrBlank(username)) {
                    Toast.makeText(activity, "请输入Email/Username",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtil.isNullOrBlank(backupName)) {
                    Toast.makeText(activity, "请输入备注",Toast.LENGTH_SHORT).show();
                    return;
                }

                LMClient client = LConstants.LM_CLIENT;

                T_CONTACT contact = new T_CONTACT();

                contact.setBackupName(backupName);
                contact.setPinyin(PinyinUtil.toPinyin(backupName));

                ConnectorManager manager = LConstants.getConnectorManager();

                try {
                    RESMessage resMessage = client.addContact(manager.getFixedIOSession(),contact,username);
                    String alert = resMessage.getDescription();
                    if (resMessage.getCode() == 0){
                        alert = "添加成功";

                        T_USER user = (T_USER) BeanUtil.map2Object((Map<String, Object>) resMessage.getData(),T_USER.class);

                        B_Contact c = new B_Contact();
                        c.setBackupName(backupName);
                        c.setGroupID(0);
                        c.setGroupName("");
                        c.setNickname(user.getNickname());
                        c.setPhoneNo(user.getPhoneNo());
                        c.setPinyin(contact.getPinyin());
                        c.setUserID(user.getUserID());
                        c.setUUID(user.getUuid());
                        LConstants.contacts.add(c);
                        LConstants.isNeedRefreshContact = true;
                    }
                    Toast.makeText(activity, alert, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

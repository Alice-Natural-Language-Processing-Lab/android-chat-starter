package com.likemessage.common;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.generallycloud.nio.Encoding;
import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.component.concurrent.UniqueThread;
import com.likemessage.bean.B_Contact;
import com.likemessage.client.LMClient;
import com.likemessage.database.DBUtil;
import com.likemessage.network.ConnectorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wangkai on 2016/4/13.
 */
public class LConstants {

    public static Integer THIS_USER_ID;

    public static String THIS_USER_NAME;

    public static String THIS_NICK_NAME;

    public static LMClient LM_CLIENT = new LMClient();

    public static HashMap<String, B_Contact> contactUUIDHashMap = new HashMap<String, B_Contact>();

    public static HashMap<Integer, B_Contact> contactUserIDHashMap = new HashMap<Integer, B_Contact>();

    public static List<B_Contact> contacts = new ArrayList<B_Contact>();

    public static List<B_Contact> fixdContacts = new ArrayList<B_Contact>();

    public static boolean isNeedRefreshContact = false;

    public static UniqueThread uniqueThread;

    private static AtomicBoolean inited = new AtomicBoolean(false);

    private static Logger logger = LoggerFactory.getLogger(LConstants.class);

    private static ConnectorManager connectorManager = new ConnectorManager();

    public static void init(Context context) {
        Encoding.DEFAULT = Encoding.UTF8;

        if (inited.compareAndSet(false, true)) {

            PhoneInfo.init(context);

            DBUtil.init(context);

           uniqueThread = new UniqueThread(connectorManager,"connector-manager");

            uniqueThread.start();
        }
    }

    public static ConnectorManager getConnectorManager(){
        return connectorManager;
    }

    public static B_Contact getBContactByUUID(String uuid) {
        return contactUUIDHashMap.get(uuid);
    }

    public static B_Contact getBContactByUserID(Integer userID) {
        return contactUserIDHashMap.get(userID);
    }

    public static void initizlizeContact() {
        List<B_Contact> contacts = LConstants.contacts;
        Collections.sort(contacts, new Comparator<B_Contact>() {
            public int compare(B_Contact b_contact, B_Contact t1) {
                String p1 = b_contact.getPinyin();
                String p2 = t1.getPinyin();
                return p1.compareTo(p2);
            }
        });

        List<B_Contact> contactList = new ArrayList<B_Contact>();
        char lastChar = 0;
        HashMap<String, B_Contact> contactUUIDHashMap = LConstants.contactUUIDHashMap;
        HashMap<Integer, B_Contact> contactUserIDHashMap = LConstants.contactUserIDHashMap;

        for (int i = 0; i < contacts.size(); i++) {
            B_Contact contact = contacts.get(i);
            logger.info("_____________________contact:{}", JSONObject.toJSON(contact));
            contactUUIDHashMap.put(contact.getUUID(), contact);
            contactUserIDHashMap.put(contact.getUserID(), contact);
            String p = contact.getPinyin();
            char c = p.charAt(0);
            if (c > lastChar){
                lastChar = c;
                B_Contact contact1 = new B_Contact();
                contact1.setPinyin(String.valueOf(lastChar));
                contact1.setUserID(-1);
                contactList.add(contact1);
            }
            contactList.add(contact);
        }
        fixdContacts = contactList;
    }
}

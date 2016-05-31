package com.likemessage.common;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.gifisan.nio.Encoding;
import com.gifisan.nio.client.ClientSession;
import com.gifisan.nio.client.ClientTCPConnector;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.UniqueThread;
import com.gifisan.nio.plugin.jms.client.MessageProducer;
import com.gifisan.nio.plugin.jms.client.impl.DefaultMessageProducer;
import com.gifisan.nio.plugin.jms.client.impl.FixedMessageConsumer;
import com.likemessage.bean.B_Contact;
import com.likemessage.client.LMClient;
import com.likemessage.database.DBUtil;

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

    public static FixedMessageConsumer messageConsumer = null;

    public static MessageProducer messageProducer = null;

    public static LMClient client = new LMClient();

    public static HashMap<String, B_Contact> contactUUIDHashMap = new HashMap<String, B_Contact>();

    public static HashMap<Integer, B_Contact> contactUserIDHashMap = new HashMap<Integer, B_Contact>();

    public static List<B_Contact> contacts = new ArrayList<B_Contact>();

    public static List<B_Contact> fixdContacts = new ArrayList<B_Contact>();

    public static boolean isNeedRefreshContact = false;

    public static ClientTCPConnector connector = null;

    public static ClientSession clientSession = null;

    public static UniqueThread uniqueThread = new UniqueThread();

    public static boolean initComplete = false;

    private static AtomicBoolean inited = new AtomicBoolean(false);

    private static Logger logger = LoggerFactory.getLogger(LConstants.class);

    public static void init(Context context) {
        Encoding.DEFAULT = Encoding.UTF8;

//        connector = new ClientTCPConnector("10.0.2.2",18900,"M");
//        connector = new ClientTCPConnector("wkapp.wicp.net",11990,"M");
        connector = new ClientTCPConnector("192.168.191.1", 8300, "M");
//        connector = new ClientTCPConnector("192.168.1.48", 8300, "M");

        if (inited.compareAndSet(false, true)) {

//            PhoneInfo.init(context);

            DBUtil.init(context);

            uniqueThread.start();

            uniqueThread.execute(new Runnable() {
                @Override
                public void run() {
                    try {

//                        THIS_PHONE = PhoneInfo.getPhoneInfo().getNativePhoneNumber();
//                        logger.info("================================THIS_PHONE:" + THIS_PHONE);

                        logger.info("================================start to connect server:");

                        connector.connect();

                        clientSession = connector.getClientSession();

                        logger.info("================================Connected to server:" + connector.toString());

                        messageConsumer = new FixedMessageConsumer(clientSession);
                        messageProducer = new DefaultMessageProducer(clientSession);

                        initComplete = true;

                    } catch (Exception e) {
                        e.printStackTrace();

                        logger.info("================================Can not connect to:" + connector.toString());
                    }
                }
            });
        }
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

package com.likemessage.common;

import android.content.Context;

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

    public static HashMap<String,B_Contact> contactUUIDHashMap = new HashMap<String, B_Contact>();

    public static HashMap<Integer,B_Contact> contactUserIDHashMap = new HashMap<Integer, B_Contact>();

    public static List<B_Contact> contacts = new ArrayList<B_Contact>();

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
        connector = new ClientTCPConnector("192.168.191.1", 18900, "M");
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

    public static B_Contact getBContactByUUID(String uuid){
        return contactUUIDHashMap.get(uuid);
    }

    public static B_Contact getBContactByUserID(Integer userID){
        return contactUserIDHashMap.get(userID);
    }

}

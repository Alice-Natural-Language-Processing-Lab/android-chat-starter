package com.likemessage.common;

import android.content.Context;

import com.gifisan.nio.client.ClientSession;
import com.gifisan.nio.client.ClientTCPConnector;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.UniqueThread;
import com.gifisan.nio.plugin.jms.client.MessageConsumer;
import com.gifisan.nio.plugin.jms.client.MessageProducer;
import com.gifisan.nio.plugin.jms.client.impl.DefaultMessageConsumer;
import com.gifisan.nio.plugin.jms.client.impl.DefaultMessageProducer;
import com.likemessage.database.DBUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wangkai on 2016/4/13.
 */
public class LConstants {

    public static String THIS_PHONE = null;

    public static String FRIEND_PHONE = null;

    public static MessageConsumer messageConsumer = null;

    public static MessageProducer messageProducer = null;

    //    private ClientConnector connector = new ClientConnector("wkapp.wicp.net",11990);

//    private static ClientConnector connector = new ClientConnector("10.0.2.2",8300);

    private static ClientTCPConnector connector = new ClientTCPConnector("192.168.1.48",18900);

    private static ClientSession request = null;

    private static ClientSession receiveSession = null;

    public static UniqueThread uniqueThread = new UniqueThread();

    public static boolean initComplete = false;

    private static AtomicBoolean inited = new AtomicBoolean(false);

    private static Logger logger = LoggerFactory.getLogger(LConstants.class);

    public static void init(Context context){

        if (inited.compareAndSet(false,true)){

            PhoneInfo.init(context);

            DBUtil.init(context);

            uniqueThread.start();

            uniqueThread.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        THIS_PHONE = PhoneInfo.getPhoneInfo().getNativePhoneNumber();
                        logger.info("================================THIS_PHONE:"+THIS_PHONE);

                        if ("17087791610".equals(THIS_PHONE)){
                            FRIEND_PHONE = "18767480090";
                        }else{
                            FRIEND_PHONE = "17087791610";
                        }

                        logger.info("================================FRIEND_PHONE:"+FRIEND_PHONE);

                        connector.connect();
                        request = connector.getClientSession();
                        receiveSession = connector.getClientSession();

                        logger.info("================================Connected to server:"+connector.toString());

                        messageConsumer = new DefaultMessageConsumer(receiveSession,THIS_PHONE);
                        messageProducer = new DefaultMessageProducer(request);
                        messageProducer.login("admin","admin100");
                        messageConsumer.login("admin","admin100");

                        initComplete = true;

                    } catch (Exception e) {
                        e.printStackTrace();

                        logger.info("================================Can not connect to:"+connector.toString());
                    }
                }
            });


        }
    }

}

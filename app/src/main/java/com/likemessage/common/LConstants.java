package com.likemessage.common;

import android.content.Context;

import com.gifisan.nio.UniqueThread;
import com.gifisan.nio.client.ClientConnector;
import com.gifisan.nio.client.ClientResponse;
import com.gifisan.nio.client.ClientSesssion;
import com.gifisan.nio.common.DebugUtil;
import com.gifisan.nio.jms.client.MessageConsumer;
import com.gifisan.nio.jms.client.MessageProducer;
import com.gifisan.nio.jms.client.impl.MessageConsumerImpl;
import com.gifisan.nio.jms.client.impl.MessageProducerImpl;
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

    private static ClientConnector connector = new ClientConnector("192.168.1.67",8300);

    private static ClientSesssion request = null;

    private static ClientSesssion receiveSession = null;

    public static UniqueThread uniqueThread = new UniqueThread();

    public static boolean initComplete = false;

    private static AtomicBoolean inited = new AtomicBoolean(false);

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
                        DebugUtil.info("================================THIS_PHONE:"+THIS_PHONE);

                        if ("17087791610".equals(THIS_PHONE)){
                            FRIEND_PHONE = "18767480090";
                        }else{
                            FRIEND_PHONE = "17087791610";
                        }

                        DebugUtil.info("================================FRIEND_PHONE:"+FRIEND_PHONE);

                        connector.connect(true);
                        request = connector.getClientSession();
                        receiveSession = connector.getClientSession();

                        DebugUtil.info("================================Connected to server:"+connector.toString());

                        messageConsumer = new MessageConsumerImpl(receiveSession,THIS_PHONE);
                        messageProducer = new MessageProducerImpl(request);
                        messageProducer.login("admin","admin100");
                        messageConsumer.login("admin","admin100");

                        initComplete = true;

                    } catch (Exception e) {
                        e.printStackTrace();

                        DebugUtil.info("================================Can not connect to:"+connector.toString());
                    }
                }
            });


        }
    }

}

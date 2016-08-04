package com.likemessage.network;

import com.gifisan.nio.Encoding;
import com.gifisan.nio.Looper;
import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.common.ThreadUtil;
import com.gifisan.nio.component.DefaultNIOContext;
import com.gifisan.nio.component.LoggerSEListener;
import com.gifisan.nio.component.NIOContext;
import com.gifisan.nio.component.Session;
import com.gifisan.nio.connector.TCPConnector;
import com.gifisan.nio.connector.UDPConnector;
import com.gifisan.nio.extend.ConnectorCloseSEListener;
import com.gifisan.nio.extend.FixedSession;
import com.gifisan.nio.extend.SimpleIOEventHandle;
import com.gifisan.nio.extend.configuration.ServerConfiguration;
import com.gifisan.nio.extend.plugin.jms.client.MessageProducer;
import com.gifisan.nio.extend.plugin.jms.client.impl.DefaultMessageProducer;
import com.gifisan.nio.extend.plugin.jms.client.impl.FixedMessageConsumer;
import com.gifisan.nio.extend.plugin.rtp.client.RTPClient;
import com.likemessage.common.PhoneInfo;

import java.io.IOException;

/**
 * Created by wangkai on 2016/8/3.
 */
public class ConnectorManager implements Looper {

    private Logger logger = LoggerFactory.getLogger(ConnectorManager.class);

    private TCPConnector connector = createTCPConnector();

    private SimpleIOEventHandle simpleIOEventHandle;

    public FixedMessageConsumer MESSAGE_CONSUMER = null;

    public MessageProducer MESSAGE_PRODUCER = null;

    public UDPConnector UDP_CONNECTOR = null;

    public Session getSession(){
        return connector.getSession();
    }

    public  RTPClient RTP_CLIENT = null;

    @Override
    public void loop() {

        if(isConnected()){
            ThreadUtil.sleep(5000);
            return;
        }

        if (PhoneInfo.getPhoneInfo().isNetworkConnected()){
            try {
                logger.info("================================start to connect server:");
                connector.connect();
                update();
                logger.info("================================Connected to server:" + connector.toString());
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
                ThreadUtil.sleep(1000);
            }
        }else{
            ThreadUtil.sleep(30 * 1000);
        }
    }

    private void update(){
        UDP_CONNECTOR = new UDPConnector(getSession());
        MESSAGE_CONSUMER = new FixedMessageConsumer(getFixedIOSession());
        MESSAGE_PRODUCER = new DefaultMessageProducer(getFixedIOSession());
        RTP_CLIENT = new RTPClient(getFixedIOSession(),UDP_CONNECTOR, MESSAGE_CONSUMER, MESSAGE_PRODUCER);
    }

    @Override
    public void stop() {

    }

    private TCPConnector createTCPConnector() {

        simpleIOEventHandle = new SimpleIOEventHandle();

        ServerConfiguration configuration = new ServerConfiguration();

        configuration.setSERVER_ENCODING(Encoding.UTF8);
        configuration.setSERVER_HOST("generallycloud.com");
        configuration.setSERVER_TCP_PORT(18300);
        configuration.setSERVER_UDP_PORT(18500);
        configuration.setSERVER_WRITE_QUEUE_SIZE(512);

        TCPConnector connector = new TCPConnector();

        NIOContext context = new DefaultNIOContext();

        context.setServerConfiguration(configuration);

        context.setIOEventHandleAdaptor(simpleIOEventHandle);

        context.addSessionEventListener(new LoggerSEListener());

        context.addSessionEventListener(new ConnectorCloseSEListener(connector));

        connector.setContext(context);

        return connector;
    }

    public FixedSession getFixedIOSession(){
        return simpleIOEventHandle.getFixedSession();
    }

    public TCPConnector getTCPConnector(){
        return connector;
    }

    public boolean isConnected(){
        return connector.isConnected();
    }
}

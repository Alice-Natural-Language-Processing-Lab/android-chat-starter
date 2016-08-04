package com.likemessage.call;

import android.content.Intent;
import android.os.Bundle;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.gifisan.nio.component.protocol.DatagramPacket;
import com.gifisan.nio.component.protocol.DatagramPacketFactory;
import com.gifisan.nio.component.protocol.DatagramPacketGroup;
import com.gifisan.nio.component.protocol.DatagramPacketGroup.DPForeach;
import com.gifisan.nio.extend.plugin.jms.MapMessage;
import com.gifisan.nio.extend.plugin.rtp.RTPException;
import com.gifisan.nio.extend.plugin.rtp.client.RTPClient;
import com.gifisan.nio.extend.plugin.rtp.client.RTPClientDPAcceptor;
import com.gifisan.nio.extend.plugin.rtp.client.RTPHandle;
import com.likemessage.BaseActivity;
import com.likemessage.CallActivity;
import com.likemessage.audio.AudioCodec;
import com.likemessage.audio.AudioPlayer;
import com.likemessage.audio.AudioRecorder;
import com.likemessage.audio.AudioRecorder.Record;

/**
 * Created by wangkai on 2016/6/4.
 */
public class CallHandle extends RTPHandle {

    private Logger logger = LoggerFactory.getLogger(CallHandle.class);
    private int sleep = 1;
    private boolean running = false;
    private AudioRecorder recorder = AudioRecorder.getInstance();
    private final AudioPlayer player = AudioPlayer.getInstance();
    private byte[] data = new byte[recorder.BUFFER_FRAME_SIZE];

    public void onReceiveUDPPacket(RTPClient client, DatagramPacketGroup group) {

        if (!running) {
            return;
        }
        group.foreach(new DPForeach() {
            public void onPacket(DatagramPacket packet) {

                byte[] array = packet.getData();

                AudioCodec.audio_decode(array, 0, array.length, data, data.length);

                player.write(data, data.length);
            }
        });
    }

    private RTPClient client = null;
    private String inviteUsername = null;

    public void onInvite(RTPClient client, MapMessage message) {

        final String inviteUsername = message.getParameter("inviteUsername");
        String roomID = message.getParameter("roomID");
        client.setRoomID(roomID);
        this.client = client;
        this.inviteUsername = inviteUsername;

        BaseActivity.sendMessage(new BaseActivity.MessageHandle() {
            @Override
            public void handle(BaseActivity activity) {
                Intent intent = new Intent(activity, CallActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("uuid", inviteUsername);
                intent.putExtra("isCall", false);
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    public void onInviteCall(){

        int markInterval = 2;
        int groupSize = 64;
        DatagramPacketFactory factory = new DatagramPacketFactory(markInterval);
        long currentMark = factory.getCalculagraph().getAlphaTimestamp();

        try {
            client.joinRoom(client.getRoomID());

            client.inviteReply(inviteUsername, markInterval, currentMark, groupSize);
        } catch (RTPException e) {
            e.printStackTrace();
        }

        RTPClientDPAcceptor acceptor = new RTPClientDPAcceptor(markInterval, currentMark, groupSize, this, client);

        client.setRTPClientDPAcceptor(acceptor);

        sendRecord(client, factory);
    }

    public void onInviteReplyed(RTPClient client, MapMessage message) {

        int markInterval = message.getIntegerParameter(RTPClient.MARK_INTERVAL);

        long currentMark = message.getLongParameter(RTPClient.CURRENT_MARK);

        int groupSize = message.getIntegerParameter(RTPClient.GROUP_SIZE);

        logger.debug("___________onInviteReplyed:{},{},{}", new Object[]{markInterval, currentMark, groupSize});

        DatagramPacketFactory factory = new DatagramPacketFactory(markInterval, currentMark);

        RTPClientDPAcceptor acceptor = new RTPClientDPAcceptor(markInterval, currentMark, groupSize, this, client);

        client.setRTPClientDPAcceptor(acceptor);

        sendRecord(client, factory);
    }

    private void sendRecord(RTPClient client, DatagramPacketFactory factory) {

        recorder.startRecording();
        player.startPlaying();

        running = true;

        byte[] array = new byte[100];

        for (; running; ) {

            Record record = recorder.read();

            AudioCodec.audio_encode(record.array, 0, record.length, array, 0);

            DatagramPacket packet = factory.createDatagramPacket(array);

            try {
                client.sendDatagramPacket(packet);
            } catch (RTPException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBreak(RTPClient client, MapMessage message) {
        player.stopPlaying();
        recorder.stopRecording();
        logger.debug("_________________________leave,{}", message.toString());
    }

}

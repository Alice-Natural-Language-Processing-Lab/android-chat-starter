package com.likemessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;
import com.likemessage.audio.AudioRecorder;
import com.likemessage.client.LMClient;
import com.likemessage.common.LConstants;

import in.co.madhur.chatbubblesdemo.R;

public class CallActivity extends Activity {

    private Logger logger = LoggerFactory.getLogger(CallActivity.class);

    private boolean callState = false;

    private Integer toUserID;

    private AudioRecorder audioRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        audioRecord = new AudioRecorder();

        Intent intent = getIntent();

        this.toUserID = intent.getIntExtra("toUserID",-1);

        logger.info("______________________________toUserID,{}",toUserID);

        final CallActivity activity = this;
        final Button btn_add_contact = (Button) findViewById(R.id.btn_call);

        btn_add_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (callState){
                    callState = false;
                    btn_add_contact.setText("播出");
                    audioRecord.stopRecording();

                }else{
                    callState = true;
                    btn_add_contact.setText("挂断");
                    audioRecord.setOnRecord(new AudioRecorder.OnRecord() {
                        @Override
                        public void onRecord(byte[] array, int length) {

                        }
                    });
                }
                LMClient client = LConstants.client;

            }
        });
    }
}

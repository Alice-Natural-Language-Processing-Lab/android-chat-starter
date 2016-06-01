package com.likemessage.audio;

import android.media.AudioRecord;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;

/**
 * Created by wangkai on 2016/6/1.
 */
public class AudioRecorder extends Audio implements Runnable {

    private boolean isRecording = false;
    private AudioRecord audioRecord;
    private Logger logger = LoggerFactory.getLogger(AudioRecorder.class);
    private OnRecord onRecord = null;

    public void setOnRecord(OnRecord onRecord){
        this.onRecord = onRecord;
    }

    // 开始录制
    private void initialize() {

        int audioBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG,
                AUDIO_FORMAT);
        if (audioBufSize == AudioRecord.ERROR_BAD_VALUE) {
            logger.error("__________________________________audioBufSize error");
            return;
        }
        // 初始化recorder
        if (null == audioRecord) {
            audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE,
                    CHANNEL_CONFIG, AUDIO_FORMAT, audioBufSize);
        }
    }

    // 停止录制
    public void stopRecording() {
        this.isRecording = false;
    }

    public void run() {
        // 录制前，先启动解码器
        this.initialize();
        logger.debug("____________________________________________audioRecord initialize()");
        audioRecord.startRecording();
        this.isRecording = true;
        byte[] SAMPLE_ARRAY = new byte[BUFFER_FRAME_SIZE];
        for (;isRecording;) {
            int bufferRead = audioRecord.read(SAMPLE_ARRAY, 0, BUFFER_FRAME_SIZE);
            if (bufferRead > 0) {
                logger.debug("_________________________,{}", bufferRead);
                this.onRecord.onRecord(SAMPLE_ARRAY,bufferRead);
            }
        }
        logger.info("__________________录制结束");
        audioRecord.stop();
        audioRecord.release();
    }

    public interface OnRecord{
        public abstract  void onRecord(byte [] array,int length);
    }
}

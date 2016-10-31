package com.likemessage.audio;

import android.media.AudioRecord;

import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;


/**
 * Created by wangkai on 2016/6/1.
 */
public class AudioRecorder extends Audio {

    private AudioRecord audioRecord;
    private Logger logger = LoggerFactory.getLogger(AudioRecorder.class);
    private static AudioRecorder recorder = null;
    private Record record = new Record();
    public final int BUFFER_FRAME_SIZE = 960;
    private byte[] SAMPLE_ARRAY = new byte[BUFFER_FRAME_SIZE];

    private AudioRecorder (){}

    public static AudioRecorder getInstance() {
        if (recorder == null) {
            recorder = new AudioRecorder();
            recorder.initialize();
        }
        return recorder;
    }

    //初始化
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

        logger.debug("____________________________________________audioRecord initialize()");
    }

    // 开始录制
    public void startRecording(){
        audioRecord.startRecording();
    }

    // 停止录制
    public void stopRecording() {

        logger.info("__________________录制结束");
        audioRecord.stop();
        audioRecord.release();
    }

    /**
     * 返回单例Array数组，请勿多线程操作该数组
     * @return
     */
    public Record read() {
        int bufferRead = audioRecord.read(SAMPLE_ARRAY, 0, BUFFER_FRAME_SIZE);
        if (bufferRead > 0) {
            logger.debug("_________________________,{}", bufferRead);
            //这里直接return SAMPLE_ARRAY不好，注意点应该没问题
            record.length = bufferRead;
            record.array = SAMPLE_ARRAY;
            return record;
        }
        return null;
    }

    public class Record{
        public int length;
        public byte [] array;
    }
}

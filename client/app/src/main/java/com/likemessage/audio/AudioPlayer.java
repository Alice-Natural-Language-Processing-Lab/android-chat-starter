package com.likemessage.audio;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;


/**
 * Created by wangkai on 2016/6/1.
 */
public class AudioPlayer extends Audio {

    private static AudioPlayer player;
    private AudioTrack audioTrack;
    private Logger logger = LoggerFactory.getLogger(AudioPlayer.class);
    // 注意：参数配置

    private AudioPlayer(){}

    public static AudioPlayer getInstance() {
        if (player == null) {
            player = new AudioPlayer();
            player.initAudioTrack();
        }
        return player;
    }

    private boolean initAudioTrack() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (bufferSize < 0) {
            logger.info("________________________________________initialize error!");
            return false;
        }
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize, AudioTrack.MODE_STREAM);
        // set volume:设置播放音量
        audioTrack.setStereoVolume(1.0f, 1.0f);
        return true;
    }

    public void write(byte[] array, int length) {
        audioTrack.write(array, 0, length);
    }

    public void startPlaying(){
        this.audioTrack.play();
    }

    public void stopPlaying() {
        if (this.audioTrack != null) {
            if (this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                this.audioTrack.stop();
                this.audioTrack.release();
            }
        }
    }
}

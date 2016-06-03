package com.likemessage.audio;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * Created by wangkai on 2016/6/1.
 */
public class Audio {

    public final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    public final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    public final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public final int SAMPLE_RATE = 8000;

}

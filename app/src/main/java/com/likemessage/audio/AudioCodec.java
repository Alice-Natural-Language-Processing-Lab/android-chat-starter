package com.likemessage.audio;

public interface AudioCodec {

    // initialize decoder and encoder
    public abstract int audio_codec_init(int mode);

    // encode
    public abstract int audio_encode(byte[] sample, int sampleOffset,
                                     int sampleLength, byte[] data, int dataOffset);

    // decode
    public abstract int audio_decode(byte[] data, int dataOffset,
                                     int dataLength, byte[] sample, int sampleLength);

}

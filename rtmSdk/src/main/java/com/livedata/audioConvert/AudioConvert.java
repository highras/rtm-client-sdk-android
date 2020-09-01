package com.livedata.audioConvert;

public class AudioConvert {
    public static native byte[] convertAmrwbToWav(byte[] amrSrc, int[] status, int[] wavSize);

    static {
        System.loadLibrary("audio-convert");
    }
}


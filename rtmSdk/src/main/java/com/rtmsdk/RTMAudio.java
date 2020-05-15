package com.rtmsdk;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import com.fpnn.sdk.proto.MessagePayloadPacker;
import com.livedata.audioConvert.AudioConvert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

class RTMAudioHeader {
    private byte version = 1;
    private ContainerType containerType = ContainerType.CodecInherent;
    private CodecType codecType = CodecType.AmrWb;
    private Map<String, Object> infoData = new TreeMap<>();
    private byte[] headerArray;

    public enum ContainerType {
        CodecInherent(0);
        private int value;

        ContainerType(int value) {
            this.value = value;
        }
    }

    public enum CodecType {
        AmrWb(1),
        AmrNb(2);
        private int value;

        CodecType(int value) {
            this.value = value;
        }
    }

    public RTMAudioHeader(byte version, ContainerType containerType, CodecType codecType, String lang, int duration, int sampleRate) {
        this.version = version;
        this.containerType = containerType;
        this.codecType = codecType;

        infoData.put("lang", lang);
        infoData.put("dur", duration);
        infoData.put("srate", sampleRate);
    }

    public RTMAudioHeader(String lang, int duration, int sampleRate) {
        infoData.put("lang", lang);
        infoData.put("dur", duration);
        infoData.put("srate", sampleRate);
    }

    public byte[] headerArray() throws IOException {
        MessagePayloadPacker packData = new MessagePayloadPacker();
        packData.pack(infoData);
        byte[] dataArray = packData.toByteArray();
        int mapLength = dataArray.length;
        byte[] infoDataLength = RTMUtils.intToByteArray(mapLength);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        os.write(version);    //语音数据结构版本
        os.write((byte) containerType.value);     //音频数据容器格式枚举值
        os.write((byte) codecType.value);        //编解码器枚举值
        os.write(1);    //附加信息条数

        os.write(infoDataLength);   //附加信息长度 msgpack后
        os.write(dataArray);        //附加信息内容 msgpack后

        return os.toByteArray();
    }
}

class AmrBroad implements Runnable {
    private Thread mDecodeThread;
    private AudioTrack mAudioTrack;
    private  int playerBufferSize = 0;
    private static final int SAMPLE_RATE = 16000;
    private short[] pcmData;
//    boolean isRunning = false;

    public void start(short[] data) {
        if (data== null)
            return;

        if (mAudioTrack != null && mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
            release();

        pcmData = data;

        playerBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playerBufferSize, AudioTrack.MODE_STREAM);

        mAudioTrack.play();
        mDecodeThread = new Thread(this);
        mDecodeThread.start();
    }

    public void stop() {
        mDecodeThread.interrupt();
//        isRunning = false;
    }

    private void release()
    {
        if(mAudioTrack == null)
            return;
        mAudioTrack.stop();
        mAudioTrack.release();
        mAudioTrack = null;
    }

    @Override
    public void run() {
        try {
            int idx = 0;
            while (mAudioTrack != null && !Thread.currentThread().isInterrupted()) {
                short[] tempBuffer = Arrays.copyOfRange(pcmData, idx, idx + playerBufferSize);
                // 播放
                if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
                    return;
                mAudioTrack.write(tempBuffer, 0, playerBufferSize);
                idx += playerBufferSize;
                if (idx >= pcmData.length)
                    break;
            }
            release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class RTMAudio {
    private IAudioAction audioAction = null;
    private String lang = "zh-CN";
    //    private AudioTrack mPlayer;
    private MediaRecorder mRecorder = null;
    private String audioDir = "";
    private File recordFile;
    private int minSampleRate = 16000;
    private int maxDurSeconds = 60;
    private int defaultBitRate = 16000;
    private int audioChannel = 1;
    private AmrBroad play = new AmrBroad();

    public short[] getRawData(byte[] amrSrc) {
        if (amrSrc == null)
            return null;

        int[] status = new int[1];
        int[] wavsize = new int[1];

        byte[] wavBuffer = AudioConvert.convertAmrwbToWav(amrSrc, status, wavsize);
        if (wavBuffer == null || status[0] != 0)
            return null;

        int channelCount = wavBuffer[22];
        int pos = 12;

        while (!(wavBuffer[pos] == 100 && wavBuffer[pos + 1] == 97 && wavBuffer[pos + 2] == 116 && wavBuffer[pos + 3] == 97)) {
            pos += 4;
            int chunkSize = wavBuffer[pos] + wavBuffer[pos + 1] * 256 + wavBuffer[pos + 2] * 65536 + wavBuffer[pos + 3] * 16777216;
            pos += 4 + chunkSize;
        }
        pos += 8;

        int samples = (wavBuffer.length - pos) / 2;
        short[] pcmData = new short[samples];

        int idx = 0;
        while (pos < wavBuffer.length) {
            pcmData[idx] = getShort(wavBuffer[pos], wavBuffer[pos + 1]);
            pos += 2;
            idx++;
        }
        return pcmData;
    }

    private  short getShort(byte firstByte, byte secondByte) {
        return (short) ((0xff & firstByte) | (0xff00 & (secondByte << 8)));
    }

    public String getRecordPath(){
        return recordFile.getAbsolutePath();
    }

    public interface IAudioAction {
        void startRecord();

        void stopRecord();

        void broadAudio();

        void broadFinish();
    }

    public void init(String lang, IAudioAction audioAction) {
        this.audioAction = audioAction;
        this.lang = lang;

        audioDir = Environment.getExternalStorageDirectory().getPath() + "/rtmCache/";

        File dir = new File(audioDir);
        if (!dir.exists())
            if (!dir.mkdirs()) {
                audioDir = "";
                return;
            }
        recordFile = new File(dir.getPath() + "/recoderAudio");
    }

    //    public static RTMAudio getInstance() {
//        return RTMAudioClassHolder.instance;
//    }
//
//    private static class RTMAudioClassHolder {
//        private static final RTMAudio instance = new RTMAudio();
//    }
    private int getAudioTime() {
        int length;
        MediaPlayer tmp = new MediaPlayer();
        try {
            tmp.setDataSource(recordFile.getPath());
            tmp.prepare();
            length = tmp.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
            tmp.release();
            return 0;
        }
        tmp.release();
        return length;
    }

    public byte[] genAudioData() throws IOException {
        byte[] audio = fileToByteArray();
        if (audio == null)
            return null;
        final int audioDur = getAudioTime();

        RTMAudioHeader tt = new RTMAudioHeader(lang, audioDur, minSampleRate);
        byte[] header = tt.headerArray();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(header);        //附加信息内容 msgpack后
        os.write(audio);      //语音内容

        return os.toByteArray();
    }

    private byte[] fileToByteArray() {
        byte[] data;
        try {
            FileInputStream fis = new FileInputStream(recordFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    public static byte[] unpackAudioData(byte[] audioDta) {
//        byte[] data = Arrays.copyOfRange(audioDta, 3, audioDta.length);//刨除前3字节
        int pos = 4;

        ByteArrayInputStream ss = new ByteArrayInputStream(audioDta);

        int extNum = audioDta[3];
        for (int i = 0; i < extNum; i++) {
            byte[] extraLength = Arrays.copyOfRange(audioDta, pos, pos + 4);//4个字节附加内容长度
            pos += 4;
            int len = RTMUtils.byteArrayToInt(extraLength);
            byte[] extraContent = Arrays.copyOfRange(audioDta, pos, pos + len);//附加内容
            pos += len;
        }

        return Arrays.copyOfRange(audioDta, pos, audioDta.length);//音频数据
    }

    public void startRecord() {
        if (recordFile.exists())
            recordFile.delete();
        try {
            recordFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (mRecorder != null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        mRecorder.setMaxDuration(maxDurSeconds * 1000);//设置录音最大时长
        mRecorder.setAudioSamplingRate(minSampleRate);  //采样率16K
        mRecorder.setAudioChannels(audioChannel);//单声道
        mRecorder.setAudioEncodingBitRate(defaultBitRate);//比特率16bit
        mRecorder.setOutputFile(recordFile.getPath());

        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
                    mr.stop();
            }
        });

        try {
            mRecorder.prepare();
            mRecorder.start();
            if (audioAction != null)
                audioAction.startRecord();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (mRecorder == null)
            return;
        try {
            mRecorder.stop();
            if (audioAction != null)
                audioAction.startRecord();
        } catch (IllegalStateException e) {
        }

        mRecorder.release();
        mRecorder = null;
        if (audioAction != null)
            audioAction.stopRecord();
//        audioDur = System.currentTimeMillis() - audioDur;
    }

    public void writeWavFile(byte[] amrData, File file)
    {
        int[] status = new int[1];
        int[] wavsize = new int[1];
        byte[] wavBuffer = AudioConvert.convertAmrwbToWav(amrData, status, wavsize);
        if (wavBuffer == null)
            return;
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(wavBuffer);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void broadAduio(byte[] amrData) {
        short[] data = getRawData(amrData);
        play.start(data);
    }

    public void broadAduio() {
        broadAduio(fileToByteArray());
    }

    public void stopAduio() {
        play.stop();
    }
}

package com.rtm.test;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rtmsdk.RTMAudio;
import com.rtmsdk.RTMClient;
import com.rtmsdk.RTMStruct;
import com.rtmsdk.TranscribeLang;
import com.rtmsdk.UserInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    Chronometer timer;
    Context mycontext = this;
    int REQUEST_CODE_CONTACT = 101;
    File recordFile;
    //    RTMUtils.audioUtils1 audioManage = RTMUtils.audioUtils1.getInstance();
    RTMAudio audioManage = new RTMAudio();

    TestClass ceshi;
    final String[] buttonNames = {"chat", "message", "history", "friend", "group", "room", "file", "data", "system", "user", "stress"};
    Map<Integer, String> testButtons = new HashMap<Integer, String>();

    public void testAduio(final byte[] audioData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audioData != null && ceshi != null)
                    ceshi.startAudioTest(audioData);
                else
                    mylog.log("audioData is null");
            }
        }).start();
    }

    public void startTest(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ceshi != null) {
                        if (name == "stress")
                            ;
//                           ceshi.startStress();
                        else
                            ceshi.startCase(name);
                    }
                    else{
                        mylog.log("ceshi is null");
                    }
                } catch (InterruptedException e) {
                    mylog.log("startTest:" + name + " exception:" + e.getMessage());
                }
            }
        }).start();
    }

    public static byte[] toByteArray(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (true) {
            try {
                if (!(-1 != (n = input.read(buffer)))) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    class AudioButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.record:
                    timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                    timer.start();
                    audioManage.startRecord();
                    break;
                case R.id.stopAudio:
                    timer.stop();
                    audioManage.stopRecord();
//                    try {
//                        testAduio(audioManage.genAudioData());
//                    } catch (Exception e) {
//                        mylog.log("hehe:" + e.getMessage());
//                    }
                    break;
                case R.id.broadAudio:
                    timer.stop();
                    audioManage.stopRecord();
//                    audioManage.broadRecoder(getResources().openRawResource(R.raw.demo));
//                    audioManage.broadRecoder(toByteArray((getResources().openRawResource(R.raw.demo))));
                    audioManage.broadAduio();
                    break;
                case R.id.stopBroad:
                    audioManage.stopAduio();
                    break;
            }
        }
    }

    class reloginStartFunc implements UserInterface.IReloginStart{
        @Override
        public boolean reloginWillStart(long uid, RTMStruct.RTMAnswer answer, int reloginCount) {
            mylog.log("重连第 " + reloginCount + "次, 结果 :" + answer.getErrInfo() );
            return true;
        }
    }

    class reloginCompleteFunc implements UserInterface.IReloginCompleted{
        @Override
        public void reloginCompleted(long uid, boolean successfulm, RTMStruct.RTMAnswer answer, int reloginCount) {
            mylog.log("重连结束 结果 " + answer.getErrInfo() + " 重连次数 "+ reloginCount);
        }
    }

    class TestButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (testButtons.containsKey(v.getId())) {
                String testName = testButtons.get(v.getId());
                startTest(testName);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CONTACT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 判断该权限是否已经授权
                boolean grantFlas = false;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        //-----------存在未授权-----------
                        grantFlas = true;
                    }
                }

                if (grantFlas) {
                    //-----------未授权-----------
                    // 判断用户是否点击了不再提醒。(检测该权限是否还可以申请)
                    // shouldShowRequestPermissionRationale合理的解释应该是：如果应用之前请求过此权限
                    //但用户拒绝了请求且未勾选"Don’t ask again"(不在询问)选项，此方法将返回 true。
                    //注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中勾选了
                    //"Don’t ask again" 选项，此方法将返回 false。如果设备规范禁止应用具有该权限，此方法会返回 false。
                    boolean shouldShowRequestFlas = false;
                    for (String per : permissions) {
                        if (shouldShowRequestPermissionRationale(per)) {
                            //-----------存在未授权-----------
                            shouldShowRequestFlas = true;
                        }
                    }
                } else {
                    //-----------授权成功-----------
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void startTestCase(final long pid, final long uid, final String token, final String endpoint) {
        ceshi = new TestClass(pid,uid,token,endpoint);
        ceshi.client  = new RTMClient(endpoint, pid, uid, new RTMExampleQuestProcessor());
        ceshi.client.setAutoConnect(this,new reloginStartFunc(), new reloginCompleteFunc());

        //test for push another rtm-client//
/*
        ceshi.pushUserTokens = new HashMap<Long, String>() {
            {
                put(101L, "80E21F51FF13AABBCEE821096ED402E4");
            }
        };
        for (final long testuid : ceshi.pushUserTokens.keySet()) {
            RTMClient rtmUser = new RTMClient(endpoint, pid, testuid, new RTMExampleQuestProcessor());
            rtmUser.setAutoConnect(this,new reloginStartFunc(), new reloginCompleteFunc());
            ceshi.addClients(testuid, rtmUser);
        }
*/

        RTMAudio.getInstance().init(getExternalCacheDir(), TranscribeLang.ZH_CN,null);
        byte[] audioData = null;
        try {
            InputStream inputStream=getAssets().open("AudioDemo");
            FileOutputStream outfile = new FileOutputStream(RTMAudio.getInstance().getRecordFile());
            audioData = toByteArray(inputStream);
            outfile.write(audioData);
            outfile.close();
            inputStream.close();
            ceshi.audioFile = RTMAudio.getInstance().getRecordFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ceshi.loginRTM();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        NetStateReceiver stateReceiver = new NetStateReceiver();
//        this.registerReceiver(new netchange(),intentFilter);

        audioManage.init(getExternalCacheDir(),TranscribeLang.EN_US, null);
        TestButtonListener testButtonListener = new TestButtonListener();
        AudioButtonListener audioButtonListener = new AudioButtonListener();


        for (String name : buttonNames) {
            int buttonId = getResources().getIdentifier(name, "id", getBaseContext().getPackageName());
            Button button = $(buttonId);
            button.setOnClickListener(testButtonListener);
            testButtons.put(buttonId, name);
        }

        timer = $(R.id.timer);
        Button startRecoder = $(R.id.record);
        Button broadcast = $(R.id.broadAudio);
        Button stopAudio = $(R.id.stopAudio);
        Button stopBroad = $(R.id.stopBroad);

        startRecoder.setOnClickListener(audioButtonListener);
        stopAudio.setOnClickListener(audioButtonListener);
        broadcast.setOnClickListener(audioButtonListener);
        stopBroad.setOnClickListener(audioButtonListener);


        Properties properties = new Properties();
        try {
            InputStream in = this.getAssets().open("properties");
            properties.load(in);
            in.close();
        } catch (IOException e) {
            mylog.log("read properties file error");
            return;
        }
        final long uid = Long.parseLong(properties.getProperty("uid"));
        final String token = properties.getProperty("usertoken");
        final String endpoint = properties.getProperty("endpoint");
        final long pid = Long.parseLong(properties.getProperty("pid"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                startTestCase(pid, uid, token, endpoint);
            }
        }).start();
    }
}

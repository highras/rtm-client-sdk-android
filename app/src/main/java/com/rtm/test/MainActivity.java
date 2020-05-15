package com.rtm.test;

import android.Manifest;
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

import com.fpnn.sdk.ErrorCode;
import com.rtmsdk.RTMAudio;
import com.rtmsdk.RTMClient;
import com.rtmsdk.UserInterface;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
                            ceshi.startStress();
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
                    try {
                        testAduio(audioManage.genAudioData());
                    } catch (Exception e) {
                        mylog.log("hehe:" + e.getMessage());
                    }
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

    public void startTestCase(final long pid, final long uid, final String token, final String endpoint) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ceshi = new TestClass(pid, uid, token, endpoint);
                if (ceshi.client == null) {
                    mylog.log("TestClass prepare error:");
                    return;
                }

                class CodeCallback implements UserInterface.ErrorCodeCallback {
                    @Override
                    public void call(int errorCode) {
                        if (errorCode == ErrorCode.FPNN_EC_OK.value())
                            mylog.log("rtmUser login RTM success");
                        else
                            mylog.log("rtmUser login RTM error:" + errorCode);
                    }
                }
                RTMExampleQuestProcessor processorTest = new RTMExampleQuestProcessor();
                CodeCallback callbackTest = new CodeCallback();

                //test for push another rtm-client//
                Map<Long, String> users = new HashMap<Long, String>() {
                    {
                        put(101L, "A8427907274E6A11771ED877D3EC5B73");
//                        put(9529L, "96C96410CF447F68AFCD8A9045502493");
//                        put(9530L, "5F1A33C5768E6A644100918D0B2D679C");
//                        put(9531L, "1D297088A0C534199A25DDC49136C1A8");
//                        put(9532L, "3E6D527567F35CB12ED42048819D832E");
                    }
                };

                for (long uid : users.keySet()) {
                    RTMClient rtmUser = new RTMClient(endpoint, pid, uid, new RTMExampleQuestProcessor());
                    boolean lgonStatus = rtmUser.login(callbackTest, users.get(uid));
                    if (!lgonStatus)
                        mylog.log("rtmUser login RTM error");
                    ceshi.addClients(uid, rtmUser);
                }
            }
            //test for push another rtm-client//
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
//        jiamitest();
//        if (true)
//            return;

        audioManage.init("zh-CN", null);
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
        startTestCase(pid, uid, token, endpoint);
//        jiamitest();
    }
}

package com.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rtm.R;

import java.io.IOException;
import java.io.InputStream;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        System.out.println(new String("Test with activity!"));

        // case 1
        this.baseTest();
    }

    public void baseTest() {

        byte[] file = this.loadKey("key/test-secp256k1-public.der");
        byte[] bytes = this.loadKey("key/test-secp256k1-public.der-false");

        new TestCase(bytes, file);
    }

    private byte[] loadKey(String derPath) {

        byte[] bytes = new byte[0];
        InputStream inputStream = null;

        try {

            inputStream = getAssets().open(derPath);

            int size = inputStream.available();
            bytes = new byte[size];

            inputStream.read(bytes);
            inputStream.close();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return bytes;
    }
}

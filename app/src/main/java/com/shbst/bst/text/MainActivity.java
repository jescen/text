package com.shbst.bst.text;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "locale";
    // 获取文件根目录
    public static final String ThemePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    // 视频资源目录
    public static final String VIDEO_PATH = "/Movies/";
    // 默认播放视频
    public static final String DefaultPath = ThemePath+VIDEO_PATH+"KONE.mp4";
    private EventBus eventBus = EventBus.getDefault();
    ConfigurationParams configurationParams;
    InputStream is = null;
    TextView params;
    StringBuffer sb = new StringBuffer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventBus.register(this);
        configurationParams = new ConfigurationParams(MainActivity.this);
        params = (TextView) findViewById(R.id.params);
        try {
            is = getAssets().open("mediascreen.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread.start();

    }
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            configurationParams.parseXML(is);
        }
    });

    public void onEventMainThread(ConfigurationParams.Params event) {
        sb.append(event.toString()+"\n");
        params.setText(sb);
        Log.i(TAG, "onEventMainThread: "+event.toString());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

}

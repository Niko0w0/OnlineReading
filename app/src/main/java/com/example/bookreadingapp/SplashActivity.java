package com.example.bookreadingapp;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends Activity {

    public static final int CODE = 1001;
    public static final int TOTAL_TIME = 5000;
    public static final int INTERVAL_TIME = 1000;
    private TextView mTextView;

    // 动态申请权限
    private static final int REQUEST_PERMISSION_CODE = 0;
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入数据权限
            Manifest.permission.READ_EXTERNAL_STORAGE,// 读取数据权限
            Manifest.permission.ACCESS_FINE_LOCATION,// 定位权限
            Manifest.permission.ACCESS_COARSE_LOCATION // 获取基站的服务信号权限，以便获取位置信息
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        try {
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
            Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            Object sVmRuntime = getRuntime.invoke(null);
            setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{new String[]{"L"}});
        } catch (Throwable e) {
            Log.e("[error]", "reflect bootstrap failed:", e);
        }


        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://www.google.com", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


        //倒计时
        mTextView = (TextView) findViewById(R.id.time_text_view);

        MyHandler handler = new MyHandler(this);
        Message message = Message.obtain();
        message.what = CODE;
        message.arg1 = TOTAL_TIME;
        handler.sendMessage(message);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳到下一页面
                BookListActivity.start(SplashActivity.this);
                SplashActivity.this.finish();
                handler.removeMessages(CODE);
            }
        });
    }

    public static class MyHandler extends Handler {

        public final WeakReference<SplashActivity> mWeakReference;

        public MyHandler(SplashActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = mWeakReference.get();
            if (msg.what == CODE) {


                if (activity != null) {
                    // 设置textview，更新UI
                    int time = msg.arg1;
                    activity.mTextView.setText(time / INTERVAL_TIME + "秒  点击跳过");
                    // 发送倒计时

                    Message message = Message.obtain();
                    message.what = CODE;
                    message.arg1 = time - INTERVAL_TIME;

                    if (time > 0) {
                        sendMessageDelayed(message, INTERVAL_TIME);
                    } else {
                        //跳到下一页面
                        BookListActivity.start(activity);
                        activity.finish();
                    }
                }
            }
        }

    }
}


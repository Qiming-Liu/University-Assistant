package com.pross;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pross.object.PowerRate;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static TextView print;
    static Button open;
    Button close;
    Button log;

    public static Boolean isClosed = true;
    static RequestQueue requestQueues;
    static MainActivity mainActivity;

    public static boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;

        //绑定view
        print = findViewById(R.id.textView);
        open = findViewById(R.id.open);
        close = findViewById(R.id.close);
        log = findViewById(R.id.log);

        NoHttp.initialize(this);
        Logger.setDebug(true);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setTag("NoHttp:");// 打印Log的tag。
        requestQueues = NoHttp.newRequestQueue();

        FontStyle fontStyle = new FontStyle(this, "yh.ttf");
        fontStyle.setTypeface(print, false);
    }

    @Override
    public void onBackPressed() {
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        if(!isClosed){
            close.setEnabled(false);
            isClosed = true;
            print("关闭服务中");
            exit = true;
        } else {
            stopApp();
        }
    }

    public void open(View v) {
        open.setEnabled(false);
        close.setEnabled(true);
        isClosed = false;
        OpenServer.start();

        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    public void close(View v) {
        close.setEnabled(false);
        isClosed = true;
        print("关闭服务中");

        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    public void log(View v) {
        String url = "https://www.jhuncloud.com/applog";
        String txt = System.currentTimeMillis() / 1000 + ".txt";
        //限制上传log大小在1M以内
        String log = print.getText().toString();
        if (log.length() > 1000000) log = log.substring(0, 900000);
        //开始上传请求
        Request<String> stringPostRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
        //构建上传参数
        stringPostRequest.add("log", log);
        stringPostRequest.add("txt", txt);
        MainActivity.requestQueues.add(2, stringPostRequest, new SimpleResponseListener<String>() {
            @Override
            public void onStart(int what) {
                MainActivity.print("开始发送log信息");
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                MainActivity.print("发送完毕:" + txt);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }

            @Override
            public void onFinish(int what) {
            }
        });
    }

    public static void print(String s) {
        mainActivity.runOnUiThread(() -> print.setText(s + "\n" + print.getText()));
    }

    public static boolean isJhunWIFI() {
        WifiManager wifiMgr = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        if (wifiId == "JHUN-AUTO") return true;//填入江大wifi名称
        return false;
    }

    public static boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static void openButton(){
        mainActivity.runOnUiThread(() -> open.setEnabled(true));
    }

    public static void stopApp(){
        mainActivity.runOnUiThread(() -> mainActivity.finish());
    }
}
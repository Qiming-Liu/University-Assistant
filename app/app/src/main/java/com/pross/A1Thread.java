package com.pross;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.pross.object.PowerRate;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class A1Thread extends Thread {

    final public static String a1 = "http://94.191.42.64:1234/xzs/transa1";
    public static List<String> ListPower = new ArrayList();

    @Override
    public void run() {

        try {
            //如果查询队列为空，加入一个110的空查询
            if(!(ListPower.size() > 0)){
                ListPower.add("110");
            } else{
                MainActivity.print("A1Thread:开始电费查询" + ListPower.get(0));
            }

            //获得结果
            PowerRate pr = HtmlUnit.a1get(ListPower.get(0));

            //从查询队列中删除
            ListPower.remove(0);

            //构建上传参数
            ArrayList<NameValuePair> NVPdata = new ArrayList<NameValuePair>();
            NameValuePair NVPqsh = new BasicNameValuePair("qsh", ListPower.get(0));
            NVPdata.add(NVPqsh);
            NVPdata.addAll(pr.toNVP());

            //开始上传请求
            MainActivity.print("A1Thread:上传" + NVPdata.toString());
            new HttpThread(a1,NVPdata,new Handler(){
                @Override
                public void handleMessage(Message result) {

                    //获得json对象
                    JSONObject js = JSONObject.parseObject(result.getData().getString("Post"));

                    //如果返回的查询不为110，此查询请求加入查询列表
                    String qsh = js.getString("qsh");
                    if(!qsh.equals("110")){
                        ListPower.add(qsh);
                        MainActivity.print("A1Thread:收到查询请求" + qsh);
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

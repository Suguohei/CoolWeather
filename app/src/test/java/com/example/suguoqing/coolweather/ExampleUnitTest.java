package com.example.suguoqing.coolweather;

import android.util.Log;

import com.example.suguoqing.coolweather.db.City;
import com.example.suguoqing.coolweather.gson.Weather;
import com.example.suguoqing.coolweather.util.HttpUtil;
import com.example.suguoqing.coolweather.util.Utillity;

import org.junit.Test;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = "ExampleUnitTest";
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testHandleWeatherResponse(){
        String address = "https://free-api.heweather.com/s6/weather?location=CN101010100&key=af7488dedc3c47a39f4c643aa4eb8a69";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               // Log.d(TAG, "onFailure: ______________________________失败");
                System.out.print("hdhdhd");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Weather weather = Utillity.handleWeatherResponse(responseText);
                //注意在单元测试中是不能使用log的，可以使用java中的system.out.print
               // Log.d(TAG, "onResponse: ++++++++++++++++++++++++++"+weather);
                System.out.print("hasdhhhf");
                System.out.print(weather);
            }
        });


       // System.out.print("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
    }

    /***
     * 测试能不能将获取的数据解析
     */
    @Test
    public void testGetJson(){

        try {
            String address = "https://free-api.heweather.com/s6/weather?location=CN101010100&key=af7488dedc3c47a39f4c643aa4eb8a69";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(address).build();
            Response response = client.newCall(request).execute();
            String responseText = response.body().string();
            System.out.print(responseText);
            System.out.print("------------------------------------------------------"+"\n");
            Weather weather = Utillity.handleWeatherResponse(responseText);
            System.out.print(weather);
            System.out.print("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
package com.example.suguoqing.coolweather;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.suguoqing.coolweather.gson.Weather;
import com.example.suguoqing.coolweather.util.Utillity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.suguoqing.coolweather", appContext.getPackageName());
    }


    /*****
     *在单元测试中遇见问题，就是报异常：java.lang.RuntimeException:
     * Method put in org.json.JSONObject not mocked.
     * See https://sites.google.com/a/android.com/tools/tech-docs/unit-testing-support for details.
     *
     * 但是将测试代码放到设备AndroidTest中，使用JSONObject代码时无异常。


     原来，JSON包含在Android SDK中，JUnit单元测试无法使用，会抛异常；
     但可以在AndroidTest中使用，如果要在Junit中使用，需要在App或Library项目的build.gradle中添加依赖：

     testCompile files('libs/json.jar')
     下载json     jar包，导入libs文件夹中
     就可以在单元测试中使用json解析了
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

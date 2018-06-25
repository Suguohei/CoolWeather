package com.example.suguoqing.coolweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suguoqing.coolweather.gson.Daily_forecast;
import com.example.suguoqing.coolweather.gson.Life_style;
import com.example.suguoqing.coolweather.gson.Weather;
import com.example.suguoqing.coolweather.util.HttpUtil;
import com.example.suguoqing.coolweather.util.Utillity;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView typeText;
    private TextView brfText;
    private TextView txtText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化各种控件
        scrollView = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        typeText = findViewById(R.id.type_text);
        brfText = findViewById(R.id.brf_text);
        txtText = findViewById(R.id.txt_text);
        //缓存
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //如果又缓存的话，就直接的从缓存中拿数据
            Weather weather = Utillity.handleWeatherResponse(weatherString);
            //显示这个weather
            showWeatherInfo(weather);
        }else {
            //如果没有缓存的话，就查询
            //这个页面是从上一个选择城市的页面跳转过来的，所以可以根据intent来获取weatherid
            String weatherid = getIntent().getStringExtra("weatherid");
            scrollView.setVisibility(View.INVISIBLE);
            //根据id来请求数据
            requsetWeather(weatherid);
        }
    }

    /*******
     * 根据id来请求天气信息
     * @param weatherid
     */
    private void requsetWeather(String weatherid) {

        String address = "https://free-api.heweather.com/s6/weather?location="+weatherid+"&key=af7488dedc3c47a39f4c643aa4eb8a69";

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utillity.handleWeatherResponse(responseText);
                //开启一个线程来更新ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.getStatus())){
                            //添加缓存
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();

                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    /*****
     * 将天气实体类信息，显示在布局中
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        //城市信息
        String cityName = weather.getBasic().getLocation();
        String cnty = weather.getBasic().getCnty();
        String lat = weather.getBasic().getLat();
        String lon = weather.getBasic().getLon();
        String tz = weather.getBasic().getTz();
        titleCity.setText(cityName);
        titleUpdateTime.setText(tz);

        //更新信息
        String loc = weather.getUpdate().getLoc();//更新时间
        titleUpdateTime.setText(loc);

        //当前天气
        String cloud = weather.getNow().getCloud();//云量
        String cond_txt = weather.getNow().getCond_txt();//实时天气状况
        String fl = weather.getNow().getFl();//体感温度
        String hum = weather.getNow().getHum();//相对湿度
        String pcpn = weather.getNow().getPcpn();//降水量
        String pres = weather.getNow().getPres();//大气压
        String tmp = weather.getNow().getTmp();//温度
        String vis = weather.getNow().getVis();//能见度
        String wind_dir = weather.getNow().getWind_dir();//风向
        String wind_sc = weather.getNow().getWind_sc();//风力
        String wind_spd = weather.getNow().getWind_spd();//风速
        degreeText.setText(tmp+"摄氏度 C");
        weatherInfoText.setText(cond_txt);



        //未来天气
        for(Daily_forecast forecast : weather.getForecasts()){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);

            TextView dateText = view.findViewById(R.id.data_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);

            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getCond_txt_d());
            maxText.setText(forecast.getTmp_max());
            minText.setText(forecast.getTmp_mix());

            forecastLayout.addView(view);
        }

        //小提示
        for(int i = 0;i<weather.getSuggestions().size();i++){
            Life_style life_style = weather.getSuggestions().get(0);
            String type = life_style.getType();
            String brf = life_style.getBrf();
            String txt = life_style.getTxt();

            typeText.setText(type);
            brfText.setText(brf);
            txtText.setText(txt);
        }

        scrollView.setVisibility(View.VISIBLE);

    }
}
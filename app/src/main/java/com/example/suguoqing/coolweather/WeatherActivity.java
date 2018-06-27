package com.example.suguoqing.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    //每日必应图片
    private ImageView image;

    //添加下拉刷新功能
    public SwipeRefreshLayout swipeRefresh;

    //手动选择天气
    public DrawerLayout drawerLayout;

    //按钮
    public Button navbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实现状态栏和背景融合一体的效果
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
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
        //每日必应图片
        image = findViewById(R.id.image);

        //下拉刷新功能
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        //
        drawerLayout = findViewById(R.id.drawer_layout);
        navbutton = findViewById(R.id.nav_button);

        navbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        //缓存
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        //从缓存拿weatherId
        final String weatherId;
        if(weatherString != null){
            //如果又缓存的话，就直接的从缓存中拿数据
            Weather weather = Utillity.handleWeatherResponse(weatherString);
            weatherId = weather.getBasic().getCid();
            //显示这个weather
            showWeatherInfo(weather);
        }else {
            //如果没有缓存的话，就查询
            //这个页面是从上一个选择城市的页面跳转过来的，所以可以根据intent来获取weatherid
           weatherId = getIntent().getStringExtra("weatherid");
            scrollView.setVisibility(View.INVISIBLE);
            //根据id来请求数据
            requsetWeather(weatherId);
        }

        //设置下拉刷新的监听
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requsetWeather(weatherId);
            }
        });

        //缓存中加载图片
        String imageString = prefs.getString("image",null);
        if(imageString != null){
            //如果不为空，就从缓存中加载图片
            Glide.with(this).load(imageString).into(image);

        }else{
            //如果为空，就从服务器加载图片
            loadBingPic();
        }
    }


    //从服务器加载图片
    private void loadBingPic() {
        String requsetBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requsetBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "必应图片加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                //将图片写入缓存
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                        .edit();
                editor.putString("image",bingPic);
                editor.apply();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(image);
                    }
                });
            }
        });
    }

    /*******
     * 根据id来请求天气信息
     * @param weatherid
     */
    public void requsetWeather(String weatherid) {

        String address = "https://free-api.heweather.com/s6/weather?location="+weatherid+"&key=af7488dedc3c47a39f4c643aa4eb8a69";

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();

                        //刷新完成
                        swipeRefresh.setRefreshing(false);
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

                        //刷新完成
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        //加载必应图片
        loadBingPic();

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
        degreeText.setText(tmp+"℃");
        weatherInfoText.setText(cond_txt);


        //为了刷新的时候，先清空
        forecastLayout.removeAllViews();
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

package com.example.suguoqing.coolweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.suguoqing.coolweather.gson.Daily_forecast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;

public class Details extends AppCompatActivity {
    private static final String TAG = "Details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        Daily_forecast forecast = (Daily_forecast) intent.getSerializableExtra("forecast");
        Log.d(TAG, "onCreate: ++++++++++++++++++++"+forecast);

        TextView cond_txt = findViewById(R.id.cond_txt_d);
        TextView date = findViewById(R.id.date);
        TextView hum = findViewById(R.id.hum);
        TextView pcpn = findViewById(R.id.pcpn);
        TextView pop = findViewById(R.id.pop);
        TextView pres = findViewById(R.id.pres);
        TextView max = findViewById(R.id.tmp_max);
        TextView min = findViewById(R.id.tmp_mix);
        TextView vis = findViewById(R.id.vis);
        TextView dir = findViewById(R.id.wind_dir);
        TextView sc = findViewById(R.id.wind_sc);
        TextView spd = findViewById(R.id.wind_spd);

        cond_txt.setText("天气："+forecast.getCond_txt_d());
        date.setText("日期："+forecast.getDate());
        hum.setText("相对湿度："+forecast.getHum());
        pcpn.setText("降水量："+forecast.getPcpn());
        pop.setText("降水概率："+forecast.getPop());
        pres.setText("大气压强："+forecast.getPres());
        max.setText("最高温度："+forecast.getTmp_max());
        min.setText("最低温度："+forecast.getTmp_mix());
        vis.setText("能见度："+forecast.getVis());
        dir.setText("风向："+forecast.getWind_dir());
        sc.setText("风力："+forecast.getWind_sc());
        spd.setText("风速："+forecast.getWind_spd());

        /*Intent intent = getIntent();
        Daily_forecast forecast = (Daily_forecast) intent.getSerializableExtra("forecast");
       // Log.d(TAG, "onCreate: __________________0"+forecast);
        Map<String,String> map = new HashMap<>();
        map.put("天气：",forecast.getCond_txt_d());
        map.put("日期：",forecast.getDate());
        map.put("相对湿度：",forecast.getHum());
        map.put("降水量：",forecast.getPcpn());
        map.put("降水概率：", forecast.getPop());
        map.put("大气压强：",forecast.getPres());
        map.put("最高温度：",forecast.getTmp_max());
        map.put("最低温度：",forecast.getTmp_mix());
        map.put("能见度：",forecast.getVis());
        map.put("风向：",forecast.getWind_dir());
        map.put("风力：",forecast.getWind_sc());
        map.put("风速：",forecast.getWind_spd());

        Set<String> set = map.keySet();

        for(String key : set){
            //name.setText("name");
           // content.setText(map.get(key));
        }*/


        /*private String cond_txt_d;
    private String date;
    private String hum;
    private String pcpn;
    private String pop;//降水概率
    private String pres;
    private String tmp_max;
    private String tmp_mix;
    private String vis;
    private String wind_dir;
    private String wind_sc;
    private String wind_spd;*/



    }
}

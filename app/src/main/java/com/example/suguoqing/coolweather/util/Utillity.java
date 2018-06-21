package com.example.suguoqing.coolweather.util;

import android.text.TextUtils;

import com.example.suguoqing.coolweather.db.City;
import com.example.suguoqing.coolweather.db.County;
import com.example.suguoqing.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utillity {
    /***
     * 解析和处理省级数据
     */
    public static boolean handleProvinceResponse(String response){

        if(!TextUtils.isEmpty(response)){

            try {
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0; i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();

                }
                return true;//就是数据填充完成，返回true
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return true;
    }


    /*解析和处理服务器返回的市级数据*/
    public static boolean handleCityResponse(String response,int provinceId){

        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCitys = new JSONArray(response);
                for(int i = 0; i<allCitys.length();i++){
                    JSONObject cityObject = allCitys.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /***
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty = new JSONArray(response);
                for(int i = 0; i<allCounty.length();i++){
                    JSONObject cityObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setWeatherId(cityObject.getString("weather_id"));
                    county.setCountyName(cityObject.getString("name"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}

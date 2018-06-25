package com.example.suguoqing.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Weather {
    private String status;
    private Basic basic;
    private Now now;
    private Update update;

    @SerializedName("daily_forecast")
    private ArrayList<Daily_forecast> forecasts;

    @SerializedName("lifestyle")
    private ArrayList<Life_style> suggestions;



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public Now getNow() {
        return now;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public ArrayList<Daily_forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(ArrayList<Daily_forecast> forecasts) {
        this.forecasts = forecasts;
    }


    @Override
    public String toString() {
        return "Weather{" +
                "status='" + status + '\'' +
                ", basic=" + basic +
                ", now=" + now +
                ", update=" + update +
                ", forecasts=" + forecasts +
                ", suggestions=" + suggestions +
                '}';
    }

    public ArrayList<Life_style> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(ArrayList<Life_style> suggestions) {
        this.suggestions = suggestions;
    }
}

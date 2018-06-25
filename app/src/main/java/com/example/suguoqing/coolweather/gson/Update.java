package com.example.suguoqing.coolweather.gson;

public class Update {

    private String loc;//更新时间

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "Update{" +
                "loc='" + loc + '\'' +
                '}';
    }
}

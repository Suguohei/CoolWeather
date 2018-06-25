package com.example.suguoqing.coolweather.gson;

public class Life_style {

    private String type;
    private String brf;
    private String txt;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrf() {
        return brf;
    }

    public void setBrf(String brf) {
        this.brf = brf;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }


    @Override
    public String toString() {
        return "Life_style{" +
                "type='" + type + '\'' +
                ", brf='" + brf + '\'' +
                ", txt='" + txt + '\'' +
                '}';
    }
}

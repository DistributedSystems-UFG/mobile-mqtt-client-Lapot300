package com.example.basicandroidmqttclient;

import java.util.Date;

public class RegistredTemp {
    private Date date;
    private int temp;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}

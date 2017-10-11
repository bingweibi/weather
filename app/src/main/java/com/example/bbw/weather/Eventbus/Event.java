package com.example.bbw.weather.Eventbus;


/**
 * Created by bbw on 2017/9/19.
 */

public class Event {

    private String message;

    public Event(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}

package com.example.bbw.weather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bbw on 2017/9/13.
 */

public class Suggestion {

    public Comf comf;

    public class Comf {
        @SerializedName("txt")
        public String descriable;
    }
}

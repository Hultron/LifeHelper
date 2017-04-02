package com.hultron.lifehelper.gson.weather;


import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;

public class Suggestion {

    public Comfort comfort;

    public CarWash carWash;

    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}

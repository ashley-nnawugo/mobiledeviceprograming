package com.example.cw2_fitnesstracker;

import android.location.Location;

public interface ICallback {
    public void runCounter(int speed, int duration, int distance);
}

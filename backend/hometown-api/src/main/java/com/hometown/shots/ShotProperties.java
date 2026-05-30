package com.hometown.shots;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ShotProperties {

    private final String dir;

    public ShotProperties(@Value("${hometown.shots.dir:E:/HomeTown/.run/shots}") String dir) {
        this.dir = dir;
    }

    public String getDir() { return dir; }
}

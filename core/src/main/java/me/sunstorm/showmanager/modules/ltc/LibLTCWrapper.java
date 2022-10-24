package me.sunstorm.showmanager.modules.ltc;

public class LibLTCWrapper {

    public native void init(int sampleRate, int frameRate);

    public native void setTime(int hour, int min, int sec, int frame);

    public native byte[] getData();

    public native void free();
}

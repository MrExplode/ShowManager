package me.mrexplode.timecode.fileio;

import me.mrexplode.timecode.Timecode;

public class Music {
    
    public Timecode startingTime;
    public String file;
    public long length;
    
    @Override
    public String toString() {
        String name;
        if (file.contains("/")) {
            name = file.split("/")[file.split("/").length - 1];
        } else if (file.contains("\\")) {
            name = file.split("\\\\")[file.split("\\\\").length - 1];
        } else {
            name = file;
        }
        return startingTime.toString() + " " + name;
        
    }

}

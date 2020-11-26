package me.mrexplode.showmanager.fileio;

import lombok.Getter;
import lombok.Setter;
import me.mrexplode.showmanager.util.Timecode;

@Getter
@Setter
public class Music {
    private Timecode startingTime;
    private String file;
    private long length;
    
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

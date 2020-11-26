package me.mrexplode.showmanager.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArraySegment {
    
    private int id;
    private int max;
    private float[] data;

}

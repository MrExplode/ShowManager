package me.mrexplode.timecode;


public class ArraySegment {
    
    private int id;
    private int max;
    private float[] data;
    
    public ArraySegment(int id, int max, float[] data) {
        this.id = id;
        this.max = max;
        this.data = data;
    }

    
    public int getId() {
        return id;
    }

    
    public void setId(int id) {
        this.id = id;
    }

    
    public int getMax() {
        return max;
    }

    
    public void setMax(int max) {
        this.max = max;
    }

    
    public float[] getData() {
        return data;
    }

    
    public void setData(float[] data) {
        this.data = data;
    }

}

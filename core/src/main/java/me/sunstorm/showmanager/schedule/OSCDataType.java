package me.sunstorm.showmanager.schedule;


public enum OSCDataType {
    
    INTEGER,
    BOOLEAN,
    FLOAT,
    STRING;
    
    public static Object castTo(String value, OSCDataType to) {
        switch (to) {
            case BOOLEAN:
                return Boolean.valueOf(value);
            case FLOAT:
                return Float.valueOf(value);
            case INTEGER:
                return Integer.valueOf(value);
            default:
                return value;
        }
    }

}

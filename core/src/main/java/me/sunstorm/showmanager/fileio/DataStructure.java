package me.sunstorm.showmanager.fileio;


public enum DataStructure {
    
    CSV(".csv"),
    REAPER_MARKER(".csv"),
    JSON(".json");
    
    private String fileExtension;
    
    DataStructure(String fileExtension) {
        this.fileExtension = fileExtension;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }

}

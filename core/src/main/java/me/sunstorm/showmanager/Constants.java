package me.sunstorm.showmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class Constants {
    public final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    //yeah I don't care about cross compat ATM.
    public File BASE_DIRECTORY = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming", "ShowManager");
}

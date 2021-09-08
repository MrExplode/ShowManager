package me.sunstorm.showmanager.project;

import lombok.Getter;
import me.sunstorm.showmanager.util.JsonLoader;

import java.io.File;

@Getter
public class ProjectManager {
    private Project project;
    private File projectFile;
    private boolean modified = false;

    public void newProject(String name) {
        File file = new File("", name);
        project = JsonLoader.loadOrDefault(file, Project.class);
        modified = false;
        updateGui();
    }

    public void loadProject(File file) {
        project = JsonLoader.loadConfig(file, Project.class);
        projectFile = file;
        modified = false;
        updateGui();
    }

    public void saveProject() {
        JsonLoader.saveConfig(projectFile, project);
        modified = false;
    }

    public void saveProjectAs(File file) {
        JsonLoader.saveConfig(file, project);
        projectFile = file;
        modified = false;
    }

    public void invalidate() {
        modified = true;
    }

    private void updateGui() {

    }
}

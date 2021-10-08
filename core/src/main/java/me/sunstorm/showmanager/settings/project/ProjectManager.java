package me.sunstorm.showmanager.settings.project;

import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.terminable.Terminable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ProjectManager implements Terminable {
    private final File PROJECTS_DIR = new File(Constants.BASE_DIRECTORY, "projects");
    private final File LAST_PROJECT = new File(PROJECTS_DIR, "lastProject");
    private final List<Project> projects = new ArrayList<>();
    protected static Project currentProject;

    public ProjectManager() {
        register();
        log.info("Loading projects...");
        if (!PROJECTS_DIR.exists()) {
            PROJECTS_DIR.mkdirs();
            return;
        }

        String lastProjectName = "";
        try {
            lastProjectName = Files.readString(LAST_PROJECT.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<File> projectFiles = Arrays.stream(PROJECTS_DIR.listFiles()).filter(f -> f.isFile() && f.getName().endsWith(".json")).collect(Collectors.toList());
        String finalLastProjectName = lastProjectName;
        projectFiles.forEach(projectFile -> {
            Project project = new Project(projectFile);
            try {
                project.loadJson();
                projects.add(project);
            } catch (JsonParseException e) {
                log.warn("Invalid project file: {}", projectFile.getName());
            }
            String correctFileName = project.getName().toLowerCase().replace(" ", "_");
            if (!projectFile.getName().replace(".json", "").equals(correctFileName)) {
                log.warn("File name does not match project name for {}, making corrections...", project.getName());
                if (!projectFile.renameTo(new File(PROJECTS_DIR, correctFileName + ".json")))
                    log.warn("Renaming project file {} failed", projectFile.getName());
            }
            //todo load last project descriptor
            if (project.getName().equals(finalLastProjectName)) {
                currentProject = project;
            }
        });
        if (currentProject == null) {
            currentProject = new Project(new File(PROJECTS_DIR, "unknown.json"));
            currentProject.loadJson();
        }
    }

    @Override
    public void shutdown() throws Exception {
        log.info("Saving projects...");
        currentProject.save();
        if (!LAST_PROJECT.exists())
            LAST_PROJECT.createNewFile();
        PrintWriter writer = new PrintWriter(LAST_PROJECT);
        writer.println(currentProject.getName());
        writer.close();
    }
}

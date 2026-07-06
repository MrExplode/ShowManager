import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    java
    alias(libs.plugins.shadow)
}

group = "me.sunstorm"
version = "2.0-SNAPSHOT"

allprojects {
    apply<JavaPlugin>()

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(project(":core"))
}

tasks.shadowJar {
    transform(Log4j2PluginsCacheFileTransformer())
    archiveBaseName.set("ShowManager")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "me.sunstorm.showmanager.Bootstrap"
    }
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.register("build-webapp", Exec::class.java) {
    workingDir = layout.projectDirectory.dir("webapp").asFile
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        commandLine("cmd.exe", "/c", "bun", "run", "build")
    } else {
        commandLine("bun", "run", "build")
    }
    doLast {
        println("Webapp build done!")
    }
}


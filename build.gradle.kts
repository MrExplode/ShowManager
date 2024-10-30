import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    java
    id("com.gradleup.shadow") version "8.3.4"
}

group = "me.sunstorm"
version = "2.0-SNAPSHOT"

allprojects {
    apply<JavaPlugin>()

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(23))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        maven { url = uri("https://jitpack.io") }
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


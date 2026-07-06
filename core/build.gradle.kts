plugins {
    java
    jacoco
}

group = "me.sunstorm"
version = "2.0-SNAPSHOT"

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.javaosc.core) {
        exclude("org.slf4j")
    }
    implementation(libs.disruptor)
    implementation(libs.javalin)
    implementation(libs.javax.inject)
    implementation(libs.jaxb.api)
    implementation(libs.terminalconsoleappender)
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)
    implementation(libs.log4j.slf4j2.impl)
    implementation(libs.asm)
    implementation(libs.slf4j.api)
    implementation(libs.jedis)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    mockitoAgent(libs.mockito.core) {
        isTransitive = false
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.required = false
        csv.required = false
    }

    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude("ch/bildspur/artnet/**")
        }
    }))

    dependsOn(tasks.test)
}

if (System.getenv("CI") != null) {
    tasks.test {
        finalizedBy(tasks.jacocoTestReport)
    }
}
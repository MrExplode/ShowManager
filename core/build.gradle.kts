plugins {
    java
    jacoco
}

group = "me.sunstorm"
version = "2.0-SNAPSHOT"

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(group = "com.google.code.gson",     name = "gson",                    version = "2.13.2")
    implementation(group = "com.google.guava",         name = "guava",                   version = "33.4.8-jre")
    implementation(group = "com.illposed.osc",         name = "javaosc-core",            version = "0.9") {
        exclude("org.slf4j")
    }
    implementation(group = "com.lmax",                 name = "disruptor",               version = "4.0.0")
    implementation(group = "io.javalin",               name = "javalin",                 version = "6.7.0")
    implementation(group = "javax.inject",             name = "javax.inject",            version = "1")
    implementation(group = "javax.xml.bind",           name = "jaxb-api",                version = "2.3.1")
    implementation(group = "net.minecrell",            name = "terminalconsoleappender", version = "1.3.0")
    implementation(group = "org.apache.logging.log4j", name = "log4j-core",              version = "2.25.2")
    implementation(group = "org.apache.logging.log4j", name = "log4j-api",               version = "2.25.2")
    implementation(group = "org.apache.logging.log4j", name = "log4j-slf4j2-impl",       version = "2.25.2")
    implementation(group = "org.ow2.asm",              name = "asm",                     version = "9.8")
    implementation(group = "org.slf4j",                name = "slf4j-api",               version = "2.0.17")
    implementation(group = "redis.clients",            name = "jedis",                   version = "6.2.0")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(group = "org.assertj",       name = "assertj-core",          version = "3.27.6")
    testImplementation(group = "org.mockito",       name = "mockito-core",          version = "5.19.0")
    testImplementation(group = "org.mockito",       name = "mockito-junit-jupiter", version = "5.19.0")
    mockitoAgent(      group = "org.mockito",       name = "mockito-core",          version = "5.19.0") {
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
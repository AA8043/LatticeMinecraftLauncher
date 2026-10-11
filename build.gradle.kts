plugins {
    java
    application
    id("io.freefair.lombok") version "8.6"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.node-gradle.node") version "7.0.2"
}

group = "org.a8043.lmcl"
version = "1.0.0"

javafx {
    version = "21.0.4"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing", "javafx.web")
}

application {
    mainClass.set("org.a8043.lmcl.Main")
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.file("test")
    doFirst {
        if (!workingDir.exists()) workingDir.mkdirs()
    }

    jvmArgs = listOf(
        "--add-opens", "javafx.graphics/javafx.scene=ALL-UNNAMED"
    )
}

repositories {
    mavenCentral()
}



dependencies {
    implementation("io.github.aa8043:cwafx:1.6.0")

    implementation("org.apache.sshd:sshd-core:3.0.0-M5")
    implementation("org.apache.sshd:sshd-sftp:3.0.0-M5")
    implementation("org.apache.sshd:sshd-netty:3.0.0-M5")

    implementation("org.slf4j:slf4j-api:2.0.18")
    implementation("org.apache.logging.log4j:log4j-core:2.26.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.26.0")

    implementation("cn.hutool:hutool-all:5.8.38")
    implementation("org.tomlj:tomlj:1.1.1")
    implementation("org.mozilla:rhino:1.9.1")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    workingDir = rootProject.file("test")
}
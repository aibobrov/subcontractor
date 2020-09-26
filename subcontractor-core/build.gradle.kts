plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    jcenter()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2") // JVM dependency

    implementation("org.jetbrains.exposed", "exposed-core", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.24.1")

    implementation("org.postgresql:postgresql:42.2.2")
    // JUnit
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
}

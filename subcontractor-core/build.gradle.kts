plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.4.0"

}

repositories {
    jcenter()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION)) // or "stdlib-jdk8"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC") // JVM dependency

    implementation("org.jetbrains.exposed", "exposed-core", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.24.1")

    implementation("org.postgresql:postgresql:42.2.2")
    // JUnit
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
}

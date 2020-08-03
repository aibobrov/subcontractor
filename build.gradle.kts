plugins {
    java
    application
    kotlin("jvm") version "1.3.72"
}

group = "org.subcontractor"
version = "0.0.1"

repositories {
    mavenCentral()
}

application {
    mainClassName = "MainKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.slack.api:slack-api-client-kotlin-extension:1.1.1")
    implementation("com.slack.api:bolt:1.1.1")
    implementation("com.slack.api:bolt-jetty:1.1.1")
    implementation("org.slf4j", "slf4j-simple", "1.7.30")
    implementation("com.slack.api:slack-api-client-kotlin-extension:1.1.1")

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

// Fetch environment variables from .env file
fun updateEnvironment(task: JavaExec, environmentFile: String) {
    file(environmentFile)
        .readLines()
        .map { it.split("=") }
        .forEach { list ->
            check(list.size == 2) { "Should be 2 elements for transforming into pair" }
            val (key, value) = list
            task.environment(key, value)
        }
}

tasks.withType<JavaExec>() {
    updateEnvironment(this, ".env")
}

plugins {
    java
    application
    idea
    kotlin("jvm")
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

application {
    mainClassName = "MainKt"
}

dependencies {
    implementation(project(path = ":subcontractor-core", configuration = "default"))
    implementation(kotlin("stdlib-jdk8"))
    runtimeOnly(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.4.0")


    // Slack
    implementation("com.slack.api:slack-api-client-kotlin-extension:1.1.1")
    implementation("com.slack.api:bolt:1.1.1")
    implementation("com.slack.api:slack-api-client-kotlin-extension:1.1.1")
    implementation("com.slack.api:bolt-servlet:1.1.1")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")

    // JUnit
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


tasks.withType<JavaExec> {
    updateEnvironment(this, "$rootDir/.env")
}

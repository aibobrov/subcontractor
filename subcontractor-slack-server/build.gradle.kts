plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    application
}

application {
    mainClassName = "MainKt"
}

dependencies {
    implementation(project(":subcontractor-core"))
    runtimeOnly(kotlin("reflect"))

    // Slack
    implementation("com.slack.api:slack-api-client-kotlin-extension:1.1.1")
    implementation("com.slack.api:bolt:1.1.1")
    implementation("com.slack.api:bolt-servlet:1.1.1")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")

    // JUnit
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val installBootDist by tasks.getting {
    doLast {
        copy {
            from("src/main/resources/application.yml")
            into(buildDir.resolve("install/subcontractor-slack-server-boot/bin/"))
        }
    }
}

val bootJar by tasks.getting(org.springframework.boot.gradle.tasks.bundling.BootJar::class){
    launchScript()
}

val deployLocally by tasks.creating(Copy::class){
    group = "distribution"
    dependsOn(bootJar)
    from(bootJar)
    val deployedConfig = file("/var/www/subcontractor/application.yml")
    if(!deployedConfig.exists()){
        from("src/main/resources/application.yml")
    }
    into("/var/www/subcontractor")
}
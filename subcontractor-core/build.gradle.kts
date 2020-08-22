plugins {
    application
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // JUnit
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
}

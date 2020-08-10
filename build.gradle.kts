plugins {
    val kotlinVersion = "1.3.72"
    base
    kotlin("jvm") version kotlinVersion apply false
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

subprojects {
    version = "0.0.1"
    group = "org.subcontractor"
}

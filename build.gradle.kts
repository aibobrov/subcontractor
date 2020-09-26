plugins {
    kotlin("jvm") version "1.4.10" apply false
    kotlin("plugin.serialization") version "1.4.10" apply false
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

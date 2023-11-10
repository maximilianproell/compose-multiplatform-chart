plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.25.3"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "multiplatformchart"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.animation)

                implementation(libs.kotlinx.atomicfu)
            }
        }
        val commonTest by getting {
            dependencies {
                //implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "at.maximilianproell.multiplatformchart"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01, true)

    signAllPublications()
}

mavenPublishing {
    coordinates("io.github.maximilianproell", "compose-multiplatform-chart", "2.0.0")

    pom {
        name.set("Compose Multiplatform Chart")
        description.set("A Compose Multiplatform library for drawing charts, targeting Android and iOS.")
        inceptionYear.set("2023")
        url.set("https://github.com/maximilianproell/compose-multiplatform-chart")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("maximilianproell")
                name.set("Maximilian Pröll")
                url.set("https://github.com/maximilianproell")
            }
        }
        scm {
            url.set("https://github.com/maximilianproell/compose-multiplatform-chart")
            connection.set("scm:git:git://github.com/maximilianproell/compose-multiplatform-chart.git")
            developerConnection.set("scm:git:ssh://git@github.com:maximilianproell/compose-multiplatform-chart.git")
        }
    }
}
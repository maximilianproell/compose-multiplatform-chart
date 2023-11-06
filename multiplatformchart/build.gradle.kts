plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
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

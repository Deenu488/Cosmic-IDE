plugins {
    id(BuildPlugins.ANDROID_LIBRARY)
    id(BuildPlugins.KOTLIN_ANDROID)
}

android {
    namespace = "io.github.rosemoe.sora"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(Dependencies.MATERIAL)
    // We are using sora-editor from this commit 67b15c1
    implementation("androidx.annotation:annotation:1.4.0")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("org.jruby.jcodings:jcodings:1.0.57")
    implementation("org.jruby.joni:joni:2.1.43")
}
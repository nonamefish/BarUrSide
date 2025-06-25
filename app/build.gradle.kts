import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.ktlint)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

ktlint {
    android.set(true)
    outputColorName.set("RED")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mingyuwu.barurside"
        minSdk = 26
        targetSdk = 34
        versionCode = 25
        versionName = "5.6.10"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            buildConfigField("Boolean", "LOGGER_VISIABLE", "false")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            buildConfigField("Boolean", "LOGGER_VISIABLE", "true")
        }
    }

    kotlinOptions {
        jvmTarget = "16"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    namespace = "com.mingyuwu.barurside"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation(libs.firestore.ktx)
    implementation(libs.storage.ktx)
    implementation(libs.auth.ktx)
    implementation(libs.crashlytics.ktx)
    implementation(libs.analytics.ktx)
    implementation(libs.firebase.ui.storage)
    implementation(libs.lifecycle.extensions)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.map.utils)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(libs.lottie)
    implementation(libs.glide)
    implementation(libs.permissionx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

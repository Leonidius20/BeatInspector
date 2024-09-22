// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.1" apply false

    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ksp) apply false

    id("com.android.library") version "8.5.1" apply false
    id("com.mikepenz.aboutlibraries.plugin") version "10.10.0" apply false
    //id("com.google.dagger.hilt.android") version "2.51.1" apply false
    alias(libs.plugins.hilt) apply false

    alias(libs.plugins.room) apply false
    alias(libs.plugins.compose.compiler) apply false
}

buildscript {
    repositories {
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        classpath("com.github.alexfu:androidautoversion:3.3.0")
    }
}
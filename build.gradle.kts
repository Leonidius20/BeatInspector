// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.10" apply false
    id("com.android.library") version "8.1.1" apply false
    id("com.mikepenz.aboutlibraries.plugin") version "10.10.0" apply false
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
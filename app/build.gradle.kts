import java.util.Properties

plugins {
    // kotlin("kapt")
    id("kotlin-kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.github.alexfu.androidautoversion")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.google.dagger.hilt.android") // todo: maybe should change that dagger.hilt.android.plugin
}

android {
    namespace = "ua.leonidius.beatinspector"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.leonidius20.beatinspector"
        minSdk = 21
        targetSdk = 34
        versionCode = androidAutoVersion.versionCode
        versionName = androidAutoVersion.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        resourceConfigurations.addAll(listOf("en")) // only english resources


        val keystoreFile = project.rootProject.file("secrets.properties")

        if (keystoreFile.exists()) {
            val secretProperties = Properties()
            secretProperties.load(keystoreFile.inputStream())

            val clientId = secretProperties.getProperty("SPOTIFY_CLIENT_ID") ?: throw Exception("SPOTIFY_CLIENT_ID not set in secrets.properties")

            buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"$clientId\"")
        } else {
            // get from env variables
            val clientId = System.getenv("SPOTIFY_CLIENT_ID") ?: throw Exception("SPOTIFY_CLIENT_ID env variable not set")

            buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"$clientId\"")
        }

    }

    signingConfigs {
        create("release") {
            storeFile = project.rootProject.file("android-keystore.jks")

            if (!project.rootProject.file("secrets.properties").exists()) {
                // we are on github actions, get from env variables
                storePassword = System.getenv("SIGNATURE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("SIGNATURE_KEY_ALIAS")
                keyPassword = System.getenv("SIGNATURE_KEY_PASSWORD")
            } else {
                // we are on local machine, get from file
                storeFile = project.rootProject.file("android-keystore.jks")

                val secretsFile = project.rootProject.file("secrets.properties")
                val secretProperties = Properties()
                secretProperties.load(secretsFile.inputStream())

                storePassword = secretProperties.getProperty("SIGNATURE_KEYSTORE_PASSWORD")
                keyAlias = secretProperties.getProperty("SIGNATURE_KEY_ALIAS")
                keyPassword = secretProperties.getProperty("SIGNATURE_KEY_PASSWORD")
            }


        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("com.squareup.retrofit2:retrofit:2.10.0-SNAPSHOT")
    // implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0-SNAPSHOT")
    // implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("io.coil-kt:coil-base:2.5.0")
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("androidx.palette:palette-ktx:1.0.0") // for color extraction from album art
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")

    implementation("com.github.haroldadmin:NetworkResponseAdapter:5.0.0")

    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    // implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.github.ireward:compose-html:1.0.2") // todo: apache 2.0, requires attribution

    implementation("net.openid:appauth:0.11.1")

    implementation("com.mikepenz:aboutlibraries-core:10.10.0")

    implementation("androidx.browser:browser:1.7.0")

    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.paging:paging-runtime-ktx:3.3.0")

    // optional - Jetpack Compose integration
    implementation("androidx.paging:paging-compose:3.3.0")

    // hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.10")

    // room
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")


    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
    }
    namespace = "com.example.myapplication"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        dataBinding=true
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("com.google.mlkit:translate:17.0.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
//    implementation fileTree(dir: 'libs', include: ['*.jar'])
//    implementation project(":libs:filament")
    implementation("com.google.android.filament:filament-android:1.9.10")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.razorpay:checkout:1.6.4")
//    implementation("com.github.delight-im:Android-AdvancedWebView:v3.2.1")x
    implementation("com.google.android.filament:filament-utils-android:1.6.0")
    implementation("com.google.android.filament:gltfio-android:1.6.0")
    implementation("com.razorpay:checkout:1.6.6")
//    implementation("com.github.phucynwa:AndroidProfanityFilter:0.0.3")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.mlkit:language-id:17.0.4")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.android.material:material:1.3.0-alpha03")
    implementation("com.google.android.material:material:1.3.0-alpha02")
    implementation("com.android.volley:volley:1.2.1")
    //logs
    implementation("com.bugfender.sdk:android:3.1.1")
    implementation("androidx.test:monitor:1.6.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
//    implementation("androidx.test:monitor:1.6.1")
//    implementation("org.testng:testng:6.9.6")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//    debugImplementation("com.github.zerobranch.android-remote-debugger:debugger:1.1.")
//    releaseImplementation("com.github.zerobranch.android-remote-debugger:noop:1.1.2")
//    implementation("com.google.api-client:google-api-client:1.30.10")
//    implementation("com.google.oauth-client:google-oauth-client:1.34.1")
//    implementation 'com.google.apis:google-api-services-youtube:v3-rev333-1.25.0'
//    implementation("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0")
    //implementation("com.google.android.ads:mediation-test-suite:3.0.0")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    implementation("com.google.ml-kit:translate:17.0.1")
}
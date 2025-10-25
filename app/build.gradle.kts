plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.huyntd.superapp.gundamshop_mobilefe"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.huyntd.superapp.gundamshop_mobilefe"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    //   Là 1 thư viện SDK để giao tiếp với google play service để lấy id-token phía android user
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Nhớ chuyển sang android mode thêm permission INTERNET
    implementation("com.android.volley:volley:1.2.1")
    // api retrofit + gson
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.core.ktx)
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30") // Kiểm tra phiên bản mới nhất
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // WebSocket (Java-WebSocket là transport layer)
    implementation("org.java-websocket:Java-WebSocket:1.5.3")

    // STOMP client for Android
    // NOTE: Thư viện này dựa trên RxJava2, nên cần thêm RxJava
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")

    // RxJava 2 dependencies (Cần thiết cho StompProtocolAndroid)
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
plugins {
    alias(libs.plugins.android.application)
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
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    //Firebase
//    implementation(libs.firebase.analytics)
//    implementation(libs.firebase.auth)
//    implementation(libs.firebase.database)
    //   Là 1 thư viện SDK để giao tiếp với google play service để lấy id-token phía android user
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Nhớ chuyển sang android mode thêm permission INTERNET
    implementation("com.android.volley:volley:1.2.1")
    // api retrofit + gson
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30") // Kiểm tra phiên bản mới nhất
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
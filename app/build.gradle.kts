plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.testarang"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.testarang"
        minSdk = 24
        targetSdk = 34
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

    // 패키징 옵션 추가
    packagingOptions {
        resources {
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // AndroidX 및 기본 의존성
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Google Vision API 및 gRPC 관련 의존성
    implementation ("io.grpc:grpc-okhttp:1.54.0")
    implementation ("com.google.api:gax-grpc:2.0.0")

//    implementation ("io.grpc:grpc-okhttp:1.42.0")
//    implementation ("com.google.api:gax-grpc:1.65.0")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.4.0") // Google OAuth 의존성
    implementation ("io.grpc:grpc-auth:1.42.0") // gRPC Auth 관련 의존성
    implementation ("com.google.cloud:google-cloud-vision:1.100.0") // Google Vision API 의존성

    // 추가된 필요 의존성
    implementation("io.grpc:grpc-core:1.42.0") // gRPC core
    implementation("io.grpc:grpc-stub:1.42.0") // gRPC stub
    implementation("com.google.protobuf:protobuf-java:3.17.3") // Protocol Buffers
}
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.vavill.exchangerates"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vavill.exchangerates"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    hilt {
        enableExperimentalClasspathAggregation = true
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":ui:xml"))
    implementation(project(":ui:compose"))
    implementation(project(":ui:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //fragment
    implementation(libs.androidx.fragment.ktx)

    // di
    implementation(libs.hilt.android)
//    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    //coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    //image loading
//    implementation(libs.glide)
    implementation(libs.coil)
    implementation(libs.coil.svg)

    // pull to refresh
    implementation(libs.androidx.swiperefreshlayout)

    // datastore
    implementation(libs.androidx.datastore.preferences)

//    implementation(libs.kotlin.stdlib.jdk7)

}
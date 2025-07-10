plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.fma_fe"
    compileSdk = 35
//    buildToolsVersion = "35.0.1"

    defaultConfig {
        applicationId = "com.example.fma_fe"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
// UI & layout
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ViewPager2 & RecyclerView for onboarding
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // ActivityX
    implementation("androidx.activity:activity:1.7.2")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")}



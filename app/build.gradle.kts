plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")



}

android {
    namespace = "com.example.pokeaprendo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pokeaprendo"
        minSdk = 31
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //Splash screen
    implementation("androidx.core:core-splashscreen:1.0.0")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore-ktx")

    //Firebase translation
    implementation("com.google.mlkit:translate:17.0.2")



    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    //Retrofit para consumir API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Consumir de imagenes de tipo link
    implementation ("com.squareup.picasso:picasso:2.8")

    //Para trabajar con corrutinas
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6")


    //Fragments
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    //Layout responsives
    implementation("com.google.android.flexbox:flexbox:3.0.0")
}

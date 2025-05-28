import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "net.micode.schedulemanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.micode.schedulemanagement"
        minSdk = 28
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
    // 修正 APK 文件名配置 (兼容 AGP 7.0+)
    applicationVariants.all {
        val variant = this
        variant.outputs.forEach { output ->
            // 使用新的 API 设置文件名
            val outputImpl = output as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val variantName = variant.name.replaceFirstChar { it.uppercase() }
            val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            outputImpl.outputFileName = "ScheduleManager-${variantName}-${date}.apk"
        }
    }
}

dependencies {

    implementation(libs.material.calendarview)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
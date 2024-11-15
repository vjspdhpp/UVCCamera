/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

plugins {
    alias(libs.plugins.application) apply (true)
    alias(libs.plugins.kotlin.android) apply (true)
    alias(libs.plugins.ksp)

}

android {
    namespace = "com.serenegiant.uvccamera"
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.serenegiant.usbcameratest"
        minSdk = 14
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
//    implementation(fileTree(mapOf("dir" to buildDir, "include" to listOf("*.jar"))))

    implementation(libs.support.v4)
    implementation(libs.support.annotations)

    implementation("com.serenegiant:common:2.12.4") {
        exclude(module = "support-v4")
    }
    implementation(project(":libuvccamera"))
    implementation(project(":usbCameraCommon"))
}

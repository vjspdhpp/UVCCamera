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
pluginManagement {
    repositories {
        google()  // Android 插件位于 Google Maven 仓库
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version("8.6.0")  // 请确认版本号
        id("com.android.library") version("8.6.0")  // 确保 Android Library 插件的版本正确
        id("org.jetbrains.kotlin.android")version ("1.8.0")  // Kotlin 插件版本
    }
}

include(":libuvccamera")
include(":usbCameraCommon")
//include ':usbCameraTest'
//include ':usbCameraTest0'
//include ':usbCameraTest2'
//include ':usbCameraTest3'
//include ':usbCameraTest4'
include(":usbCameraTest5")
//include ':usbCameraTest6'
//include ':usbCameraTest7'
//include ':usbCameraTest8'

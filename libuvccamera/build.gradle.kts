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
import java.util.Properties

plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
}


android {
	namespace = "com.serenegiant.uvccamera"

	compileSdk = 34

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}

    defaultConfig {
        minSdk  = 14
		targetSdk = 34
    }
	kotlinOptions {
		jvmTarget = "1.8"
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
	sourceSets {
		getByName("main") {
			jniLibs.srcDirs("src/main/libs")
		}
	}
}

tasks.withType<JavaCompile> {
	dependsOn(tasks.named("ndkBuild"))
}

fun getNdkBuildPath(): String {
	val properties = Properties()
	properties.load(project.rootProject.file("local.properties").inputStream())

	val ndkBuildingDir = properties.getProperty("ndk.dir")
	var ndkBuildPath = ndkBuildingDir
	if (org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)) {
		ndkBuildPath = "$ndkBuildingDir/ndk-build.cmd"
	} else {
		ndkBuildPath = "$ndkBuildingDir/ndk-build"
	}
	return ndkBuildPath
}


tasks.register<Exec>("ndkBuild") {
	description = "Compile JNI source via NDK"
	group = "build"

	doLast {
		println("executing ndkBuild")
	}

	val ndkBuildPath = getNdkBuildPath()
	commandLine(ndkBuildPath, "-j8", "-C", file("src/main").absolutePath)
}
tasks.register<Exec>("ndkClean") {
	description = "Clean JNI libraries"
	group = "clean"

	doLast {
		println("executing ndkBuild clean")
	}

	val ndkBuildPath = getNdkBuildPath()
	commandLine(ndkBuildPath, "clean", "-C", file("src/main").absolutePath)
}

tasks.named("clean") {
	dependsOn("ndkClean")
}

dependencies {
	implementation(fileTree(mapOf("dir" to buildDir, "include" to listOf("*.jar"))))

	implementation("com.android.support:support-v4:27.1.1")
	implementation("com.android.support:support-annotations:27.1.1")

	implementation("com.serenegiant:common:2.12.4") {
		exclude(module = "support-v4")
	}
}


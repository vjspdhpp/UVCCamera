// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.application) apply (false)
    alias(libs.plugins.library) apply (false)
    alias(libs.plugins.kotlin.android) apply (false)
}



buildscript {
    repositories {
//		google()
        maven("https://maven.google.com")
        maven("https://jitpack.io")
        maven("https://repo1.maven.org/maven2")
        google()
    }
    dependencies {
        classpath(libs.gradle)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
    }
}

allprojects {
    repositories {
        maven("https://maven.google.com")
        maven("http://raw.github.com/saki4510t/libcommon/master/repository/") {
            isAllowInsecureProtocol = true
        }
        google()
        maven("https://jitpack.io")
        maven("https://repo1.maven.org/maven2")
    }
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}

//ext {
//	supportLibVersion = '27.1.1'  // variable that can be referenced to keep support libs consistent
//	commonLibVersion= '2.12.4'
//	versionBuildTool = '27.0.3'
//	versionCompiler = 27
//	versionTarget = 27
//	versionNameString = '1.0.0'
//	javaSourceCompatibility = JavaVersion.VERSION_1_8
//	javaTargetCompatibility = JavaVersion.VERSION_1_8
//}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.dagger.hilt.android") version "2.50" apply false
// compatible with the 1.9.22 version of Kotlin
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
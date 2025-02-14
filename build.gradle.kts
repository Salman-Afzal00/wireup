buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath ("com.android.tools.build:gradle:3.5.1")
    }
}
plugins {
    id("com.android.application") version "8.8.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}
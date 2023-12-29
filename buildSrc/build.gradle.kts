plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("bom"))
    implementation("com.android.tools.build:gradle:8.2.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
//    implementation("org.jetbrains.kotlin:kotlin-serialization")
    implementation("org.kohsuke:github-api:1.316")
}

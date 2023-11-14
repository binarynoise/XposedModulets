plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("bom"))
    implementation("com.android.tools.build:gradle:8.1.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
//    implementation("org.jetbrains.kotlin:kotlin-serialization")
    implementation("org.kohsuke:github-api:1.316")
}

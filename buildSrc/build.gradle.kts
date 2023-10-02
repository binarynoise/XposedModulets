plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("bom"))
    implementation("com.android.tools.build:gradle:8.1.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    implementation("org.kohsuke:github-api:1.316")
}

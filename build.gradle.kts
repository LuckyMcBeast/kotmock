import java.util.*

plugins {
    kotlin("multiplatform") version "1.8.0"
    id("maven-publish")
    id("signing")
}

group = "dev.mcullenm"
version = "0.2-ALPHA"

repositories {
    mavenCentral()
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported inKotlin/Native.")
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }

    publishing {
        loadLocalProperties()
        repositories {
            maven {
                name = "OSS"
                val releaseRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                val snapshotRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotRepoUrl else releaseRepoUrl
                credentials {
                    username = getExtraString("ossrhUsername")
                    password = getExtraString("ossrhPassword")
                }
            }
        }

        // Configure all publications
        publications.withType<MavenPublication> {
            // Stub javadoc.jar artifact
            artifact(javadocJar.get())

            // Provide artifacts information requited by Maven Central
            pom {
                name.set("KotMock")
                description.set(
                    "A simple mocking library for Kotlin Native and JVM with the goal of being Native-first with no dependencies other than Kotlin itself"
                )
                url.set("https://github.com/LuckyMcBeast/kotmock")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("LuckyMcBeast")
                        name.set("M. Cullen McClellan")
                        email.set("cullen.mcclellan@mcullenm.dev")
                    }
                }
                scm {
                    url.set("https://github.com/LuckyMcBeast/kotmock")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}

fun loadLocalProperties() {
    val propsFile = project.rootProject.file("local.properties")
        if (propsFile.exists()) {
            propsFile.reader().use {
                Properties().apply { load(it) }
            }.onEach { (name, value) ->
                ext[name.toString()] = value
            }
        } else {
            ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
            ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
            ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
            ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
            ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
        }
}

fun getExtraString(name: String) = ext[name]?.toString()


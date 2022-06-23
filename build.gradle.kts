import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("idea")
    kotlin("jvm") version "1.3.41"
    id("maven-publish")
}

object Maven {
    private const val DOMAIN = "https://nexus.jxpanda.com"
    const val URL_RELEASE = "$DOMAIN/repository/maven-releases/"
    const val URL_SNAPSHOTS = "$DOMAIN/repository/maven-snapshots/"
    const val USERNAME = "ezor"
    const val PASSWORD = "Ezor2019@)!("
}

object Project {
    const val GROUP = "com.tuanzili"
    // 声明插件的ID，其实就是包名
    const val GROUP_ID = GROUP
    const val ARTIFACT_ID = "tuanzili-commons"
    const val VERSION = "2.9.5"
}

object Version {
    const val MYBATIS_PLUS = "3.2.0"
    const val COMMONS_LANG3 = "3.8.1"
    const val COMMONS_MATH3 = "3.6.1"
    const val JACKSON = "2.11.0"
    const val FAST_JSON = "1.2.60"
    const val COMMONS_CODEC = "1.11"
    const val DOM4J = "2.1.1"
    const val HTTP_CLIENT = "4.5.10"
}

group = Project.GROUP
version = Project.VERSION
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    mavenLocal()
    maven(Maven.URL_RELEASE) {
        credentials {
            username = Maven.USERNAME
            password = Maven.PASSWORD
        }
    }
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.apache.commons:commons-lang3:${Version.COMMONS_LANG3}")
    api("org.apache.commons:commons-math3:${Version.COMMONS_MATH3}")
    api("com.baomidou:mybatis-plus-annotation:${Version.MYBATIS_PLUS}")
    api("com.baomidou:mybatis-plus-extension:${Version.MYBATIS_PLUS}")
    api("com.fasterxml.jackson.core:jackson-annotations:${Version.JACKSON}")
    api("com.fasterxml.jackson.core:jackson-core:${Version.JACKSON}")
    api("com.fasterxml.jackson.core:jackson-databind:${Version.JACKSON}")
    api("com.fasterxml.jackson.module:jackson-module-parameter-names:${Version.JACKSON}")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:${Version.JACKSON}")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Version.JACKSON}")
    api("commons-codec:commons-codec:${Version.COMMONS_CODEC}")
    api("com.alibaba:fastjson:${Version.FAST_JSON}")
    api("org.dom4j:dom4j:${Version.DOM4J}")
    api("org.bouncycastle:bcprov-jdk16:1.46")
    api("org.apache.httpcomponents:httpclient:${Version.HTTP_CLIENT}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = Project.GROUP_ID
            artifactId = Project.ARTIFACT_ID
            version = Project.VERSION

            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri(if (version.toString().endsWith("SNAPSHOT")) Maven.URL_SNAPSHOTS else Maven.URL_RELEASE)
            credentials {
                username = Maven.USERNAME
                password = Maven.PASSWORD
            }
        }
    }
}

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.34"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    // Okhttp 需要
    options("skip-kotlin-relocate")
    // 安装
    install(
        "common",
        "common-5",
        "module-chat",
        "module-configuration",
        "module-lang",
        "module-database",
        "module-metrics",
        "module-kether",
        "module-ui",
        "platform-bukkit"
    )
    classifier = null
    version = "6.0.7-26"
    // plugin.yml
    description {
        contributors {
            name("Cnzw")
        }
        desc("我是插件的说明")
        dependencies {
//            name("PlaceholderAPI")
        }
        links {
            name("homepage").url("https://cnzw.top")
        }
        prefix("IWQ")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // TabooLib
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11800:11800-minimize:api")
    compileOnly("ink.ptms.core:v11800:11800-minimize:mapped")
    // Kotlin
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    // 依赖
    compileOnly("com.alibaba:fastjson:1.2.79")
    compileOnly("com.squareup.okhttp3:okhttp:4.9.3")
    // 插件依赖
    compileOnly("me.clip:placeholderapi:2.10.9") { isTransitive = false }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}
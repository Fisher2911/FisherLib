plugins {
    id("java")
    id("maven-publish")
}

group = "io.github.fisher2911"
version = "1.0.0-beta"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = "${project.name}"
            version = "${project.version}"

            from(components["java"])
        }
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("REPO_USERNAME")
                    password = System.getenv("REPO_PASSWORD")
                }
            }

            name = "HibiscusMCRepository"
            url = uri("https://repo.hibiscusmc.com/releases/")
        }
    }
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
//    withJavadocJar()
}

tasks {

//    build {
//        dependsOn(javadoc)
//    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = Charsets.UTF_8.name()
    }

}
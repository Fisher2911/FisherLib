plugins {
    id("java")
    id("maven-publish")
}

group = "io.github.fisher2911"
version = "1.0.1-beta"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withJavadocJar()
}

tasks {
    build {
        dependsOn(javadoc)
    }

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
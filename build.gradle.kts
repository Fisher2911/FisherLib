plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("maven-publish")
}

group = "io.github.fisher2911"
version = "1.0.10-beta"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}


dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("org.xerial:sqlite-jdbc:3.39.2.0")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("net.kyori:adventure-api:4.11.0")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.2")
//    implementation("com.zaxxer:HikariCP:3.3.0")
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        relocate("org.spongepowered.configurate", "io.github.fisher2911.fisherlib.configurate")
        relocate("net.objecthunter.exp4j", "io.github.fisher2911.fisherlib.exp4j")
        relocate("net.kyori.adventure", "io.github.fisher2911.fisherlib.adventure")
        relocate("com.zaxxer.hikari", "io.github.fisher2911.fisherlib.hikari")
        relocate("org.bstats", "io.github.fisher2911.fisherlib.bstats")
        archiveFileName.set("FisherLib-${version}.jar")

        dependencies {
            exclude(dependency("org.yaml:snakeyaml"))
        }
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = Charsets.UTF_8.name()
    }

}
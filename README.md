# FisherLib

-----------------
[![](https://jitpack.io/v/Fisher2911/FisherLib.svg)](https://jitpack.io/#Fisher2911/FisherLib)

FisherLib is a library for Minecraft Spigot plugins, which has different features that can be used to speed up
development, and make creating plugins easier. It is split up into modules so that you can choose which features you
want to use in your plugins.

### Feature List:

- [x] GUI Library
- [X] Common Library
- [ ] Command Library
- [ ] Custom Item Library
- [ ] Custom Block Library

### Installation

Gradle:

```kotlin
repositories {
    maven("https://repo.hibiscusmc.com/releases")
}

dependencies {
    // common library
    implementation("io.github.Fisher2911.FisherLib:common:v1.0.1-beta")
    // gui library
    implementation("io.github.Fisher2911.FisherLib:gui:v1.0.1-beta")
}
```

Maven:

```xml
<repository>
    <id>hmc-repo-releases</id>
    <name>HibiscusMC Repository</name>
    <url>https://repo.hibiscusmc.com/releases</url>
</repository>

<dependency>
    <groupId>io.github.Fisher2911.FisherLib</groupId>
    <artifactId>gui</artifactId>
    <version>v1.0.1-beta</version>
</dependency>

<dependency>
    <groupId>io.github.Fisher2911.FisherLib</groupId>
    <artifactId>common</artifactId>
    <version>v1.0.1-beta</version>
</dependency>
```

### Usage

Refer to the wiki for usage information.

### Support:

- [Support Discord](https://discord.gg/zJQbHXYGWy)
- [Donate](https://buy.stripe.com/3cs7t5dNe4WQ1Ec4gX)
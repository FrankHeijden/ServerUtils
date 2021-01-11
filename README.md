<!-- Variables (this block will not be visible in the readme -->
[spigot]: https://www.spigotmc.org/resources/79599/
[spigotRatingImg]: https://img.shields.io/badge/dynamic/json.svg?color=brightgreen&label=rating&query=%24.rating.average&suffix=%20%2F%205&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F79599
[spigotDownloadsImg]: https://img.shields.io/badge/dynamic/json.svg?color=brightgreen&label=downloads%20%28spigotmc.org%29&query=%24.downloads&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F79599
[issues]: https://github.com/FrankHeijden/ServerUtils/issues
[wiki]: https://github.com/FrankHeijden/ServerUtils/wiki
[release]: https://github.com/FrankHeijden/ServerUtils/releases/latest
[releaseImg]: https://img.shields.io/github/release/FrankHeijden/ServerUtils.svg?label=github%20release
[license]: https://github.com/FrankHeijden/ServerUtils/blob/master/LICENSE
[licenseImg]: https://img.shields.io/github/license/FrankHeijden/ServerUtils.svg
[bugReports]: https://github.com/FrankHeijden/ServerUtils/issues?q=is%3Aissue+is%3Aopen+label%3Abug
[bugReportsImg]: https://img.shields.io/github/issues/FrankHeijden/ServerUtils/bug.svg?label=bug%20reports
[reportBug]: https://github.com/FrankHeijden/ServerUtils/issues/new?labels=bug&template=bug.md
[featureRequests]: https://github.com/FrankHeijden/ServerUtils/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement
[featureRequestsImg]: https://img.shields.io/github/issues/FrankHeijden/ServerUtils/enhancement.svg?label=feature%20requests&color=informational
[requestFeature]: https://github.com/FrankHeijden/ServerUtils/issues/new?labels=enhancement&template=feature.md
[gradleInstall]: https://gradle.org/install/
[bStatsImg]: https://bstats.org/signatures/bukkit/ServerUtils.svg
[bStats]: https://bstats.org/plugin/bukkit/ServerUtils/7790
<!-- End of variables block -->

# ServerUtils
ServerUtils allows you to manage your plugins in-game.
Featuring reloading, unloading and loading of plugins from your plugins folder at runtime.
ServerUtils also has handy methods to lookup commands and plugins,
and provides you with handy information about them.

For the full description of this plugin, please refer to the ServerUtils [SpigotMC][spigot] page.

[![](https://jitpack.io/v/FrankHeijden/ServerUtils.svg)](https://jitpack.io/#FrankHeijden/ServerUtils)
[![releaseImg]][release]
[![GitHub Actions](https://github.com/FrankHeijden/ServerUtils/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/FrankHeijden/ServerUtils/actions)
[![licenseImg]][license]
[![featureRequestsImg]][featureRequests]
[![bugReportsImg]][bugReports]
[![spigotRatingImg]][spigot]
[![spigotDownloadsImg]][spigot]

[![Discord](https://img.shields.io/discord/580773821745725452.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/WJGvzue)

[![bStatsImg]][bStats]

## How to run the project?
1. Make sure you have [gradle][gradleInstall] installed.
2. Run the project with `gradle clean build` to compile all submodules with dependencies.
3. Afterwards, the platform specific plugins and platform independent compiled jars can be found in the `jars/` directory.

## API Repository / Dependency
Please use the following maven repository:
#### Gradle:
```groovy
maven { url 'https://jitpack.io' }
```
and as dependency:
```groovy
compileOnly 'com.github.FrankHeijden:ServerUtils:TAG'
```
#### Maven:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
and as dependency:
```xml
<dependency>
    <groupId>com.github.FrankHeijden</groupId>
    <artifactId>ServerUtils</artifactId>
    <version>TAG</version>
    <scope>provided</scope>
</dependency>
```

## Commands and Permissions
Please refer to the [SpigotMC][spigot] page for an updated overview of the commands and permissions.

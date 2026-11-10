import org.slf4j.event.Level
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URI

plugins {
    `java-library`
    `maven-publish`
    id("net.neoforged.moddev") version "2.0.141"
    idea
}

val neoMetadataUrl = "https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml"
val metadataProperties = listOf(
    "minecraft_version",
    "minecraft_version_range",
    "neo_version",
    "neo_version_range",
    "mod_id",
    "mod_name",
    "mod_license"
)

val modId = gradleProperty("mod_id")
val modVersion = "${gradleProperty("minecraft_version")}-${gradleProperty("mod_build")}"

version = modVersion
group = gradleProperty("mod_group_id")

base {
    archivesName = modId
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

repositories {
    maven("https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
    maven("https://maven.blamejared.com") {
        content {
            includeGroup("mezz.jei")
        }
    }
}

dependencies {
    compileOnly("curse.maven:jade-324717:8068368")
    compileOnly("mezz.jei:jei-26.1.2-neoforge-api:29.53.0.115")
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
        exclude("**/*.bbmodel")
        exclude("src/generated/**/.cache")
        exclude(".cache/**")
    }
}

neoForge {
    version = gradleProperty("neo_version")

    runs {
        register("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        register("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        register("data") {
            clientData()
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = Level.DEBUG
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

val localRuntime: Configuration = configurations.create("localRuntime")
configurations.runtimeClasspath {
    extendsFrom(localRuntime)
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = metadataProperties.associateWith { gradleProperty(it) } + ("mod_version" to modVersion)
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

tasks.register("runDataClient") {
    group = "neoforge"
    description = "Runs data generation, then launches the client."
    dependsOn("runData", "runClient")
}

tasks.named("runClient") {
    mustRunAfter("runData")
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = layout.projectDirectory.dir("repo").asFile.toURI()
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

tasks.register("listNeoVersions") {
    group = "neoforge"
    description = "Lists available NeoForge versions for the configured (or -Pmc) Minecraft version."
    notCompatibleWithConfigurationCache("Queries the NeoForged maven at execution time")

    doLast {
        val minecraftVersion = targetMinecraftVersion()
        val versions = fetchNeoVersions(minecraftVersion)

        logger.lifecycle("NeoForge versions for Minecraft $minecraftVersion (newest first):")
        versions.forEach { logger.lifecycle("  $it") }
    }
}

tasks.register("selectNeoVersion") {
    group = "neoforge"
    description = "Selects a NeoForge version and writes it to gradle.properties (interactive, or -Pneo=<version>)."
    notCompatibleWithConfigurationCache("Reads stdin and queries the NeoForged maven at execution time")

    doLast {
        val minecraftVersion = targetMinecraftVersion()
        val versions = fetchNeoVersions(minecraftVersion)
        val requestedVersion = findProperty("neo")?.toString()

        val chosenVersion = when {
            requestedVersion.isNullOrBlank() -> promptForNeoVersion(minecraftVersion, versions)
            versions.contains(requestedVersion) -> requestedVersion
            else -> throw GradleException("NeoForge $requestedVersion is not available for Minecraft $minecraftVersion.")
        }

        val propertiesFile = file("gradle.properties")
        writeGradleProperty(propertiesFile, "neo_version", chosenVersion)
        if (minecraftVersion != gradleProperty("minecraft_version")) {
            writeGradleProperty(propertiesFile, "minecraft_version", minecraftVersion)
            writeGradleProperty(propertiesFile, "minecraft_version_range", minecraftDropRange(minecraftVersion))
            writeGradleProperty(propertiesFile, "neo_version_range", "[${minecraftDrop(minecraftVersion)},)")
        }

        logger.lifecycle("Updated gradle.properties -> neo_version=$chosenVersion (Minecraft $minecraftVersion).")
        logger.lifecycle("Refresh/reimport the Gradle project for the change to take effect.")
    }
}

fun gradleProperty(name: String): String = property(name).toString()

fun targetMinecraftVersion(): String = findProperty("mc")?.toString() ?: gradleProperty("minecraft_version")

fun fetchNeoVersions(minecraftVersion: String): List<String> {
    val metadata = URI(neoMetadataUrl).toURL().readText()
    val versions = Regex("<version>([^<]+)</version>").findAll(metadata)
        .map { it.groupValues[1] }
        .filter { it.startsWith("$minecraftVersion.") }
        .toList()
        .reversed()

    if (versions.isEmpty()) throw GradleException("No NeoForge versions found for Minecraft $minecraftVersion. Try -Pmc=<version>.")
    return versions
}

fun promptForNeoVersion(minecraftVersion: String, versions: List<String>): String {
    val newestVersions = versions.take(25)

    logger.lifecycle("Available NeoForge versions for Minecraft $minecraftVersion (newest first):")
    newestVersions.forEachIndexed { index, version -> logger.lifecycle(String.format("  [%2d] %s", index + 1, version)) }
    logger.lifecycle("Enter a number (1-${newestVersions.size}) or a full version, then press Enter:")

    val input = BufferedReader(InputStreamReader(System.`in`)).readLine()?.trim()
    if (input.isNullOrEmpty()) throw GradleException("No selection entered. You can also run with -Pneo=<version>.")

    val selectedIndex = input.toIntOrNull()
    return when {
        selectedIndex != null && selectedIndex in 1..newestVersions.size -> newestVersions[selectedIndex - 1]
        selectedIndex != null -> throw GradleException("Selection $selectedIndex is out of range.")
        versions.contains(input) -> input
        else -> throw GradleException("NeoForge $input is not available for Minecraft $minecraftVersion.")
    }
}

fun minecraftDrop(minecraftVersion: String): String = minecraftVersion.split(".").take(2).joinToString(".")

fun minecraftDropRange(minecraftVersion: String): String {
    val (year, drop) = minecraftDrop(minecraftVersion).split(".")
    return "[$year.$drop,$year.${drop.toInt() + 1})"
}

fun writeGradleProperty(propertiesFile: File, key: String, value: String) {
    val pattern = Regex("(?m)^(${Regex.escape(key)}\\s*=).*$")
    val text = propertiesFile.readText()
    if (!pattern.containsMatchIn(text)) throw GradleException("Property '$key' was not found in gradle.properties")
    propertiesFile.writeText(text.replace(pattern) { it.groupValues[1] + value })
}
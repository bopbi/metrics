package com.bobbyprabowo.metrics.plugin.graph

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class ModuleGraphTask : DefaultTask() {

    @get:Optional
    @get:InputFile
    abstract val ownershipFile: RegularFileProperty

    @get:Input
    @get:Option(option = "dependencies", description = "The project dependencies")
    abstract val dependencies: MapProperty<String, List<String>>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun process() {

        try {
            val graph = buildModuleGraph(
                dependencies = dependencies.get()
            )
            appendMermaidGraphToReadme(graph, outputFile.get().asFile, logger)
        } catch (e: Exception) {
            logger.log(LogLevel.ERROR, e.message, e)
        }
    }
}

fun buildModuleGraph(
    dependencies: MutableMap<String, List<String>>
): String {
    val projectPaths = dependencies
        .entries
        .asSequence()
        .flatMap { (source, target) -> target.plus(source) }
        .sorted()

    val mostMeaningfulGroups: List<String> = projectPaths
        .map { it.split(":").takeLast(2).take(1) }
        .distinct()
        .flatten()
        .toList()

    val projectNames = projectPaths
        .map { listOf(it.split(":").takeLast(2)) }
        .distinct()
        .flatten()
        .toList()

    val subgraphs = mostMeaningfulGroups.joinToString("\n") { group ->
        createSubgraph(group, projectNames)
    }

    var arrows = ""

    dependencies.forEach { (source, targets) ->
        if (source == ":") return@forEach
        targets.forEach { target ->
            val sourceName = source.split(":").last { it.isNotBlank() }
            val targetName = target.split(":").last { it.isNotBlank() }
            if (sourceName != targetName) {
                arrows += "  $sourceName --> $targetName\n"
            }
        }
    }

    val mermaidConfig = """
      %%{
        init: {
          'theme': 'neutral'
        }
      }%%
    """.trimIndent()
    val graphOrientation = "LR"
    return "${mermaidConfig}\n\ngraph $graphOrientation\n$subgraphs\n$arrows"
}

fun createSubgraph(group: String, modules: List<List<String>>): String {
    // Extract module names for the current group
    val moduleNames = modules.asSequence()
        .map { it.takeLast(2) } // group by the most relevant part of the path
        .filter { it.all { it.length > 2 } }
        .filter { it.contains(group) }
        .map { it.last() } // take the actual module name
        .joinToString("\n    ") { moduleName -> moduleName }

    if (moduleNames.isBlank()) {
        return ""
    }
    return "  subgraph $group\n    $moduleNames\n  end"
}

fun appendMermaidGraphToReadme(
    mermaidGraph: String,
    outputFile: File,
    logger: Logger,
) {
    val readmeLines: MutableList<String> = outputFile.readLines().toMutableList()

    readmeLines.clear()
    readmeLines.add("```mermaid\n$mermaidGraph\n```")

    outputFile.writeText(readmeLines.joinToString("\n"))
    logger.debug("Module graph added to ${outputFile.path}")
}

fun Project.parseProjectStructure(): HashMap<String, List<String>> {
    val dependencies = hashMapOf<String, List<String>>()
    project.allprojects.forEach { sourceProject ->
        sourceProject.configurations.forEach { config ->
            config.dependencies.withType(ProjectDependency::class.java)
                .map { it.dependencyProject }
                .forEach { targetProject ->
                    dependencies[sourceProject.path] =
                        dependencies.getOrDefault(sourceProject.path, emptyList())
                            .plus(targetProject.path)
                }
        }
    }
    return dependencies
}

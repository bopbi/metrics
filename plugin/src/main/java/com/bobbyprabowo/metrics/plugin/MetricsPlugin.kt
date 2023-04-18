package com.bobbyprabowo.metrics.plugin

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.bobbyprabowo.metrics.plugin.graph.ModuleGraphTask
import com.bobbyprabowo.metrics.plugin.graph.parseProjectStructure
import org.codehaus.groovy.runtime.StringGroovyMethods
import org.gradle.api.Plugin
import org.gradle.api.Project

class MetricsPlugin : Plugin<Project> {

    private val name: String = "metrics"

    override fun apply(target: Project) {
        val metricExtension = target.extensions.create(name, MetricExtension::class.java)
        target.plugins.withId("com.android.application") {
            val androidComponents =
                target.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            androidComponents.onVariants { variant ->
                val variantName = StringGroovyMethods.capitalize(variant.name)
                target.tasks.register(
                    "metrics${variantName}Bundle",
                    MetricTask::class.java
                ) { task ->
                    task.group = name

                    // Add explicit dependency to support DexGuard
                    task.dependsOn("bundle$variantName")
                }
            }
        }

        target.tasks.register(
            "metricsGraph",
            ModuleGraphTask::class.java
        ) { task ->
            val outputPath = "./graph.md"
            task.dependencies.set(target.parseProjectStructure())
            task.outputFile.set(target.layout.projectDirectory.file(outputPath))
        }
    }
}

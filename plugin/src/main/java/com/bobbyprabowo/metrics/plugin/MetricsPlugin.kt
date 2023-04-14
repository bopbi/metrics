package com.bobbyprabowo.metrics.plugin

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MetricsPlugin : Plugin<Project> {

    private val name: String = "metrics"

    override fun apply(target: Project) {
        val metricExtension = target.extensions.create(name, MetricExtension::class.java)

        target.plugins.withId("com.android.application") {
            val androidComponents =
                target.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        }
    }
}

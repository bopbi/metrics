package com.bobbyprabowo.metrics.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class MetricTask : DefaultTask() {

    @get:Optional
    @get:InputFile
    abstract val ownershipFile: RegularFileProperty

    @TaskAction
    fun process() {
        println("hello world")
    }
}

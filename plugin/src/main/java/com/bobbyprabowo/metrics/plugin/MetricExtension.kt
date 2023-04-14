package com.bobbyprabowo.metrics.plugin

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class MetricExtension(objects: ObjectFactory) {
    val ownershipFile: RegularFileProperty = objects.fileProperty()
    val defaultOwner: Property<String> = objects.property(String::class.java)
    init {
        defaultOwner.convention("unknown")
    }
}

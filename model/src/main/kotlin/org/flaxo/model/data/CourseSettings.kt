package org.flaxo.model.data

import org.flaxo.model.CourseSettingsView
import java.util.Objects
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "course_settings")
@Table(name = "course_settings")
class CourseSettings(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val language: String? = null,

        val testingLanguage: String? = null,

        val testingFramework: String? = null

) : Identifiable, Viewable<CourseSettingsView> {

    override fun view() = CourseSettingsView(
            language = language,
            testingLanguage = testingLanguage,
            testingFramework = testingFramework
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}
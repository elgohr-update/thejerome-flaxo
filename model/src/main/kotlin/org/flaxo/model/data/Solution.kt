package org.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Student task data object.
 */
@Entity(name = "solution")
@Table(name = "solution")
data class Solution(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val task: Task = Task(),

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val student: Student = Student(),

        val sha: String? = null,

        @OneToMany(mappedBy = "solution", orphanRemoval = true)
        val buildReports: List<BuildReport> = mutableListOf(),

        @OneToMany(mappedBy = "solution", orphanRemoval = true)
        val codeStyleReports: List<CodeStyleReport> = mutableListOf(),

        val deadline: Boolean = true

) : Identifiable, Viewable {

    override fun view(): Any = let { solution ->
        object {
            val task = solution.task.branch
            val student = solution.student.nickname
            val buildReport = solution.buildReports.views()
            val codeStyleReport = solution.buildReports.views()
            val deadline = solution.deadline
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}
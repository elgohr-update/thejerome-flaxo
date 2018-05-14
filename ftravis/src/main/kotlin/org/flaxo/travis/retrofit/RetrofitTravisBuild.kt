package org.flaxo.travis.retrofit

import org.flaxo.travis.TravisBuildStatus
import org.flaxo.travis.TravisPullRequestBuild
import java.time.LocalDateTime

class RetrofitTravisBuild(pojo: RetrofitTravisBuildPOJO): TravisPullRequestBuild {

    override val repositoryOwner: String = pojo.repository.owner.name

    override val repositoryName: String = pojo.repository.name

    override val pullRequestNumber: Int = pojo.pullRequestNumber

    override val buildStatus: TravisBuildStatus = TravisBuildStatus.retrieve(pojo.state)

    override val branch: String = pojo.branch.name

    override val commitSha: String = pojo.commit.sha

    override val finishedAt: LocalDateTime? = pojo.finishedAt

}
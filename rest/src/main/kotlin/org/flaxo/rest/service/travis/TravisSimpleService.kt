package org.flaxo.rest.service.travis

import org.apache.logging.log4j.LogManager
import org.flaxo.cmd.CmdExecutor
import org.flaxo.model.DataService
import org.flaxo.model.IntegratedService
import org.flaxo.model.data.Course
import org.flaxo.model.data.User
import org.flaxo.travis.SimpleTravis
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisClient
import org.flaxo.travis.TravisException
import org.flaxo.travis.TravisUser
import org.flaxo.travis.build.TravisBuild
import org.flaxo.travis.parseTravisWebHook
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.io.Reader

/**
 * Travis service basic implementation.
 */
open class TravisSimpleService(private val client: TravisClient,
                               private val dataService: DataService
) : TravisService {

    private val logger = LogManager.getLogger(TravisSimpleService::class.java)

    override fun retrieveTravisToken(githubUsername: String, githubToken: String): String {
        CmdExecutor.execute("travis", "login",
                "-u", githubUsername,
                "-g", githubToken)

        return CmdExecutor.execute("travis", "token")
                .first().split(" ").last()
    }

    override fun travis(travisToken: String): Travis =
            SimpleTravis(client, travisToken)

    override fun parsePayload(reader: Reader): TravisBuild? =
            parseTravisWebHook(reader)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun activateTravis(user: User,
                                course: Course,
                                githubToken: String,
                                githubUserId: String
    ) {
        logger.info("Initialising travis client for ${user.nickname} user")

        val travisToken = retrieveTravisToken(user, githubUserId, githubToken)

        val travis = travis(travisToken)

        logger.info("Retrieving travis user for ${user.nickname} user")

        val travisUser: TravisUser = travis.getUser()
                .getOrElseThrow { errorBody ->
                    TravisException("Travis user retrieving failed for ${user.nickname} " +
                            "due to: ${errorBody.string()}")
                }

        logger.info("Trigger travis user with id ${travisUser.id} sync for ${user.nickname} user")

        travis.sync(travisUser.id)
                ?.also { errorBody ->
                    throw TravisException("Travis user ${travisUser.id} sync hasn't started due to: ${errorBody.string()}")
                }

        logger.info("Trying to ensure that current user's travis synchronisation has finished")

        waitUntilSyncIsOver(travis, travisUser.id)

        logger.info("Activating git repository of the course ${user.nickname}/${course.name} for travis CI")

        travis.activate(githubUserId, course.name)
                .getOrElseThrow { errorBody ->
                    TravisException("Travis activation of $githubUserId/${course.name} " +
                            "repository went bad due to: ${errorBody.string()}")
                }
    }

    private fun waitUntilSyncIsOver(travis: Travis,
                                    travisUserId: String,
                                    attemptsLimit: Int = 20,
                                    retrievingDelay: Long = 3000
    ) {
        val observationDuration: (Int) -> Long = { attempt -> (attempt + 1) * retrievingDelay / 1000 }

        repeat(attemptsLimit) { attempt ->
            Thread.sleep(retrievingDelay)

            val travisUser = travis.getUser()
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user $travisUserId retrieving went bad due to: ${errorBody.string()}")
                    }

            if (travisUser.isSyncing)
                logger.info("Travis user $travisUserId synchronisation hasn't finished " +
                        "after ${observationDuration(attempt)} seconds.")
            else return
        }

        throw TravisException("Travis synchronisation hasn't finished " +
                "after ${observationDuration(attemptsLimit)} seconds.")
    }

    private fun retrieveTravisToken(user: User,
                                    githubUserId: String,
                                    githubToken: String
    ): String = retrieveUserWithTravisToken(user, githubUserId, githubToken)
            .credentials
            .travisToken
            ?: throw TravisException("Travis token wasn't found for ${user.nickname}.")

    private fun retrieveUserWithTravisToken(user: User,
                                            githubUserId: String,
                                            githubToken: String
    ): User = user
            .takeUnless { it.credentials.travisToken.isNullOrBlank() }
            ?: dataService.addToken(
                    user.nickname,
                    IntegratedService.TRAVIS,
                    retrieveTravisToken(githubUserId, githubToken)
            )

}

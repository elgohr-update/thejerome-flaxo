package org.flaxo.rest.manager.github

import org.flaxo.git.GitPayload
import org.flaxo.github.Github
import org.flaxo.github.GithubException
import org.flaxo.github.graphql.GithubQL
import org.flaxo.github.parseGithubEvent
import java.io.Reader
import org.kohsuke.github.GitHub as KohsukeGithub

/**
 * Github manager implementation.
 */
class SimpleGithubManager(private val webHookUrl: String) : GithubManager {

    override fun with(credentials: String) =
            Github(
                    githubClientProducer = { KohsukeGithub.connectUsingOAuth(credentials) },
                    rawWebHookUrl = webHookUrl,
                    githubQL = GithubQL.from(credentials)
            )

    override fun parsePayload(reader: Reader,
                              headers: Map<String, List<String>>
    ): GitPayload? {
        val types = headers["x-github-event"].orEmpty()
                .also {
                    if (it.isEmpty()) throw GithubException("Github payload type wasn't found in headers.")
                }
        return parseGithubEvent(reader, types.first(), KohsukeGithub.connectAnonymously())
    }
}
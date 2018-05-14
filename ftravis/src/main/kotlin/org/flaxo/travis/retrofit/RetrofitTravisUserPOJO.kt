package org.flaxo.travis.retrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Travis user class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class RetrofitTravisUserPOJO {

    var id: String = ""

    var login: String = ""

    var name: String = ""

    @JsonProperty("is_syncing")
    var isSyncing: Boolean = false

    @JsonProperty("github_id")
    var githubId: Int = 0
}
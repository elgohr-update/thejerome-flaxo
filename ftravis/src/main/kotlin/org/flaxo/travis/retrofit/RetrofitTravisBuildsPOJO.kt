package org.flaxo.travis.retrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class RetrofitTravisBuildsPOJO {

    var builds: List<RetrofitTravisBuildPOJO> = emptyList()

    @JsonProperty("@pagination")
    lateinit var pagination: TravisPaginationPOJO

}
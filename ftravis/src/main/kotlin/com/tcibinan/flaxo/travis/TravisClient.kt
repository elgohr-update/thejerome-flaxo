package com.tcibinan.flaxo.travis

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Travis retrofit client.
 */
interface TravisClient {

    /**
     * Retrieves associated travis user.
     *
     * Also requires travis authorization token.
     */
    @Headers("Travis-API-Version: 3")
    @GET("user")
    fun getUser(@Header("Authorization") authorization: String
    ): Call<TravisUser>

    /**
     * Activates the [repositorySlug].
     *
     * Also requires travis authorization token.
     */
    @Headers("Travis-API-Version: 3")
    @POST("/repo/{repository_slug}/activate")
    fun activate(@Header("Authorization") authorization: String,
                 @Path("repository_slug") repositorySlug: String
    ): Call<TravisRepository>

    /**
     * Deactivates the [repositorySlug].
     * s
     * Also requires travis authorization token.
     */
    @Headers("Travis-API-Version: 3")
    @POST("/repo/{repository_slug}/deactivate")
    fun deactivate(@Header("Authorization") authorization: String,
                   @Path("repository_slug") repositorySlug: String
    ): Call<TravisRepository>

}


package com.app.zee5test.repository

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {

    @GET("w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pilimit=50&generator=prefixsearch")
    suspend fun fetchVariationList(
        @Query("gpssearch") searchQuery: String,
        @Query("pithumbsize") imageSize: Int,
        @Query("gpsoffset") paginateItemCount: Int
    ): Response<ResponseBody>
}
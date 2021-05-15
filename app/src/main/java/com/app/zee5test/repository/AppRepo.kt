package com.app.zee5test.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.app.zee5test.model.SearchItem

interface AppRepo {
    suspend fun getVariants(
        searchQuery: String,
        imageSize: Int,
        paginateItemCount: Int
    ): LiveData<PagingData<SearchItem>>
}
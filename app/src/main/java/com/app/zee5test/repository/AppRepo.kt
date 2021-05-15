package com.app.zee5test.repository

import androidx.paging.PagingData
import com.app.zee5test.model.SearchItem
import kotlinx.coroutines.flow.Flow

interface AppRepo {
    suspend fun getVariants(
        searchQuery: String,
        imageSize: Int,
        paginateItemCount: Int
    ): Flow<PagingData<SearchItem>>
}
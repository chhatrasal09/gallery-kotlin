package com.app.zee5test.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.zee5test.database.AppDao
import com.app.zee5test.database.WikiRemoteMediator
import com.app.zee5test.model.SearchItem
import kotlinx.coroutines.flow.Flow

class AppRepoImpl(private val apiClient: ApiClient, private val appDao: AppDao) : AppRepo {

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getVariants(
        searchQuery: String,
        imageSize: Int,
        paginateItemCount: Int
    ): Flow<PagingData<SearchItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10, enablePlaceholders = false,
            ),
            remoteMediator = WikiRemoteMediator(searchQuery, imageSize, apiClient, appDao),
            pagingSourceFactory = { appDao.getQueriedItems("%$searchQuery%") }
        ).flow
    }
}
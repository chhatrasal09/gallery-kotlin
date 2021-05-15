package com.app.zee5test.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.app.zee5test.database.AppDao
import com.app.zee5test.database.WikiRemoteMediator
import com.app.zee5test.model.SearchItem

class AppRepoImpl(private val apiClient: ApiClient, private val appDao: AppDao) : AppRepo {

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getVariants(
        searchQuery: String,
        imageSize: Int,
        paginateItemCount: Int
    ): LiveData<PagingData<SearchItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10, enablePlaceholders = false,
            ),
            remoteMediator = WikiRemoteMediator(searchQuery, imageSize, apiClient, appDao),
            pagingSourceFactory = { appDao.getQueriedItems("%$searchQuery%") }
        ).liveData
    }
}
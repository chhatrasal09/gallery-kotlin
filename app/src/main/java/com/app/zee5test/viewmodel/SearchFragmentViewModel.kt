package com.app.zee5test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.zee5test.model.SearchItem
import com.app.zee5test.repository.AppRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

class SearchFragmentViewModel(private val repo: AppRepo) : ViewModel() {

    private val mImageSize = 800

    suspend fun searchQuery(query: String): Flow<PagingData<SearchItem>> =
        withContext(viewModelScope.coroutineContext) {
            return@withContext repo.getVariants(query, mImageSize, 0).distinctUntilChanged()
                .cachedIn(viewModelScope)
        }
}
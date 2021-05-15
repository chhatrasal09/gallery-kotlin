package com.app.zee5test.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.zee5test.model.SearchItem

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(list: List<SearchItem>): List<Long>

    @Query("SELECT * FROM searchitem WHERE query_string LIKE :query")
    fun getQueriedItems(query: String): PagingSource<Int, SearchItem>
}
package com.app.zee5test.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchItem(
    @PrimaryKey @ColumnInfo(name = "page_id") val pageId: String,
    @ColumnInfo(name = "query_string") val queryString: String,
    val url: String
)
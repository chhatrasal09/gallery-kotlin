package com.app.zee5test.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.app.zee5test.model.SearchItem
import com.app.zee5test.repository.ApiClient
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class WikiRemoteMediator(
    private val query: String,
    private val imageSize: Int,
    private val apiClient: ApiClient,
    private val dao: AppDao
) : RemoteMediator<Int, SearchItem>() {

    private var paginateItemCount = 0
    private var lastUpdated = 0L

    /**
     * Callback triggered when Paging needs to request more data from a remote source due to any of
     * the following events:
     *  * Stream initialization if [initialize] returns [LAUNCH_INITIAL_REFRESH]
     *  * [REFRESH] signal driven from UI
     *  * [PagingSource] returns a [LoadResult] which signals a boundary condition, i.e., the most
     *  recent [LoadResult.Page] in the [PREPEND] or [APPEND] direction has
     *  [LoadResult.Page.prevKey] or [LoadResult.Page.nextKey] set to `null` respectively.
     *
     * It is the responsibility of this method to update the backing dataset and trigger
     * [PagingSource.invalidate] to allow [androidx.paging.PagingDataAdapter] to pick up new
     * items found by [load].
     *
     * The runtime and result of this method defines the remote [LoadState] behavior sent to the
     * UI via [CombinedLoadStates].
     *
     * This method is never called concurrently *unless* [Pager.flow] has multiple collectors.
     * Note that Paging might cancel calls to this function if it is currently executing a
     * [PREPEND] or [APPEND] and a [REFRESH] is requested. In that case, [REFRESH] has higher
     * priority and will be executed after the previous call is cancelled. If the [load] call with
     * [REFRESH] returns an error, Paging will call [load] with the previously cancelled [APPEND]
     * or [PREPEND] request. If [REFRESH] succeeds, it won't make the [APPEND] or [PREPEND] requests
     * unless they are necessary again after the [REFRESH] is applied to the UI.
     *
     * @param loadType [LoadType] of the condition which triggered this callback.
     *  * [PREPEND] indicates the end of pagination in the [PREPEND] direction was reached. This
     *  occurs when [PagingSource.load] returns a [LoadResult.Page] with
     *  [LoadResult.Page.prevKey] == `null`.
     *  * [APPEND] indicates the end of pagination in the [APPEND] direction was reached. This
     *  occurs when [PagingSource.load] returns a [LoadResult.Page] with
     *  [LoadResult.Page.nextKey] == `null`.
     *  * [REFRESH] indicates this method was triggered due to a requested refresh. Generally, this
     *  means that a request to load remote data and **replace** all local data was made. This can
     *  happen when:
     *    * Stream initialization if [initialize] returns [LAUNCH_INITIAL_REFRESH]
     *    * An explicit call to refresh driven by the UI
     * @param state A copy of the state including the list of pages currently held in memory of the
     * currently presented [PagingData] at the time of starting the load. E.g. for
     * load(loadType = APPEND), you can use the page or item at the end as input for what to load
     * from the network.
     *
     * @return [MediatorResult] signifying what [LoadState] to be passed to the UI, and whether
     * there's more data available.
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchItem>
    ): MediatorResult {
        return try {
            when (loadType) {
                LoadType.REFRESH -> {
                    paginateItemCount = 0
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> Unit
            }
            val set = mutableSetOf<SearchItem>()
            val result = apiClient.fetchVariationList(query, imageSize, paginateItemCount)
            val data = JSONObject(result.body()?.string() ?: "{}")
            if (data.has("query") && data.getJSONObject("query").has("pages")) {
                val pageData = data.getJSONObject("query").getJSONObject("pages")
                for (key in pageData.keys()) {
                    if (pageData.getJSONObject(key).has("thumbnail") &&
                        pageData.getJSONObject(key).getJSONObject("thumbnail").has("source")
                    )
                        set.add(
                            SearchItem(
                                key,
                                query,
                                pageData.getJSONObject(key).getJSONObject("thumbnail")
                                    .getString("source")
                            )
                        )
                }
                paginateItemCount += 10
            }
            if (set.isNotEmpty()) {
                val list = dao.insertItems(set.toList())
                lastUpdated = System.currentTimeMillis()
            }
            MediatorResult.Success(endOfPaginationReached = !data.has("continue"))
        } catch (e: Exception) {
            e.printStackTrace()
            MediatorResult.Error(e)
        }
    }

    /**
     * Callback fired during initialization of a [PagingData] stream, before initial load.
     *
     * This function runs to completion before any loading is performed.
     *
     * @return [InitializeAction] used to control whether [load] with load type [REFRESH] will be
     * immediately dispatched when the first [PagingData] is submitted:
     *  * [LAUNCH_INITIAL_REFRESH] to immediately dispatch [load] asynchronously with load type
     *  [REFRESH], to update paginated content when the stream is initialized.
     *  Note: This also prevents [RemoteMediator] from triggering [PREPEND] or [APPEND] until
     *  [REFRESH] succeeds.
     *  * [SKIP_INITIAL_REFRESH] to wait for a refresh request from the UI before dispatching [load]
     *  asynchronously with load type [REFRESH].
     */
    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
        return if (System.currentTimeMillis() - lastUpdated >= cacheTimeout) {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    }
}
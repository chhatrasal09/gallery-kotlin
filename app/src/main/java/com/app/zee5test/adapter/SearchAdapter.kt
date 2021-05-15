package com.app.zee5test.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.zee5test.databinding.SearchImageItemBinding
import com.app.zee5test.model.SearchItem
import com.app.zee5test.utils.dp
import com.app.zee5test.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey

/**
 * This class is responsible for populating data to recyclerview. The [PagingDataAdapter] is
 * used so that the adapter know when to paginated the data based on the logic provide in
 * [com.app.zee5test.database.WikiRemoteMediator].
 *
 */
class SearchAdapter :
    PagingDataAdapter<SearchItem, SearchAdapter.ContentViewHolder>(SEARCH_ITEM_COMPARATOR) {

    var onItemSelected: ((Int, String, ImageView) -> Unit)? = null

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder =
        ContentViewHolder(
            SearchImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.onBind(position)
    }

    /**
     * This function is overrided to clean the data from image view to handle OOM issue while recycling view.
     */
    override fun onViewRecycled(holder: ContentViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.viewDataBinding.itemImageView)
    }

    /**
     * This function is used to clear the data before loading data for new query.
     */
    suspend fun resetList() {
        submitData(PagingData.empty())
    }

    inner class ContentViewHolder(val viewDataBinding: SearchImageItemBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun onBind(position: Int) {
            val data = getItem(position) ?: return
            viewDataBinding.itemImageView.transitionName = data.url
            Glide.with(itemView.context)
                .load(data.url)
                .format(DecodeFormat.PREFER_RGB_565)
                .override(200.dp, 200.dp)
                .centerCrop()
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(data.pageId))
                .into(viewDataBinding.itemImageView)
            itemView.setOnSingleClickListener {
                onItemSelected?.invoke(position, data.url, viewDataBinding.itemImageView)
            }
        }
    }

    companion object {
        val SEARCH_ITEM_COMPARATOR = object :
            DiffUtil.ItemCallback<SearchItem>() {
            /**
             * Called to check whether two objects represent the same item.
             *
             *
             * For example, if your items have unique ids, this method should check their id equality.
             *
             *
             * Note: `null` items in the list are assumed to be the same as another `null`
             * item and are assumed to not be the same as a non-`null` item. This callback will
             * not be invoked for either of those cases.
             *
             * @param oldItem The item in the old list.
             * @param newItem The item in the new list.
             * @return True if the two items represent the same object or false if they are different.
             * @see Callback.areItemsTheSame
             */
            override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean =
                oldItem === newItem

            /**
             * Called to check whether two items have the same data.
             *
             *
             * This information is used to detect if the contents of an item have changed.
             *
             *
             * This method to check equality instead of [Object.equals] so that you can
             * change its behavior depending on your UI.
             *
             *
             * For example, if you are using DiffUtil with a
             * [RecyclerView.Adapter], you should
             * return whether the items' visual representations are the same.
             *
             *
             * This method is called only if [.areItemsTheSame] returns `true` for
             * these items.
             *
             *
             * Note: Two `null` items are assumed to represent the same contents. This callback
             * will not be invoked for this case.
             *
             * @param oldItem The item in the old list.
             * @param newItem The item in the new list.
             * @return True if the contents of the items are the same or false if they are different.
             * @see Callback.areContentsTheSame
             */
            override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean =
                oldItem == newItem
        }
    }
}
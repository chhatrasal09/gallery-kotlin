package com.app.zee5test.view

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.app.zee5test.R
import com.app.zee5test.adapter.SearchAdapter
import com.app.zee5test.databinding.FragmentSearchBinding
import com.app.zee5test.di.searchFragmentModule
import com.app.zee5test.utils.dp
import com.app.zee5test.viewmodel.SearchFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules


/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    private var mBinding: FragmentSearchBinding? = null
    private var mSearchJob: Job? = null
    private val mViewModel: SearchFragmentViewModel by viewModel()
    private val mSearchAdapter by lazy {
        SearchAdapter().also {
            it.onItemSelected = { _, url, imageView ->
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToFullScreenImageFragment(
                        url
                    ), FragmentNavigatorExtras(imageView to url)
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadKoinModules(searchFragmentModule)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSharedElementTransitionOnEnter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    private fun setSharedElementTransitionOnEnter() {
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.apply {
            searchResultRecyclerView.adapter = mSearchAdapter
            searchResultRecyclerView.addItemDecoration(GridItemDecoration(4.dp))

            searchBtn.setOnClickListener {
                val query = searchViewInput.query?.toString()?.trim() ?: return@setOnClickListener
                if (query.isEmpty()) {
                    Toast.makeText(it.context, "Search query cannot be empty!", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }
                search(query)
            }

            searchViewInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                /**
                 * Called when the user submits the query. This could be due to a key press on the
                 * keyboard or due to pressing a submit button.
                 * The listener can override the standard behavior by returning true
                 * to indicate that it has handled the submit request. Otherwise return false to
                 * let the SearchView handle the submission by launching any associated intent.
                 *
                 * @param query the query text that is to be submitted
                 *
                 * @return true if the query has been handled by the listener, false to let the
                 * SearchView perform the default action.
                 */
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val text = query?.trim() ?: return false
                    search(text)
                    return true
                }

                /**
                 * Called when the query text is changed by the user.
                 *
                 * @param newText the new content of the query text field.
                 *
                 * @return false if the SearchView should perform the default action of showing any
                 * suggestions if available, true if the action was handled by the listener.
                 */
                override fun onQueryTextChange(newText: String?): Boolean = false
            })

            postponeEnterTransition()
            searchResultRecyclerView.viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    private fun search(query: String) {
        mSearchJob?.cancel()
        mSearchJob = lifecycleScope.launch(Dispatchers.IO) {
            mSearchAdapter.resetList()
            mSearchAdapter.submitData(PagingData.empty())
            mViewModel.searchQuery(query).collectLatest {
                lifecycleScope.launch { mSearchAdapter.submitData(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    override fun onDetach() {
        super.onDetach()
        unloadKoinModules(searchFragmentModule)
    }

    companion object {

        fun newInstance() = SearchFragment()
    }
}

class GridItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    /**
     * Retrieve any offsets for the given item. Each field of `outRect` specifies
     * the number of pixels that the item view should be inset by, similar to padding or margin.
     * The default implementation sets the bounds of outRect to 0 and returns.
     *
     *
     *
     * If this ItemDecoration does not affect the positioning of item views, it should set
     * all four fields of `outRect` (left, top, right, bottom) to zero
     * before returning.
     *
     *
     *
     * If you need to access Adapter for additional data, you can call
     * [RecyclerView.getChildAdapterPosition] to get the adapter position of the
     * View.
     *
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        outRect.left = space
        outRect.right = space
        outRect.top = space
        outRect.bottom = space

    }
}
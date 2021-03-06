package jp.co.tukiyo.yoruha.ui.fragment

import android.os.Bundle
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import jp.co.tukiyo.yoruha.R
import jp.co.tukiyo.yoruha.data.api.googlebooks.GoogleBooksAPIClient
import jp.co.tukiyo.yoruha.data.api.googlebooks.model.VolumeItem
import jp.co.tukiyo.yoruha.databinding.FragmentSearchResultListBinding
import jp.co.tukiyo.yoruha.ui.adapter.BookSearchResultListAdapter
import jp.co.tukiyo.yoruha.ui.listener.OnListsBookItemListener
import jp.co.tukiyo.yoruha.viewmodel.SearchResultListFragmentViewModel

@FragmentWithArgs
class SearchResultListFragment : BaseFragment<FragmentSearchResultListBinding>(),
        SearchView.OnQueryTextListener,
        Toolbar.OnMenuItemClickListener,
        OnListsBookItemListener {

    @Arg(key = "queryString")
    var query: String = ""

    override val layoutResourceId: Int = R.layout.fragment_search_result_list

    val searchViewModel: SearchResultListFragmentViewModel by lazy {
        SearchResultListFragmentViewModel(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel(searchViewModel)
        setHasOptionsMenu(true)
        searchViewModel.adapter = BookSearchResultListAdapter(context, this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding?.apply {
            toolbar.inflateMenu(R.menu.menu_search)
        }?.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            searchResultList.run {
                layoutManager = LinearLayoutManager(activity)
                adapter = searchViewModel.adapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        recyclerView?.run {
                            (layoutManager as LinearLayoutManager).let {
                                searchViewModel.additionalLoad(query, it.findLastVisibleItemPosition())
                            }
                        }
                    }
                })
            }
            searchResultRefresh.run {
                setOnRefreshListener {
                    searchViewModel.refresh(query)
                    if (isRefreshing) {
                        isRefreshing = false
                    }
                }
            }

            toolbar.run {
                setOnMenuItemClickListener(this@SearchResultListFragment)
                (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
                    isIconified = false
                    setIconifiedByDefault(false)
                    requestFocusFromTouch()
                    setQuery(this@SearchResultListFragment.query, false)
                    setOnQueryTextListener(this@SearchResultListFragment)
                }
                menu.findItem(R.id.menu_search_sort_relevance).isChecked = true
            }

            searchViewModel.orderBy = GoogleBooksAPIClient.OrderBy.RELEVANCE

            viewModel = searchViewModel
        }

        searchViewModel.search(query)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        this.query = query ?: ""
        searchViewModel.search(this.query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null && item.groupId == R.id.menu_search_sort) {
            item.isChecked = true
            when (item.itemId) {
                R.id.menu_search_sort_relevance -> {
                    searchViewModel.orderBy = GoogleBooksAPIClient.OrderBy.RELEVANCE
                }
                R.id.menu_search_sort_newest -> {
                    searchViewModel.orderBy = GoogleBooksAPIClient.OrderBy.NEWEST
                }
            }
        }
        return true
    }

    override fun onBookClick(item: VolumeItem) {
        searchViewModel.onItemClick(item, this)
    }
}

package jp.co.tukiyo.yoruha.viewmodel

import android.content.Context
import android.widget.Toast
import jp.co.tukiyo.yoruha.data.api.googlebooks.GoogleBooksAPIClient
import jp.co.tukiyo.yoruha.data.api.googlebooks.model.VolumeItem
import jp.co.tukiyo.yoruha.extensions.onSuccess
import jp.co.tukiyo.yoruha.ui.adapter.BookSearchResultListAdapter
import jp.co.tukiyo.yoruha.ui.fragment.BaseFragment
import jp.co.tukiyo.yoruha.ui.screen.BookInfoScreen
import jp.co.tukiyo.yoruha.usecase.BookShelfManageUseCase

class SearchResultListFragmentViewModel(context: Context) : FragmentViewModel(context) {
    lateinit var adapter: BookSearchResultListAdapter
    var orderBy: GoogleBooksAPIClient.OrderBy = GoogleBooksAPIClient.OrderBy.RELEVANCE
    val useCase = BookShelfManageUseCase(context)
    var additionalLoading: Boolean = false

    fun search(query: String) {
        useCase.search(query, orderBy, 0)
                .compose(bindToLifecycle())
                .onSuccess {
                    adapter.run {
                        clear()
                        addAll(it)
                        notifyDataSetChanged()
                    }
                }
                .onError {
                    Toast.makeText(context, "search failed", Toast.LENGTH_SHORT).show()
                }
                .subscribe()
    }

    private fun additionalSearch(query: String, startIndex: Int) {
        if (adapter.itemCount != startIndex) return

        useCase.search(query, orderBy, startIndex)
                .compose(bindToLifecycle())
                .doFinally {
                    additionalLoading = false
                }
                .onSuccess {
                    adapter.run {
                        addAll(it)
                        notifyDataSetChanged()
                    }
                    Toast.makeText(context, "↓next ${it.size} books", Toast.LENGTH_SHORT).show()
                }
                .onError {
                    Toast.makeText(context, "search failed", Toast.LENGTH_SHORT).show()
                }
                .subscribe()
    }

    fun additionalLoad(query: String, lastVisibleItemPosition: Int) {
        if (additionalLoading || lastVisibleItemPosition != adapter.itemCount - 1) return

        additionalLoading = true
        additionalSearch(query, adapter.itemCount)
    }

    fun refresh(query: String) {
        search(query)
    }

    fun onItemClick(item: VolumeItem, fragment: BaseFragment<*>) {
        fragment.screenActivity.pushScreen(BookInfoScreen(item.id))
    }
}
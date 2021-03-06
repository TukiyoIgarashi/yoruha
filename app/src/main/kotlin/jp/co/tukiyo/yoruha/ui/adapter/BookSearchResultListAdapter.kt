package jp.co.tukiyo.yoruha.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.chuross.recyclerviewadapters.ItemAdapter
import jp.co.tukiyo.yoruha.R
import jp.co.tukiyo.yoruha.data.api.googlebooks.model.VolumeItem
import jp.co.tukiyo.yoruha.ui.listener.OnListsBookItemListener

class BookSearchResultListAdapter(
        context: Context,
        val listener: OnListsBookItemListener
) : ItemAdapter<VolumeItem, BookSearchResultItemViewHolder>(context) {
    override fun getAdapterId(): Int = R.layout.search_result_item

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookSearchResultItemViewHolder {
        return BookSearchResultItemViewHolder(LayoutInflater.from(context).inflate(adapterId, parent, false))
    }

    override fun onBindViewHolder(holder: BookSearchResultItemViewHolder?, position: Int) {
        holder?.bind(get(position), listener)
    }
}
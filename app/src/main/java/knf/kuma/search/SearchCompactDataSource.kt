package knf.kuma.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.kuma.commons.jsoupCookiesAdapter
import knf.kuma.directory.DirObjectCompact
import knf.kuma.directory.DirectoryPageCompact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class SearchCompactDataSource(
    val query: String,
    val onInit: (isEmpty: Boolean) -> Unit
) : PagingSource<Int, DirObjectCompact>() {

    override fun getRefreshKey(state: PagingState<Int, DirObjectCompact>): Int? =
        state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DirObjectCompact> {
        val page = params.key ?: 1
        try {
            val dir = withContext(Dispatchers.IO) {
                jsoupCookiesAdapter("https://www3.animeflv.net/browse?order=title&q=${URLEncoder.encode(query, "utf-8")}&page=$page", DirectoryPageCompact::class.java)
            }
            if (page == 1)
                onInit(dir.list.isEmpty())
            return LoadResult.Page(dir.list, null, if (dir.hasNext) page + 1 else null)
        }catch (e:Exception){
            e.printStackTrace()
            if (page == 1)
                onInit(true)
            return LoadResult.Page(emptyList(), null, null)
        }
    }
}
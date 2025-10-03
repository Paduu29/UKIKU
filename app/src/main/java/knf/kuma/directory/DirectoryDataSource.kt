package knf.kuma.directory

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.kuma.commons.jsoupCookiesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DirectoryDataSource(val type: String, val retryCallback: () -> Unit) : PagingSource<Int, DirObjectCompact>() {

    override fun getRefreshKey(state: PagingState<Int, DirObjectCompact>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DirObjectCompact> {
        val page = params.key?: 1
        try {
            val dir = withContext(Dispatchers.IO) {
                jsoupCookiesAdapter("https://www3.animeflv.net/browse?order=title&type[]=$type&page=$page", DirectoryPageCompact::class.java)
            }
            return LoadResult.Page(dir.list, null, if (dir.hasNext) page + 1 else null)
        }catch (e:Exception){
            e.printStackTrace()
            retryCallback()
            return LoadResult.Error(e)
        }
    }
}

fun createDirectoryPagedList(type: String, retryCallback: () -> Unit) =
        Pager(
            config = PagingConfig(24),
            pagingSourceFactory = { DirectoryDataSource(type, retryCallback) }
        ).flow
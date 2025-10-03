package knf.kuma.tv.cards

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import knf.kuma.R
import knf.kuma.commons.loadGlide
import knf.kuma.database.CacheDB
import knf.kuma.pojos.AnimeObject
import knf.kuma.tv.BindableCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.find

class ChapterCardView(context: Context) : BindableCardView<AnimeObject.WebInfo.AnimeChapter>(context) {

    override val imageView: ImageView
        get() = find(R.id.img)
    override val layoutResource: Int
        get() = R.layout.item_tv_card_chapter_preview

    override fun bind(data: AnimeObject.WebInfo.AnimeChapter) {
        imageView.loadGlide(data.img)
        GlobalScope.launch(Dispatchers.Main) {
            find<View>(R.id.indicator).visibility = if (withContext(Dispatchers.IO) { CacheDB.INSTANCE.seenDAO().chapterIsSeen(data.aid, data.number) }) VISIBLE else GONE
        }
        find<TextView>(R.id.chapter).text = data.number
    }
}

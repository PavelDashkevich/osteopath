package by.dashkevichpavel.osteopath.features.customerlist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.dashkevichpavel.osteopath.R

class SpaceItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = parent.context.resources.getDimension(R.dimen.card_view_default_space).toInt()
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = outRect.bottom
        }
    }
}
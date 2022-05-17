package com.jyodroid.tvseries.ui.series

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SeriesGridItemDecorator(private val space: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = space
        outRect.right = space
        outRect.left = space

        val layoutManager = parent.layoutManager as? GridLayoutManager
        if (layoutManager?.spanCount == 2) {
            if (parent.getChildLayoutPosition(view) % 2 != 0) {
                outRect.left = space / 2
            } else {
                outRect.right = space / 2
            }
        } else if (layoutManager?.spanCount == 3) {
            if (inSequence(parent.getChildLayoutPosition(view), 0)) {
                outRect.left = space / 2
                outRect.right = space / 2
            }
        }
    }

    //Sequence 3n - 2 to get the middle element on a 3 columns grid
    private fun inSequence(number: Int, counter: Int): Boolean {
        val compute = 3 * counter - 2
        return if (compute == number) true
        else if (compute > number) false
        else inSequence(number, counter + 1)
    }
}
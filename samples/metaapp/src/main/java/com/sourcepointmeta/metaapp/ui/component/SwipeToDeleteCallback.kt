package com.sourcepointmeta.metaapp.ui.component

import android.content.Context
import android.graphics.Canvas
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sourcepointmeta.metaapp.R

internal class SwipeToDeleteCallback(
    private val context: Context,
    private val onSwipeCallback: ((position: Int) -> Unit)? = null
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val icon by lazy { ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_black_36) }
    private val background by lazy { R.color.material_on_surface_disabled.toDrawable() }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipeCallback?.invoke(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView: View = viewHolder.itemView
        val backgroundCornerOffset = 20
        when {
            dX > 0 -> { // Swiping to the right
                background.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
            }
            dX < 0 -> { // Swiping to the left
                background.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> { // view is unSwiped
                background.setBounds(0, 0, 0, 0)
            }
        }
        background.draw(c)
        icon?.draw(c)
    }
}

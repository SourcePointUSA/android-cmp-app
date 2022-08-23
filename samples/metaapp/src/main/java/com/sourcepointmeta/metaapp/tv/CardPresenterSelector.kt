package com.sourcepointmeta.metaapp.tv

import android.content.Context
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.sourcepointmeta.metaapp.tv.properties.PropertyViewPresenter

class CardPresenterSelector(private val mContext: Context) : PresenterSelector() {

    enum class Type {
        PROPERTY_LIST
    }

    override fun getPresenter(item: Any): Presenter {
        val bt = item as? BaseItem ?: throw RuntimeException("The used item must be a sub-type of BaseItem!!!")

        return when (bt.type) {
            Type.PROPERTY_LIST -> PropertyViewPresenter(mContext)
            else -> throw RuntimeException("None of the presenter matches the specified type!!!")
        }
    }

    data class BaseItem(val type: Type)
}
